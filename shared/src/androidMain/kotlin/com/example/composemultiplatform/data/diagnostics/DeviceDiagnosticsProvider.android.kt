package com.example.composemultiplatform.data.diagnostics

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.StandardIntegrityManager
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.NetworkInterface
import java.security.MessageDigest
import java.util.Locale

/**
 * Constructor sin argumentos (exigido por el expect común): el Context se obtiene
 * vía KoinComponent en lugar de recibirlo por constructor. Esto requiere que tu
 * initKoin() ya llame a androidContext(...) — el mismo patrón que ya usa el resto
 * del proyecto para DataStoreFactory / createKSafe.
 */
actual class PlatformDeviceDiagnosticsProvider actual constructor() : DeviceDiagnosticsProvider, KoinComponent {

    private val context: Context by inject()

    // --- 1) País real ------------------------------------------------------

    override suspend fun collectCountrySignal(): CountrySignal {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        // simCountryIso: país de la SIM/eSIM física. null si no hay SIM (wifi-only, tablet, etc).
        val simIso = tm?.simCountryIso?.takeIf { it.isNotBlank() }?.uppercase()

        // networkCountryIso: país de la red celular ACTUAL (torre a la que está pegado ahora).
        // Útil para detectar roaming; también puede ser vacío si no hay señal celular.
        val networkIso = tm?.networkCountryIso?.takeIf { it.isNotBlank() }?.uppercase()

        // locale: lo que el usuario configuró a mano — la señal "mentirosa" que ya conocías.
        val localeIso = Locale.getDefault().country.takeIf { it.isNotBlank() }

        val best = simIso ?: networkIso ?: localeIso
        val source = when {
            simIso != null -> "sim"
            networkIso != null -> "network"
            localeIso != null -> "locale"
            else -> "unknown"
        }

        return CountrySignal(
            simCountryIso = simIso,
            networkCountryIso = networkIso,
            localeCountry = localeIso,
            bestGuessCountry = best,
            source = source,
        )
    }

    // --- 2) Red / IP para trazabilidad --------------------------------------

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override suspend fun collectNetworkSignal(): NetworkSignal {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork
        val caps = activeNetwork?.let { cm.getNetworkCapabilities(it) }

        val connectionType = when {
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WIFI"
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "CELLULAR"
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "ETHERNET"
            caps == null -> "NONE"
            else -> "UNKNOWN"
        }

        val localIp = getLocalIpAddress()
        val ssid = getSsidIfPermissionGranted()
        val carrierName = (context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)
            ?.networkOperatorName?.takeIf { it.isNotBlank() }

        // Huella de red sin requerir SSID: combina tipo de conexión + IP local + operador.
        // Sirve igual para detectar "cambié de red" aunque no haya permiso de ubicación.
        val rawId = listOfNotNull(connectionType, ssid ?: localIp, carrierName).joinToString("|")
        val fingerprint = sha256Hex(rawId)

        return NetworkSignal(
            connectionType = connectionType,
            localIp = localIp,
            publicIp = null, // resuélvela en tu backend leyendo la IP de origen del request
            ssid = ssid,
            carrierName = carrierName,
            networkFingerprint = fingerprint,
            isNewNetworkSinceLastCheck = null, // compáralo en el repositorio contra el valor guardado (DataStore/KSafe ya en tu proyecto)
        )
    }

    private fun getLocalIpAddress(): String? = try {
        NetworkInterface.getNetworkInterfaces()?.asSequence()
            ?.flatMap { it.inetAddresses.asSequence() }
            ?.firstOrNull { !it.isLoopbackAddress && it.hostAddress?.contains(':') == false }
            ?.hostAddress
    } catch (_: Exception) {
        null
    }

    /**
     * Requiere ACCESS_FINE_LOCATION otorgado por el usuario (restricción del SO desde
     * Android 8, endurecida en 10+). Si no está concedido, devuelve null en vez de
     * pedirlo aquí — pídelo tú donde tenga sentido en tu UX, este módulo nunca dispara
     * diálogos de permiso por su cuenta.
     */
    private fun getSsidIfPermissionGranted(): String? {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) return null

        return try {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            wm?.connectionInfo?.ssid
                ?.trim('"')
                ?.takeIf { it.isNotBlank() && it != "<unknown ssid>" }
        } catch (_: Exception) {
            null
        }
    }

    // --- 3) Fingerprint / atestación (Play Integrity) -----------------------

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun collectDeviceFingerprint(
        cloudProjectNumber: Long?,
        serverChallenge: ByteArray?,
    ): DeviceFingerprint {
        return try {
            val manager = IntegrityManagerFactory.createStandard(context)

            val prepareRequest = StandardIntegrityManager.PrepareIntegrityTokenRequest.builder()
                .setCloudProjectNumber(cloudProjectNumber ?: 0L)
                .build()

            val tokenProvider = manager.prepareIntegrityToken(prepareRequest).await()

            // requestHash: en producción debe derivarse de un desafío/nonce que emite TU
            // backend (por ejemplo, hash del payload que vas a enviar). Aquí se usa un
            // fallback local solo para que el demo funcione sin backend todavía.
            val requestHash = serverChallenge?.let { sha256Base64(it) }
                ?: sha256Base64(System.currentTimeMillis().toString().toByteArray())

            val tokenRequest = StandardIntegrityManager.StandardIntegrityTokenRequest.builder()
                .setRequestHash(requestHash)
                .build()

            val tokenResponse = tokenProvider.request(tokenRequest).await()

            DeviceFingerprint(
                platform = "android",
                attestationToken = tokenResponse.token(),
            )
        } catch (e: Exception) {
            // En emuladores sin Play Store / sin Google Play Services actualizado, esto
            // suele caer aquí con errores como API_NOT_AVAILABLE, PLAY_STORE_NOT_FOUND o
            // PLAY_SERVICES_NOT_FOUND. Ese mensaje crudo es justo lo que pediste ver.
            DeviceFingerprint(
                platform = "android",
                attestationError = "${e::class.simpleName}: ${e.message}",
                emulatorHintFromClientError = e.message?.let {
                    it.contains("NOT_FOUND", ignoreCase = true) ||
                            it.contains("NOT_AVAILABLE", ignoreCase = true) ||
                            it.contains("emulator", ignoreCase = true)
                },
            )
        }
    }

    private fun sha256Hex(input: String): String =
        MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sha256Base64(input: ByteArray): String =
        java.util.Base64.getEncoder().encodeToString(
            MessageDigest.getInstance("SHA-256").digest(input),
        )
}