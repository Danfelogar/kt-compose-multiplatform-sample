package com.example.composemultiplatform.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.composemultiplatform.chottulink.ChottuLinkPilot
import com.example.composemultiplatform.chottulink.ChottuLinkStatus

@Composable
fun ChottuLinkPilotScreen() {
    val state by ChottuLinkPilot.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("ChottuLink piloto") })
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
                text = "Abre la app con el dynamic link y el parámetro " +
                    "`${ChottuLinkPilot.PARAM}` agregado al final. Ese valor se muestra aquí.",
                style = MaterialTheme.typography.bodySmall,
            )

            Card {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("Configuración", style = MaterialTheme.typography.titleMedium)
                    ValueRow(
                        "SDK",
                        if (state.sdkConfigured) "inicializado" else "falta API key o dominio en local.properties",
                    )
                    ValueRow("Dominio", state.domain.ifBlank { "sin configurar" })
                    ValueRow("Parámetro", ChottuLinkPilot.PARAM)
                }
            }

            when (state.status) {
                ChottuLinkStatus.Waiting,
                ChottuLinkStatus.Empty,
                -> {
                    Card {
                        Text(
                            text = "Todavía no llegó ningún enlace. " +
                                "Abre https://danfelogar-testing.chottu.link/email-verify?code=PILOTO-123 " +
                                "tocando el link, con la app instalada.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                ChottuLinkStatus.Resolving -> {
                    Card {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            CircularProgressIndicator()
                            Text("Resolviendo el enlace…")
                        }
                    }
                }

                ChottuLinkStatus.Error -> {
                    Card {
                        Text(
                            text = state.errorMessage ?: "No se pudo leer el enlace",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                ChottuLinkStatus.Received -> {
                    Card {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text("Parámetro recibido", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = state.code ?: "El enlace no trae `${ChottuLinkPilot.PARAM}`",
                                style = MaterialTheme.typography.headlineSmall,
                                fontFamily = FontFamily.Monospace,
                            )
                        }
                    }
                    Card {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text("Detalle", style = MaterialTheme.typography.titleMedium)
                            ValueRow("URL de destino", state.destinationUrl ?: "—")
                            ValueRow("Short link", state.shortLink ?: "—")
                            state.parameters.forEach { (name, value) ->
                                ValueRow(name, value)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ValueRow(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
