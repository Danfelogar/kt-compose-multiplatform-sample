package com.example.composemultiplatform.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.composemultiplatform.ui.viewmodel.LanguageViewModel
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class LanguageModule {

    @KoinViewModel
    fun languageViewModel(dataStore: DataStore<Preferences>): LanguageViewModel =
        LanguageViewModel(dataStore)
}