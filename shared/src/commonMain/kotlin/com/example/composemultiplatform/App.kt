package com.example.composemultiplatform

import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.composemultiplatform.ui.screens.SampleScreen
import com.example.composemultiplatform.ui.screens.TodoScreen
import com.example.composemultiplatform.ui.theme.GlobalTheme

@Composable
@Preview
fun App() {
    GlobalTheme {
//        SampleScreen()
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
        ){ innerPadding ->
            TodoScreen(
                modifier = Modifier
                    .padding(innerPadding)
            )
        }
    }
}
