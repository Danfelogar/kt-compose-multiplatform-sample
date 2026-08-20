package com.example.composemultiplatform

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.composemultiplatform.ui.screens.SampleScreen
import com.example.composemultiplatform.ui.theme.GlobalTheme

@Composable
@Preview
fun App() {
    GlobalTheme {
        SampleScreen()
    }
}
