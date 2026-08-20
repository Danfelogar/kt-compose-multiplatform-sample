package com.example.composemultiplatform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform