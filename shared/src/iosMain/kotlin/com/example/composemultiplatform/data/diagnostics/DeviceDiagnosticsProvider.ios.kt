package com.example.composemultiplatform.data.diagnostics

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreTelephony.CTTelephonyNetworkInfo
import platform.DeviceCheck.DCAppAttestService
import platform.Foundation.NSData
import platform.Foundation.NSLocale
import platform.Foundation.NSProcessInfo
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.countryCode
import platform.Foundation.create
import platform.Foundation.currentLocale
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
actual class PlatformDeviceDiagnosticsProvider actual constructor() : DeviceDiagnosticsProvider {

    // --- 1) País real ------------------------------------------------------

    override suspend fun collectCountrySignal(): CountrySignal {
        // OJO: desde iOS 16, Apple restringió fuertemente CTCarrier por privacidad —
        // en la práctica, en dispositivos con eSIM/dual-SIM modernos suele devolver un
        // valor genérico o null, ya NO es una señal confiable como sí lo es en Android.
        // Apple lo hizo así a propósito para evitar exactamente este tipo de fingerprinting
        // "silencioso" fuera de sus APIs oficiales de atestación.
        val simIso = try {
            val info = CTTelephonyNetworkInfo()
            @Suppress("DEPRECATION")
            info.subscriberCellularProvider?.isoCountryCode?.takeIf { it.isNotBlank() }?.uppercase()
        } catch (_: Throwable) {
            null
        }

        val localeIso = NSLocale.currentLocale.countryCode?.takeIf { it.isNotBlank() }

        val best = simIso ?: localeIso
        val source = when {
            simIso != null -> "sim"
            localeIso != null -> "locale"
            else -> "unknown"
        }

        return CountrySignal(
            simCountryIso = simIso,
            networkCountryIso = null, // no expuesto de forma confiable en iOS moderno
            localeCountry = localeIso,
            bestGuessCountry = best,
            source = source,
        )
    }

    // --- 2) Red / IP para trazabilidad --------------------------------------

    override suspend fun collectNetworkSignal(): NetworkSignal {
        val connectionType = currentConnectionType()

        // SSID: en iOS requiere el entitlement "Access WiFi Information" + permiso de
        // ubicación otorgado (restricción de Apple, no se puede evitar). Sin eso, la API
        // devuelve null directamente — este módulo no intenta rodear esa restricción.
        val ssid: String? = null // ver notas de integración para habilitarlo si aceptas pedir el permiso

        val fingerprintSource = listOfNotNull(connectionType, NSProcessInfo.processInfo.globallyUniqueString)
            .joinToString("|")

        return NetworkSignal(
            connectionType = connectionType,
            localIp = null, // en iOS moderno obtener la IP local confiablemente requiere
            // getifaddrs vía cinterop adicional; pídemelo si lo necesitas
            publicIp = null, // resuélvela en tu backend a partir del request entrante
            ssid = ssid,
            carrierName = null,
            networkFingerprint = fingerprintSource.hashCode().toString(),
            isNewNetworkSinceLastCheck = null,
        )
    }

    private suspend fun currentConnectionType(): String = suspendCancellableCoroutine { cont ->
        val monitor = nw_path_monitor_create()
        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
        nw_path_monitor_set_update_handler(monitor) { path ->
            val status = nw_path_get_status(path)
            val type = if (status == nw_path_status_satisfied) "CONNECTED" else "NONE"
            if (cont.isActive) cont.resume(type)
        }
        nw_path_monitor_start(monitor)
    }

    // --- 3) Fingerprint / atestación (App Attest) ---------------------------

    override suspend fun collectDeviceFingerprint(
        cloudProjectNumber: Long?,
        serverChallenge: ByteArray?,
    ): DeviceFingerprint {
        val service = DCAppAttestService.sharedService

        if (!service.isSupported()) {
            // Esto es justo lo que pasa en el Simulator de Xcode: isSupported() da false.
            // En dispositivo físico real, da true.
            return DeviceFingerprint(
                platform = "ios",
                attestationError = "DCAppAttestService no soportado (típico en el Simulator de Xcode)",
                emulatorHintFromClientError = true,
            )
        }

        return try {
            val keyId = suspendCancellableCoroutine<String> { cont ->
                service.generateKeyWithCompletionHandler { keyId, error ->
                    if (error != null) {
                        cont.resumeWithException(RuntimeException(error.localizedDescription))
                    } else {
                        cont.resume(keyId ?: "")
                    }
                }
            }

            // clientDataHash: en producción debe ser SHA-256 de un challenge emitido por TU
            // backend (para prevenir replay attacks). Aquí, si no llega serverChallenge, se
            // usa un valor local solo para que el flujo compile/corra en demo sin backend.
            val challengeBytes = serverChallenge ?: "demo-challenge-reemplazar-por-backend".encodeToByteArray()
            val clientDataHash = sha256(challengeBytes)

            val attestationData = suspendCancellableCoroutine<NSData> { cont ->
                service.attestKey(keyId, clientDataHash) { data, error ->
                    if (error != null) {
                        cont.resumeWithException(RuntimeException(error.localizedDescription))
                    } else if (data != null) {
                        cont.resume(data)
                    } else {
                        cont.resumeWithException(RuntimeException("attestKey devolvió data null sin error"))
                    }
                }
            }

            DeviceFingerprint(
                platform = "ios",
                attestationToken = attestationData.base64EncodedStringWithOptions(0u),
                keyId = keyId,
            )
        } catch (e: Throwable) {
            DeviceFingerprint(
                platform = "ios",
                attestationError = e.message ?: "Error desconocido en App Attest",
                emulatorHintFromClientError = e.message?.contains("simulator", ignoreCase = true) == true,
            )
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun sha256(input: ByteArray): NSData {
        // Requiere la dependencia KMP `org.kotlincrypto.hash:sha2` (agregada arriba en el toml).
        val digest = org.kotlincrypto.hash.sha2.SHA256().digest(input)
        return digest.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = digest.size.toULong())
        }
    }
}