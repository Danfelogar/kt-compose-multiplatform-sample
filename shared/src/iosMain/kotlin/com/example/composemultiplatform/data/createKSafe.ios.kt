package com.example.composemultiplatform.data

import eu.anifantakis.lib.ksafe.KSafe

private val ksafe by lazy { KSafe() }

actual fun createKSafe(): KSafe {
    return ksafe
}