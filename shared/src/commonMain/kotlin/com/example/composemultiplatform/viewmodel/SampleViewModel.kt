package com.example.composemultiplatform.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SampleViewModel {
    private val _text = MutableStateFlow("Hello World")
    val text: StateFlow<String> = _text.asStateFlow()

    fun updateText(newText: String) {
        _text.value = newText
    }
}