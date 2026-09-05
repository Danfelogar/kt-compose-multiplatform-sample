package com.example.composemultiplatform.ui.components

import com.example.composemultiplatform.shared.presentation.components.CardBalanced
import composemultiplatform.shared.generated.resources.subtract_available
import composemultiplatform.shared.generated.resources.subtract_people_amount
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import composemultiplatform.shared.generated.resources.Res
import composemultiplatform.shared.generated.resources.subtract_business
import composemultiplatform.shared.generated.resources.subtract_people
import org.jetbrains.compose.resources.painterResource
import kotlin.math.pow

/** Qué cuenta está seleccionada actualmente en el AccountTypeCard. */
enum class AccountTab { PERSONA, NEGOCIO }

/**
 * Card de tipo de cuenta con selección dinámica Persona / Negocio.
 *
 * Al cambiar de tab:
 *  - La barra rosada se traslada (anima su posición x) hasta quedar debajo
 *    del tab activo. La posición de cada tab se mide en runtime con
 *    `onGloballyPositioned`, así que no depende de anchos hardcodeados.
 *  - La card principal (Z1) hace crossfade entre `CardBalanced` (persona,
 *    morada) y `CardBalancedBusiness` (negocio, blanca).
 *  - El panel que asoma detrás (Z0.5) también hace crossfade:
 *      · Persona activo -> asoma "Disponible Negocio" (subtract_available,
 *        blanco) en la esquina inferior derecha.
 *      · Negocio activo -> asoma "Disponible Anthony" (subtract_people_amount,
 *        degradado morado/negro) en la esquina inferior izquierda.
 */
