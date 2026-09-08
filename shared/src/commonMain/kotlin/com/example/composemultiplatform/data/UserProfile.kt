package com.example.composemultiplatform.data

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val email: String,
    val password: String
)