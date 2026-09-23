package com.example.composemultiplatform.data.diagnostics

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Señal de país "real" del dispositivo.
 *
 * IMPORTANTE: el locale (idioma/región configurado en ajustes) es la señal MÁS DÉBIL,
 * porque el usuario la puede cambiar libremente sin importar dónde esté físicamente.
 * Por eso NUNCA se usa como fuente principal, solo como último fallback.
 *
 * Prioridad real de confianza (de más a menos confiable, sin pedir permisos):
 *  1. simCountryIso   -> país de la tarjeta SIM física (difícil de falsear, pero null si no hay SIM/eSIM o el user está en wifi-only)
 *  2. networkCountryIso -> país de la torre celular a la que está conectado AHORA (cambia si el user viaja/hace roaming; en iOS ya casi no está disponible por restricciones de Apple)
 *  3. localeCountry   -> ajuste de idioma/región (NO confiable, es justo lo que ya sabías que se podía mentir)
 *
 * Para blindarlo de verdad, cruza `bestGuessCountry` en tu backend contra la geolocalización
 * por IP pública (el campo `publicIp` de [NetworkSignal]) usando un servicio de IP-geolocation
 * en el servidor. Eso sí es prácticamente imposible de falsear sin una VPN.
 */
@Serializable
data class CountrySignal(
    val simCountryIso: String? = null,
    val networkCountryIso: String? = null,
    val localeCountry: String? = null,
    val bestGuessCountry: String? = null,
    val source: String, // "sim" | "network" | "locale" | "unknown"
)

/**
 * Señal de red/IP para trazabilidad (detectar cambio de red).
 *
 * `ssid` solo se llena si el usuario ya otorgó permiso de ubicación (requisito del SO,
 * no se puede evitar). Si no hay permiso, queda null y el módulo sigue funcionando
 * usando `connectionType` + `localIp`/`publicIp` como huella de red alternativa.
 *
 * `publicIp` queda null en el cliente: hay que resolverla en tu backend (el propio
 * request HTTP ya trae la IP pública real en el servidor, no hace falta pedirla al
 * cliente ni usar servicios externos de terceros).
 */
@Serializable
data class NetworkSignal(
    val connectionType: String, // "WIFI" | "CELLULAR" | "ETHERNET" | "NONE" | "UNKNOWN"
    val localIp: String? = null,
    val publicIp: String? = null, // se resuelve en backend a partir del request, ver notas
    val ssid: String? = null,     // null si no hay permiso de ubicación otorgado
    val carrierName: String? = null,
    val networkFingerprint: String, // hash estable para comparar "misma red" entre sesiones
    val isNewNetworkSinceLastCheck: Boolean? = null,
)

/**
 * Resultado de la atestación de integridad del dispositivo.
 *
 * `attestationToken` es un blob opaco (JWS en Android, CBOR/base64 en iOS) que SOLO tu
 * backend puede verificar contra los servidores de Google/Apple. El cliente NUNCA puede
 * decidir por sí mismo "soy real" o "soy un emulador": solo puede reportar el token, o el
 * error crudo que el SO le devolvió al intentar generarlo (muy útil justamente para ver
 * qué pasa en emuladores, que es lo que pediste).
 */
@Serializable
data class DeviceFingerprint(
    val platform: String, // "android" | "ios"
    val attestationToken: String? = null,
    val attestationError: String? = null,
    val emulatorHintFromClientError: Boolean? = null,
    val keyId: String? = null, // solo iOS (App Attest key id, se guarda para futuras atestaciones)
)

@Serializable
data class DeviceDiagnostics(
    val country: CountrySignal,
    val network: NetworkSignal,
    val fingerprint: DeviceFingerprint,
)

/**
 * Representación en forma de headers custom, tal como los vas a mandar en cada
 * request HTTP (ver DiagnosticsHeaderInterceptor.kt para el plugin de Ktor).
 */
@Serializable
data class DiagnosticsHeaders(
    @SerialName("x-code-or-name-country") val xCodeOrNameCountry: String?,
    @SerialName("x-ip") val xIp: String?,
    @SerialName("x-network") val xNetwork: String?,
    @SerialName("x-fingerprint-device") val xFingerprintDevice: String?,
)

fun DeviceDiagnostics.toHeaders(): DiagnosticsHeaders = DiagnosticsHeaders(
    xCodeOrNameCountry = country.bestGuessCountry,
    xIp = network.localIp ?: network.publicIp,
    xNetwork = network.networkFingerprint,
    xFingerprintDevice = fingerprint.attestationToken ?: fingerprint.attestationError,
)