@Composable
fun AccountTypeCard(
    accountNumber: String,
    amount: Double,
    businessAccountNumber: String,
    businessAmount: Double,
    modifier: Modifier = Modifier,
    personLabel: String = "Disponible Anthony",
    personCurrency: String = "TP",
    businessAccountLabel: String = "Disponible Negocio",
    businessCurrency: String = "TP",
    onCopyAccount: (String) -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(AccountTab.PERSONA) }
    val density = LocalDensity.current

    // Posiciones (en px, relativas al Box contenedor) de cada barra indicadora,
    // medidas en runtime para que la animación funcione sin importar el ancho
    // real de "Persona" / "Negocio".
    var containerRootX by remember { mutableStateOf(0f) }
    var personaBarX by remember { mutableStateOf(0f) }
    var negocioBarX by remember { mutableStateOf(0f) }

    val animatedBarX by animateDpAsState(
        targetValue = with(density) {
            (if (selectedTab == AccountTab.PERSONA) personaBarX else negocioBarX).toDp()
        },
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "tabIndicatorTranslate"
    )

    Box(
        modifier = modifier
            .widthIn(min = 310.dp)
            .height(230.dp)
            .onGloballyPositioned { containerRootX = it.positionInRoot().x }
    ) {
        // ---------- Z3: blob blanco "Negocio", el más atrás de todo ----------
        Image(
            painter = painterResource(Res.drawable.subtract_business),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 5.dp, y = (-10).dp)
                .width(180.dp)
                .height(100.dp)
                .zIndex(-1f)
        )

        // ---------- blob morado "Persona", detrás de la barra y la card ----------
        Image(
            painter = painterResource(Res.drawable.subtract_people),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-5).dp, y = (-10).dp)
                .width(180.dp)
                .height(100.dp)
                .zIndex(0f)
        )

        // ---------- Panel "Disponible Negocio": asoma cuando Persona está activo ----------
        AnimatedVisibility(
            visible = selectedTab == AccountTab.PERSONA,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 5.dp, y = 14.dp)
                .zIndex(0.5f),
            enter = fadeIn(tween(250)) + slideInHorizontally(tween(250)) { it / 4 },
            exit = fadeOut(tween(180)) + slideOutHorizontally(tween(180)) { it / 4 }
        ) {
            BusinessPeekPanel(
                label = businessAccountLabel,
                amount = businessAmount,
                currency = businessCurrency
            )
        }

        // ---------- Panel "Disponible Anthony": asoma cuando Negocio está activo ----------
        AnimatedVisibility(
            visible = selectedTab == AccountTab.NEGOCIO,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-5).dp, y = 14.dp)
                .zIndex(0.5f),
            enter = fadeIn(tween(250)) + slideInHorizontally(tween(250)) { -it / 4 },
            exit = fadeOut(tween(180)) + slideOutHorizontally(tween(180)) { -it / 4 }
        ) {
            PersonaPeekPanel(
                label = personLabel,
                amount = amount,
                currency = personCurrency
            )
        }

        Column(
            modifier = Modifier.zIndex(1f),
            horizontalAlignment = Alignment.Start,
        ) {
            // ---------- Fila de tabs ----------
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 22.dp, top = 10.dp, end = 22.dp, bottom = 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // --- Tab "Persona" ---
                    val personaInteraction = remember { MutableInteractionSource() }
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.clickable(
                            interactionSource = personaInteraction,
                            indication = null
                        ) { selectedTab = AccountTab.PERSONA }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Persona",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Persona",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        // Placeholder invisible: solo reserva el espacio y
                        // reporta su posición. La barra visible real es la
                        // que está más abajo (animatedIndicatorBar).
                        Box(
                            modifier = Modifier
                                .width(96.dp)
                                .height(3.dp)
                                .onGloballyPositioned {
                                    personaBarX = it.positionInRoot().x - containerRootX
                                }
                        )
                    }

                    // --- Tab "Negocio" ---
                    val negocioInteraction = remember { MutableInteractionSource() }
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.clickable(
                            interactionSource = negocioInteraction,
                            indication = null
                        ) { selectedTab = AccountTab.NEGOCIO }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Business,
                                contentDescription = "Negocio",
                                tint = Color(0xFF45158A),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Negocio",
                                color = Color(0xFF45158A),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .width(96.dp)
                                .height(3.dp)
                                .onGloballyPositioned {
                                    negocioBarX = it.positionInRoot().x - containerRootX
                                }
                        )
                    }
                }

                // ---------- Z2: barra indicadora animada (la que realmente se ve) ----------
                Box(
                    modifier = Modifier
                        .offset(x = animatedBarX, y = 36.dp)
                        .width(96.dp)
                        .height(3.dp)
                        .background(
                            color = Color(0xFFDB97FF),
                            shape = RoundedCornerShape(
                                topStart = 3.6.dp,
                                topEnd = 3.6.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                )
            }

            // ---------- Z1: card principal, con crossfade Persona <-> Negocio ----------
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    (fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 8 })
                        .togetherWith(fadeOut(tween(180)) + slideOutVertically(tween(180)) { -it / 8 })
                },
                label = "mainCardSwap"
            ) { tab ->
                when (tab) {
                    AccountTab.PERSONA -> CardBalanced(
                        accountNumber = accountNumber,
                        amount = amount,
                        currency = personCurrency,
                        onCopyAccount = onCopyAccount
                    )
                    AccountTab.NEGOCIO -> CardBalancedBusiness(
                        accountNumber = businessAccountNumber,
                        amount = businessAmount,
                        currency = businessCurrency,
                        onCopyAccount = onCopyAccount
                    )
                }
            }
        }
    }
}

/** Panel blanco "Disponible Negocio" (usa subtract_available.xml). */
@Composable
private fun BusinessPeekPanel(label: String, amount: Double, currency: String) {
    Box(modifier = Modifier.width(180.dp).height(100.dp)) {
        Image(
            painter = painterResource(Res.drawable.subtract_available),
            contentDescription = null,
            modifier = Modifier.width(180.dp).height(100.dp)
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 26.dp, top = 42.dp, bottom = 20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = Color(0xFF45158A),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 14.sp
            )
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = formatAmount(amount, 0, '.', ','),
                    color = Color(0xFF45158A),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = currency,
                    color = Color(0xFF45158A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
    }
}

/** Panel degradado morado/negro "Disponible Anthony" (usa subtract_people_amount.xml). */
@Composable
private fun PersonaPeekPanel(label: String, amount: Double, currency: String) {
    Box(modifier = Modifier.width(180.dp).height(100.dp)) {
        Image(
            painter = painterResource(Res.drawable.subtract_people_amount),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.width(180.dp).height(100.dp)
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 26.dp, top = 42.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 14.sp
            )
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = formatAmount(amount, 0, '.', ','),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = currency,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
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
private fun AccountTypeCardPreview() {
    MaterialTheme {
        AccountTypeCard(
            accountNumber = "3004263815",
            amount = 3890800.0,
            businessAccountNumber = "A65413213312",
            businessAmount = 25148520.0
        )
    }
}