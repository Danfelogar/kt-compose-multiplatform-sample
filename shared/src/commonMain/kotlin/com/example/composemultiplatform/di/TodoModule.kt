package com.example.composemultiplatform.di

import com.example.composemultiplatform.data.KtorTodoRepository
import com.example.composemultiplatform.domain.TodoRepository
import com.example.composemultiplatform.ui.viewmodel.TodoViewModel
import io.ktor.client.HttpClient
import org.koin.core.annotation.Factory
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class TodoModule {

    @Factory(binds = [TodoRepository::class])
    fun todoRepository(@AuthHttpClient httpClient: HttpClient): TodoRepository = KtorTodoRepository(httpClient)

    @KoinViewModel
    fun todoViewModel(todoRepository: TodoRepository) = TodoViewModel(todoRepository)
}