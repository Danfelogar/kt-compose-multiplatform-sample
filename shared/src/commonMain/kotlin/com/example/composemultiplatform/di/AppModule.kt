package com.example.composemultiplatform.di
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.composemultiplatform.data.DataStoreFactory
import com.example.composemultiplatform.data.HttpClientEngineFactory
import com.example.composemultiplatform.data.createDataStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single


@Module
@ComponentScan("com.example.composemultiplatform.data")
class AppModule {

    @Single
    @AuthHttpClient
    fun authHttpClient(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
//           config with authentication
        }
    }

    @Single
    @NoAuthHttpClient
    fun noAuthHttpClient(engine: HttpClientEngine): HttpClient{
        return HttpClient(engine) {
            install(Logging ) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    @Single
    fun dataStore(factory: DataStoreFactory): DataStore<Preferences> = factory.create()

    @Factory
    fun httpClientEngine(): HttpClientEngine = HttpClientEngineFactory().getHttpEngine()
}

@Named
annotation class AuthHttpClient

@Named
annotation class NoAuthHttpClient