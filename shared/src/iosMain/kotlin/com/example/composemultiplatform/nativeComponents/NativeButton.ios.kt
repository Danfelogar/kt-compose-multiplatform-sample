package com.example.composemultiplatform.nativeComponents

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import com.example.composemultiplatform.LocalNativeViewFactory

@Composable
actual fun NativeButton(onClick: () -> Unit, modifier: Modifier) {
    val factory = LocalNativeViewFactory.current
    UIKitViewController(
        modifier = modifier,
        factory =  {
            factory.createButtonView(
                label = "IOS Button",
                onClick = onClick
            )
        }
    )
}