package com.example.composemultiplatform.data.diagnostics

/**
 * Contrato común. La implementación real vive en androidMain/iosMain (expect/actual),
 * igual que ya hacen HttpClientEngineFactory / createKSafe en tu proyecto.
 */
interface DeviceDiagnosticsProvider {
    suspend fun collectCountrySignal(): CountrySignal
    suspend fun collectNetworkSignal(): NetworkSignal

    /**
     * @param cloudProjectNumber (Android) el "Cloud project number" de Google Cloud vinculado
     *   a tu app en Play Console > App Integrity. En iOS se ignora.
     * @param serverChallenge nonce/challenge que DEBE generar tu backend (no lo generes
     *   fijo en el cliente en producción, eso invalida la protección contra replay attacks).
     *   Aquí se acepta como parámetro para que lo inyectes desde tu capa de red.
     */
    suspend fun collectDeviceFingerprint(
        cloudProjectNumber: Long? = null,
        serverChallenge: ByteArray? = null,
    ): DeviceFingerprint

    suspend fun collectAll(
        cloudProjectNumber: Long? = null,
        serverChallenge: ByteArray? = null,
    ): DeviceDiagnostics {
        return DeviceDiagnostics(
            country = collectCountrySignal(),
            network = collectNetworkSignal(),
            fingerprint = collectDeviceFingerprint(cloudProjectNumber, serverChallenge),
        )
    }
}

// Constructor sin argumentos a propósito: así la firma es idéntica en Android e iOS.
// La implementación Android obtiene el Context internamente vía Koin (KoinComponent),
// en vez de recibirlo por constructor, para no romper el expect/actual.
expect class PlatformDeviceDiagnosticsProvider() : DeviceDiagnosticsProvider