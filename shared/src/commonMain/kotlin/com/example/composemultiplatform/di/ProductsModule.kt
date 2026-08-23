package com.example.composemultiplatform.di

import com.example.composemultiplatform.data.network.ProductsApi
import com.example.composemultiplatform.ui.viewmodel.ProductsViewModel
import io.ktor.client.HttpClient
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class ProductsModule {

    @Single
    fun provideProductsApi(
        @NoAuthHttpClient httpClient: HttpClient
    ): ProductsApi = ProductsApi(httpClient)

    @Factory
    fun provideProductsViewModel(api: ProductsApi): ProductsViewModel = ProductsViewModel(api)
}