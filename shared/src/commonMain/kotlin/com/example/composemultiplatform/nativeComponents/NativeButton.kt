package com.example.composemultiplatform.nativeComponents

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun NativeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)