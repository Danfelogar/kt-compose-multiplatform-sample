package com.example.composemultiplatform.data

import com.example.composemultiplatform.domain.TodoRepository
import io.ktor.client.HttpClient

class KtorTodoRepository(
    private val httpClient: HttpClient
): TodoRepository {

    override suspend fun getTodos(): List<String> {
        return (1..10).map {
            "Todo $it"
        }
    }
}