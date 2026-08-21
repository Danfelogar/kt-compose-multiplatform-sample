package com.example.composemultiplatform

import android.app.Application
import org.koin.android.ext.koin.androidContext
import com.example.composemultiplatform.di.initKoin

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MyApp)
        }
    }
}