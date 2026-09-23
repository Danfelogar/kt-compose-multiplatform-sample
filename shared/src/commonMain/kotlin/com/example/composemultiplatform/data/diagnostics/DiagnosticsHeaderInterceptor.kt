package com.example.composemultiplatform.data.diagnostics

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header

/**
 * Plugin de Ktor que agrega los 4 headers custom a TODA request saliente,
 * usando el último snapshot cacheado por DeviceDiagnosticsRepository (no vuelve a
 * disparar Play Integrity / App Attest en cada request, eso sería lento y consumiría
 * cuota; solo re-consulta el fingerprint cuando tú decidas refrescarlo).
 *
 * Uso en tu HttpClientEngineFactory / donde armes el HttpClient:
 *
 *   HttpClient(engine) {
 *       install(DiagnosticsHeadersPlugin) {
 *           headersProvider = { diagnosticsRepository.currentHeaders() }
 *       }
 *       ...
 *   }
 */
class DiagnosticsHeadersPluginConfig {
    var headersProvider: (() -> DiagnosticsHeaders?)? = null
}

val DiagnosticsHeadersPlugin = createClientPlugin("DiagnosticsHeadersPlugin", ::DiagnosticsHeadersPluginConfig) {
    val provider = pluginConfig.headersProvider

    onRequest { request, _ ->
        val headers = provider?.invoke() ?: return@onRequest
        headers.xCodeOrNameCountry?.let { request.header("x-code-or-name-country", it) }
        headers.xIp?.let { request.header("x-ip", it) }
        headers.xNetwork?.let { request.header("x-network", it) }
        headers.xFingerprintDevice?.let { request.header("x-fingerprint-device", it) }
    }
}
