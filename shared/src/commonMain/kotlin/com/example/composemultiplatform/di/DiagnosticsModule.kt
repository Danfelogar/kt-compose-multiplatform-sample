package com.example.composemultiplatform.di

import com.example.composemultiplatform.data.diagnostics.DeviceDiagnosticsProvider
import com.example.composemultiplatform.data.diagnostics.PlatformDeviceDiagnosticsProvider
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DiagnosticsModule {
    @Single
    fun deviceDiagnosticsProvider(): DeviceDiagnosticsProvider =
        PlatformDeviceDiagnosticsProvider()
}