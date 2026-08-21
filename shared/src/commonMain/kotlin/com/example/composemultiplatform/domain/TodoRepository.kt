package com.example.composemultiplatform.domain

interface TodoRepository {
    suspend fun getTodos(): List<String>
}