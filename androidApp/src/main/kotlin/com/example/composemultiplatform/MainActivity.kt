package com.example.composemultiplatform

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.chottulink.lib.ChottuLink
import com.example.composemultiplatform.chottulink.ChottuLinkPilot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        handleChottuLink(intent)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleChottuLink(intent)
    }

    private fun handleChottuLink(intent: Intent?) {
        if (intent == null || !BuildConfig.CHOTTULINK_CONFIGURED) return
        ChottuLinkPilot.onResolving()
        val openedUri = intent.data
        ChottuLink.getAppLinkData(intent)
            .addOnSuccessListener { result ->
                val destination = result?.link
                val clicked = result?.shortLinkRaw ?: result?.shortLink ?: openedUri
                if (destination == null && clicked == null) {
                    ChottuLinkPilot.onNoLink()
                    return@addOnSuccessListener
                }
                val parameters = linkedMapOf<String, String>()
                destination?.queryParameters()?.let(parameters::putAll)
                clicked?.queryParameters()
                    ?.filterKeys { it != "_ch_incp" }
                    ?.let(parameters::putAll)
                val code = clicked?.getQueryParameter(ChottuLinkPilot.PARAM)
                    ?: destination?.getQueryParameter(ChottuLinkPilot.PARAM)
                Log.i(TAG, "Link recibido: clicked=$clicked destination=$destination code=$code")
                ChottuLinkPilot.onLink(
                    destinationUrl = destination?.toString() ?: clicked.toString(),
                    code = code,
                    parameters = parameters,
                    shortLink = clicked?.toString(),
                )
            }
            .addOnFailureListener { error ->
                val code = openedUri?.getQueryParameter(ChottuLinkPilot.PARAM)
                if (openedUri != null && code != null) {
                    Log.w(TAG, "SDK falló; se usa el enlace que abrió la app", error)
                    ChottuLinkPilot.onLink(
                        destinationUrl = openedUri.toString(),
                        code = code,
                        parameters = mapOf(ChottuLinkPilot.PARAM to code),
                        shortLink = openedUri.toString(),
                    )
                } else {
                    Log.e(TAG, "No se pudo leer el dynamic link", error)
                    ChottuLinkPilot.onError(error.message ?: "No se pudo leer el dynamic link")
                }
            }
    }

    private companion object {
        const val TAG = "ChottuLink"
    }
}

private fun Uri.queryParameters(): Map<String, String> =
    queryParameterNames.associateWith { name -> getQueryParameter(name).orEmpty() }

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}