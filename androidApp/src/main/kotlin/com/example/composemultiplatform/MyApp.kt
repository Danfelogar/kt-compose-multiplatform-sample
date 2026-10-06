package com.example.composemultiplatform

import android.app.Application
import android.util.Log
import com.chottulink.lib.ChottuLink
import com.example.composemultiplatform.chottulink.ChottuLinkPilot
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
        initChottuLink()
    }

    private fun initChottuLink() {
        val apiKey = BuildConfig.CHOTTULINK_API_KEY
        if (!BuildConfig.CHOTTULINK_CONFIGURED || apiKey.isBlank()) {
            Log.w(TAG, "ChottuLink no inicializado: completa CHOTTULINK_API_KEY y CHOTTULINK_DOMAIN en local.properties")
            return
        }
        ChottuLink.init(this, apiKey)
        ChottuLinkPilot.markConfigured(BuildConfig.CHOTTULINK_DOMAIN)
    }

    private companion object {
        const val TAG = "ChottuLink"
    }
}