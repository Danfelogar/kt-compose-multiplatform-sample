package com.example.composemultiplatform.di

import com.example.composemultiplatform.data.diagnostics.DeviceDiagnostics
import com.example.composemultiplatform.data.diagnostics.DeviceDiagnosticsProvider
import com.example.composemultiplatform.data.diagnostics.DiagnosticsHeaders
import com.example.composemultiplatform.data.diagnostics.toHeaders
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single
class DeviceDiagnosticsRepository(
    private val provider: DeviceDiagnosticsProvider,
) {
    private val _state = MutableStateFlow<DeviceDiagnostics?>(null)
    val state: StateFlow<DeviceDiagnostics?> = _state.asStateFlow()

    suspend fun refresh(
        cloudProjectNumber: Long? = null,
        serverChallenge: ByteArray? = null,
    ): DeviceDiagnostics {
        val result = provider.collectAll(cloudProjectNumber, serverChallenge)
        _state.value = result
        return result
    }

    fun currentHeaders(): DiagnosticsHeaders? = _state.value?.toHeaders()
}