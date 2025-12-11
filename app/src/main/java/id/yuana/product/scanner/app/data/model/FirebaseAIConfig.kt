package id.yuana.product.scanner.app.data.model

import com.google.firebase.ai.type.Schema

data class FirebaseAIConfig(
    val model: String = "gemini-2.5-flash-lite",
    val outputJsonSchema: Schema = Schema.obj(
        mapOf(
            "name" to Schema.string(
                description = "The primary product name, including brand and model if identifiable."
            ),
            "description" to Schema.string(
                description = "A concise, factual description of the product's appearance, features, and purpose."
            ),
            "category" to Schema.string(
                description = "Optional: The product category (e.g., electronics, food, clothing)."
            ),
            "confidence" to Schema.float(
                description = "Optional: A confidence score between 0.0 and 1.0 for the recognition accuracy."
            )
        )
    ),
    val systemPrompt: String = """
        You are an AI specialized in product recognition from images for scanner apps.
        Analyze the image to identify the main product, focusing on name, brand, and key details.
        If no product is clearly recognizable, return {"name": "Unknown Product", "description": "Unable to identify product"}.
        Output MUST be a single, valid JSON object matching the schema, with no extra text or formatting.
    """.trimIndent(),

    val userPrompt: String = """
        Scan the image and recognize the product.
        Provide:
        - name: The product's name, including brand if visible.
        - description: A brief description of what the product is, its main features, and usage.
        - category: (Optional) Product type.
        - confidence: (Optional) Your certainty in the recognition (0.0 to 1.0).
    """.trimIndent()
)