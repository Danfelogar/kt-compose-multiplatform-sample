package com.example.composemultiplatform.di

import com.example.composemultiplatform.data.createKSafe
import com.example.composemultiplatform.ui.viewmodel.SecureViewModel
import eu.anifantakis.lib.ksafe.KSafe
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class SecureModule {

    @Single
    fun provideKSafe(): KSafe = createKSafe()

    @KoinViewModel
    fun secureViewModel(ksafe: KSafe): SecureViewModel = SecureViewModel(ksafe)
}