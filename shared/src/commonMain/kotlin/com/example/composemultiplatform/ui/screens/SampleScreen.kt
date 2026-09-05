package com.example.composemultiplatform.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composemultiplatform.nativeComponents.NativeButton
import com.example.composemultiplatform.shared.presentation.components.CardBalanced
import com.example.composemultiplatform.ui.components.AccountTypeCard
import com.example.composemultiplatform.ui.components.CardBalancedBusiness
import com.example.composemultiplatform.ui.viewmodel.SampleViewModel

@Composable
fun SampleScreen(viewModel: SampleViewModel = SampleViewModel()) {
    val text by viewModel.text.collectAsState()
    val counter by viewModel.counter.collectAsState()

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .safeContentPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
//            CardBalancedBusiness(
//                accountNumber = "3004 2638 15",// Número de cuenta ficticio
//                amount = 3_890_800.0,// Monto dummy
//                currency = "TP",
//                label = "Tu saldo disponible",
//                accountLabel = "Cuenta",
//                isAmountHideable = true,
//                initiallyHidden = false,
//                decimalDigits = 0,// Sin decimales
//                thousandsSeparator = '.',
//                decimalSeparator = ',',
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 8.dp),
//                onCopyAccount = { accountNumber ->
//                    // Aquí implementas la copia real (ej. con ClipboardManager)
//                    println("Copiado número: $accountNumber")
//                    // viewModel.copyToClipboard(accountNumber)  // si usas ViewModel
//                }
//            )

            // En SampleScreen.kt, dentro del Column

            AccountTypeCard(
                accountNumber = "3004 2638 15",        // tu número personal
                amount = 3_890_800.0,                  // saldo personal
                businessAccountNumber = "A65413213312",// número de negocio
                businessAmount = 2_514_8520.0,         // saldo de negocio
                modifier = Modifier
                    .fillMaxWidth()
//                    .width(321.dp)
                    .padding(horizontal = 8.dp),
                personLabel = "Disponible Anthony",    // (opcional) ya es el valor por defecto
                personCurrency = "TP",                 // (opcional)
                businessAccountLabel = "Disponible Negocio",
                businessCurrency = "TP",
                onCopyAccount = { accountNumber ->
                    // Aquí implementas la copia al portapapeles
                    println("Copiado número: $accountNumber")
                    // viewModel.copyToClipboard(accountNumber)  // si usas ViewModel
                }
            )

            Spacer(modifier = Modifier.height(16.dp))


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.updateText("Hello form MVVM")
                }
            ) {
                Text(text = "Change Text")
            }
            Text(text = "Counter: $counter")
            NativeButton(
                onClick = { viewModel.incrementCounter() }
            )
        }
    }
}