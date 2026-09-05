package com.example.composemultiplatform.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.annotation.Single
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@Single
actual class DataStoreFactory {
    @OptIn(ExperimentalForeignApi::class)
    actual fun create(): DataStore<Preferences> = createDataStore(
        producePath = {
            val dir = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory, inDomain = NSUserDomainMask,
                appropriateForURL = null, create = false, error = null
            )
            requireNotNull(dir).path + "/prefs.preferences_pb"
        }
    )
}