package com.example.composemultiplatform.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.annotation.Single

@Single
actual class DataStoreFactory(private val context: Context) {
    actual fun create(): DataStore<Preferences> = createDataStore(
        producePath = { context.filesDir.resolve("prefs.preferences_pb").absolutePath }
    )
}