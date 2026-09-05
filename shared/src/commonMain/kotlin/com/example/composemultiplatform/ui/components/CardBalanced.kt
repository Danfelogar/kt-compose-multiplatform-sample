package com.example.composemultiplatform.shared.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

@Composable
fun CardBalanced(
    accountNumber: String,
    amount: Double,
    currency: String = "TP",
    label: String = "Tu saldo disponible",
    accountLabel: String = "Cuenta",
    isAmountHideable: Boolean = true,
    initiallyHidden: Boolean = false,
    decimalDigits: Int = 0,
    thousandsSeparator: Char = '.',
    decimalSeparator: Char = ',',
    modifier: Modifier = Modifier,
    onCopyAccount: (String) -> Unit = {},
) {
    var isHidden by remember { mutableStateOf(isAmountHideable && initiallyHidden) }

    Column(
        modifier = modifier
            .widthIn(min = 310.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF45158A), Color(0xFF000000))
                )
            )
            .padding(horizontal = 38.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Fila superior: etiqueta + toggle de visibilidad
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
            if (isAmountHideable) {
                IconButton(
                    onClick = { isHidden = !isHidden },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = if (isHidden) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (isHidden) "Mostrar saldo" else "Ocultar saldo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Monto + moneda
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = if (isHidden) "••••••" else formatAmount(
                    amount = amount,
                    decimalDigits = decimalDigits,
                    thousandsSeparator = thousandsSeparator,
                    decimalSeparator = decimalSeparator
                ),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = currency,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 5.dp)
            )
        }

        // Cuenta + copiar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$accountLabel $accountNumber",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Filled.ContentCopy,
                contentDescription = "Copiar número de cuenta",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier
                    .size(15.dp)
                    .clickable { onCopyAccount(accountNumber) }
            )
        }
    }
}

/** Formatea el monto con separador de miles/decimales personalizables. */
private fun formatAmount(
    amount: Double,
    decimalDigits: Int,
    thousandsSeparator: Char,
    decimalSeparator: Char
): String {
    val factor = 10.0.pow(decimalDigits.toDouble())
    val rounded = kotlin.math.round(amount * factor) / factor

    val integerPart = rounded.toLong()
    val groupedInteger = integerPart.toString()
        .reversed()
        .chunked(3)
        .joinToString(thousandsSeparator.toString())
        .reversed()

    if (decimalDigits <= 0) return groupedInteger

    val decimalPart = kotlin.math.abs(rounded - integerPart)
    val decimalString = (decimalPart * factor).toLong().toString().padStart(decimalDigits, '0')
    return "$groupedInteger$decimalSeparator$decimalString"
}

@Preview
@Composable
private fun CardBalancedPreview() {
    MaterialTheme {
        CardBalanced(
            accountNumber = "3004263815",
            amount = 3890800.0,
            currency = "TP"
        )
    }
}