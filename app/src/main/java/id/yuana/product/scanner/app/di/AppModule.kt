package id.yuana.product.scanner.app.di

import id.yuana.product.scanner.app.data.model.FirebaseAIConfig
import id.yuana.product.scanner.app.integration.firebase.FirebaseAI
import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

object AppModule {
    val firebaseAiConfig: FirebaseAIConfig by lazy { FirebaseAIConfig() }

    val firebaseAI: FirebaseAI by lazy { FirebaseAI(firebaseAiConfig) }
}