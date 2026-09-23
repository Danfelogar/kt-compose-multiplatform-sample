package com.example.composemultiplatform.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.composemultiplatform.ui.viewmodel.DeviceDiagnosticsViewModel
import com.example.composemultiplatform.ui.viewmodel.DiagnosticsUiState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DeviceDiagnosticsScreen(
    viewModel: DeviceDiagnosticsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Diagnóstico de dispositivo") })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Estos son los 4 headers custom que se envían en cada request. " +
                        "x-fingerprint-device solo es verificable de verdad en tu backend.",
                style = MaterialTheme.typography.bodySmall,
            )

            when (val s = state) {
                is DiagnosticsUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is DiagnosticsUiState.Error -> {
                    Card(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Error: ${s.message}",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                is DiagnosticsUiState.Loaded -> {
                    HeaderSummaryCard(s)
                    Card {
                        Text(
                            text = s.prettyJson,
                            modifier = Modifier.padding(16.dp),
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            Button(onClick = { viewModel.refresh() }) {
                Text("Volver a consultar")
            }
        }
    }
}

@Composable
private fun HeaderSummaryCard(state: DiagnosticsUiState.Loaded) {
    val d = state.diagnostics
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Headers custom", style = MaterialTheme.typography.titleMedium)
            DiagnosticRow("x-code-or-name-country", d.country.bestGuessCountry ?: "—")
            DiagnosticRow("  ↳ fuente", d.country.source)
            DiagnosticRow("x-ip", d.network.localIp ?: d.network.publicIp ?: "—")
            DiagnosticRow("x-network", d.network.networkFingerprint)
            DiagnosticRow(
                "  ↳ ¿red nueva?",
                d.network.isNewNetworkSinceLastCheck?.toString() ?: "sin dato previo",
            )
            DiagnosticRow(
                "x-fingerprint-device",
                if (d.fingerprint.attestationToken != null) "token OK (${d.fingerprint.attestationToken.take(24)}…)"
                else "ERROR: ${d.fingerprint.attestationError}",
            )
        }
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}