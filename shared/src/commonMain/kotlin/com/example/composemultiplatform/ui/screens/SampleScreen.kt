package com.example.composemultiplatform.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composemultiplatform.nativeComponents.NativeButton
import com.example.composemultiplatform.viewmodel.SampleViewModel

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