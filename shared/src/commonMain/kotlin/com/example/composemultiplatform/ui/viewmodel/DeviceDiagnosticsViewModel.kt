package com.example.composemultiplatform.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composemultiplatform.data.diagnostics.DeviceDiagnostics
import com.example.composemultiplatform.di.DeviceDiagnosticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.annotation.KoinViewModel

sealed interface DiagnosticsUiState {
    data object Loading : DiagnosticsUiState
    data class Loaded(val diagnostics: DeviceDiagnostics, val prettyJson: String) : DiagnosticsUiState
    data class Error(val message: String) : DiagnosticsUiState
}
@KoinViewModel
class DeviceDiagnosticsViewModel(
    private val repository: DeviceDiagnosticsRepository,
) : ViewModel() {

    private val json = Json { prettyPrint = true; encodeDefaults = true }

    private val _uiState = MutableStateFlow<DiagnosticsUiState>(DiagnosticsUiState.Loading)
    val uiState: StateFlow<DiagnosticsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = DiagnosticsUiState.Loading
        viewModelScope.launch {
            runCatching {
                // TODO producción: pide cloudProjectNumber y serverChallenge a tu backend
                // antes de llamar refresh(), en vez de dejarlos null.
                repository.refresh(cloudProjectNumber = null, serverChallenge = null)
            }.onSuccess { diagnostics ->
                _uiState.value = DiagnosticsUiState.Loaded(
                    diagnostics = diagnostics,
                    prettyJson = json.encodeToString(diagnostics),
                )
            }.onFailure { e ->
                _uiState.value = DiagnosticsUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}