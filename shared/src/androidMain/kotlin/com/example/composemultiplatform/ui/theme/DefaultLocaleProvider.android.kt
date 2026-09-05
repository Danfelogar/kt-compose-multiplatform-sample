package com.example.composemultiplatform.ui.theme

import java.util.Locale

actual fun getDefaultLocale(): String {
    return Locale.getDefault().toString()
}