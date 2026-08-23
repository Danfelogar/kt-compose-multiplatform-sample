package com.example.composemultiplatform.data.network

import com.example.composemultiplatform.data.dto.ProductResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlin.time.Duration.Companion.milliseconds

class ProductsApi(
    private val httpClient: HttpClient
) {

    suspend fun getProducts(
        page: Int = 0,
        pageSize: Int = 10
    ): Result<ProductResponseDto> {
        delay(2000L.milliseconds)
        val body = try {
            val response = httpClient.get(
                urlString = "https://dummyjson.com/products?select=id,title,price"
            ){
                contentType(ContentType.Application.Json)
                parameter("limit", pageSize)
                parameter("skip", page * pageSize)
            }

            response.body<ProductResponseDto>()
        } catch(e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.failure(e)
        }

        return Result.success(body)
    }
}