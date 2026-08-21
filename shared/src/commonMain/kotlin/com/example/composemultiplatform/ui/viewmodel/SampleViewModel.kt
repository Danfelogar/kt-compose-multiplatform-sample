package com.example.composemultiplatform.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SampleViewModel {
    private val _text = MutableStateFlow("Hello World")
    val text: StateFlow<String> = _text.asStateFlow()

    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter.asStateFlow()

    fun updateText(newText: String) {
        _text.value = newText
    }

    fun incrementCounter() {
        _counter.value++
    }
}