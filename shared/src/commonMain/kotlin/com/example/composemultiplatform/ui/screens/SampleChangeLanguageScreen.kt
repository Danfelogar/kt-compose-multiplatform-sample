package com.example.composemultiplatform.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composemultiplatform.ui.theme.LocalAppLocale
import com.example.composemultiplatform.ui.viewmodel.LanguageViewModel
import composemultiplatform.shared.generated.resources.Res
import composemultiplatform.shared.generated.resources.hello_world
import composemultiplatform.shared.generated.resources.select_language
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SampleChangeLanguageScreen() {
    val languageViewModel: LanguageViewModel = koinViewModel()
    val languageCode by languageViewModel.languageCode.collectAsState()
    var dropDownVisible by remember {
        mutableStateOf(false)
    }

    CompositionLocalProvider(LocalAppLocale provides languageCode) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ){
            Text(
                text = stringResource(Res.string.hello_world)
            )
            Box {
                TextButton(
                    onClick = {
                        dropDownVisible = !dropDownVisible
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.select_language)
                    )
                }
            }
            DropdownMenu(
                expanded = dropDownVisible,
                onDismissRequest = {
                    dropDownVisible = false
                }
            ) {
                DropdownMenuItem(
                    text = { Text( text = "English") },
                    onClick = {
                        languageViewModel.switchLanguage("en")
                        dropDownVisible = false
                    }
                )
                DropdownMenuItem(
                    text = { Text( text = "German") },
                    onClick = {
                        languageViewModel.switchLanguage("de")
                        dropDownVisible = false
                    }
                )
            }
        }
    }

}