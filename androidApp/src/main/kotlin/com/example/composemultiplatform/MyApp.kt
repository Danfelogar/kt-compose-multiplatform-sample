package com.example.composemultiplatform

import android.app.Application
import com.example.composemultiplatform.data.AppModule
import org.koin.android.ext.koin.androidContext
import com.example.composemultiplatform.di.initKoin

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        AppModule.init(this)
        initKoin {
            androidContext(this@MyApp)
        }
    }
}