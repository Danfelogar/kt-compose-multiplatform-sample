package com.example.composemultiplatform.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.composemultiplatform.data.UserProfile
import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.compose.mutableStateOf

class SecureViewModel(
    ksafe: KSafe
): ViewModel() {

    var counter by ksafe.mutableStateOf(0)
        private set

    var userProfile by ksafe.mutableStateOf(
        UserProfile(
            email = "",
            password = "",
        )
    )
        private set

    fun incrementCounter() {
        counter++
    }

    fun updateProfile(email: String, password: String) {
        userProfile = UserProfile(
            email = email,
            password = password
        )
    }
}