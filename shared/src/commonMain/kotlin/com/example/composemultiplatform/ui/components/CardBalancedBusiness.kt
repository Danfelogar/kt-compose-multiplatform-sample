package com.example.composemultiplatform.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode2
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import composemultiplatform.shared.generated.resources.Res
import composemultiplatform.shared.generated.resources.subtract_available_card
import org.jetbrains.compose.resources.painterResource
import kotlin.math.pow

/**
 * Variante blanca/gris de CardBalanced para la cuenta de "Negocio".
 *
 * Misma información (label, monto, cuenta, copiar), pero:
 *  - La forma "subtract_available_card.xml" trae una muesca recortada en la
 *    esquina inferior derecha; ese hueco NO se rellena a nivel de todo el
 *    card (eso "estorbaba" cuando el card se usa sobre distintos fondos).
 *    En su lugar, solo el contenedor del ícono QR lleva su propio fondo
 *    (`notchBackgroundColor`) y su propia esquina redondeada, imitando la
 *    curva del SVG en ese punto.
 *  - Textos en color #491496 en vez de blanco.
 *  - Ícono de QR (Material/Google Icons) dentro de ese hueco.
 *
 * Sigue siendo estático: el ojo para ocultar el monto funciona igual que en
 * CardBalanced, pero no hay lógica de tabs ni animaciones todavía.
 */
@Composable
fun CardBalancedBusiness(
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
    // Color que se ve "detrás" del hueco del QR. Debe ser el mismo color
    // que el fondo de la pantalla donde vayas a poner este card, para que
    // se vea como un recorte real y no como un cuadro pegado encima.
    notchBackgroundColor: Color = Color(0xFFE8E8E8),
    onCopyAccount: (String) -> Unit = {},
) {
    var isHidden by remember { mutableStateOf(isAmountHideable && initiallyHidden) }
    val textColor = Color(0xFF491496)

    Box(
        modifier = modifier
            .widthIn(min = 310.dp)
            .aspectRatio(312f / 126f)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(width = 112.dp, height = 92.dp)
//                .padding(bottom = 1.dp, end = 1.dp)
                .background(
                    color = notchBackgroundColor,
                    shape = RoundedCornerShape(bottomEnd = 20.dp)
                )
        )
        // Forma con la muesca. El hueco de la esquina inferior derecha
        // queda transparente: lo que se vea ahí lo define el Box del QR.
        Image(
            painter = painterResource(Res.drawable.subtract_available_card),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .padding(start = 22.dp, top = 12.dp, end = 22.dp, bottom = 12.dp)
                .width(210.dp), // deja espacio libre a la derecha para el QR
            verticalArrangement = Arrangement.spacedBy(6.dp),
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
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    lineHeight = 22.sp
                )
                if (isAmountHideable) {
                    // Icono de ojo sin fondo ni ripple
                    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    Icon(
                        imageVector = if (isHidden) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (isHidden) "Mostrar saldo" else "Ocultar saldo",
                        tint = textColor,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null // Elimina el efecto ripple/fondo
                            ) {
                                isHidden = !isHidden
                            }
                    )
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
                    color = textColor,
                    fontSize = 29.18.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 32.1.sp
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = currency,
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 30.4.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Cuenta + copiar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = accountLabel,
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    lineHeight = 22.sp
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = accountNumber,
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    lineHeight = 22.sp
                )
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "Copiar número de cuenta",
                    tint = textColor,
                    modifier = Modifier
                        .size(15.dp)
                        .clickable { onCopyAccount(accountNumber) }
                )
            }
        }

        Icon(
            imageVector = Icons.Filled.QrCode2,
            contentDescription = "Código QR",
            tint = textColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 22.dp, bottom = 14.dp)
                .size(30.dp)
        )
    }
}

/**
 * Formatea el monto con separador de miles/decimales personalizables.
 *
 * Copia local de la misma lógica que ya está en CardBalanced.kt. Si
 * prefieres no duplicarla, cambia esa función a `internal fun` y borra
 * esta copia para reutilizarla directamente.
 */
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
private fun CardBalancedBusinessPreview() {
    MaterialTheme {
        CardBalancedBusiness(
            accountNumber = "A65413213312",
            amount = 25148520.0
        )
    }
}