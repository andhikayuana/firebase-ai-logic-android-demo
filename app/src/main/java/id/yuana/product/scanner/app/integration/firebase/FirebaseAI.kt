package id.yuana.product.scanner.app.integration.firebase

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import id.yuana.product.scanner.app.data.model.FirebaseAIConfig
import id.yuana.product.scanner.app.data.model.ProductInfo
import id.yuana.product.scanner.app.di.json

class FirebaseAI(
    private val config: FirebaseAIConfig
) {
    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(
            modelName = config.model,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = config.outputJsonSchema
            },
            systemInstruction = content("system") {
                text(config.systemPrompt)
            }
        )

    suspend fun fetchInfoFromImage(
        image: Bitmap
    ): ProductInfo? {
        val currentTime = System.currentTimeMillis()

//        val (maxWidth, maxHeight) = configV2.maxResolution

        val prompt = content {
//            image(image.resizeToMaxSize(maxWidth, maxHeight).compressToWebp())
            image(image)
            text(config.userPrompt)

        }
        val response = model.generateContent(prompt)
        val imageAnalysisResponse = response.text?.let {
            json.decodeFromString<ProductInfo>(it)
        }

        val duration = System.currentTimeMillis() - currentTime
        Log.d("FirebaseAI", "FirebaseAI response: $imageAnalysisResponse")
        Log.d("FirebaseAI", "FirebaseAI response duration: ${duration}ms")

        return imageAnalysisResponse
    }
}