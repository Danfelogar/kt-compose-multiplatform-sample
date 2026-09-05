package com.example.composemultiplatform.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import java.util.Locale

actual object LocalAppLocale {
    private var defaultLocale: Locale? = null
    actual val current: String
        @Composable
        get() {
            val configuration = LocalConfiguration.current
            return configuration.locales[0].toString()
        }

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current
        if(defaultLocale == null) {
            defaultLocale = LocalLocale.current.platformLocale
        }

        val newLocale = if(value == null) {
            defaultLocale!!
        } else {
            Locale.forLanguageTag(value)
        }

        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)

        val context = LocalContext.current
        context.createConfigurationContext(configuration)
        return LocalConfiguration provides configuration
    }
}