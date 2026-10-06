package com.example.composemultiplatform.chottulink

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Estado compartido del piloto. Android escribe aquí lo que devuelve
 * [com.chottulink.lib.ChottuLink.getAppLinkData]; la pantalla solo lo muestra.
 *
 * El valor que se lee es el query param [PARAM] del enlace que se abrió
 * (el short link con el parámetro agregado al compartirlo).
 */
object ChottuLinkPilot {
    const val PARAM = "code"

    private val _state = MutableStateFlow(ChottuLinkPilotUiState())
    val state: StateFlow<ChottuLinkPilotUiState> = _state.asStateFlow()

    fun markConfigured(domain: String) {
        _state.update { it.copy(sdkConfigured = true, domain = domain) }
    }

    fun onResolving() {
        _state.update { it.copy(status = ChottuLinkStatus.Resolving, errorMessage = null) }
    }

    fun onNoLink() {
        _state.update {
            it.copy(
                status = ChottuLinkStatus.Empty,
                destinationUrl = null,
                code = null,
                parameters = emptyMap(),
                shortLink = null,
                errorMessage = null,
            )
        }
    }

    fun onLink(
        destinationUrl: String,
        code: String?,
        parameters: Map<String, String>,
        shortLink: String?,
    ) {
        _state.update {
            it.copy(
                status = ChottuLinkStatus.Received,
                destinationUrl = destinationUrl,
                code = code,
                parameters = parameters,
                shortLink = shortLink,
                errorMessage = null,
            )
        }
    }

    fun onError(message: String) {
        _state.update {
            it.copy(status = ChottuLinkStatus.Error, errorMessage = message)
        }
    }
}

enum class ChottuLinkStatus {
    Waiting,
    Resolving,
    Empty,
    Received,
    Error,
}

data class ChottuLinkPilotUiState(
    val sdkConfigured: Boolean = false,
    val domain: String = "",
    val status: ChottuLinkStatus = ChottuLinkStatus.Waiting,
    val destinationUrl: String? = null,
    val code: String? = null,
    val parameters: Map<String, String> = emptyMap(),
    val shortLink: String? = null,
    val errorMessage: String? = null,
)
