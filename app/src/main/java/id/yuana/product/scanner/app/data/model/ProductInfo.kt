package id.yuana.product.scanner.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductInfo(
    val name: String = "",
    val description: String = "",
    val category: String? = null,
    val confidence: Double = 0.0
)
