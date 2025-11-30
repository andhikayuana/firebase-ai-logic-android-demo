package id.yuana.product.scanner.app.ui.components.camera

import androidx.camera.core.CameraSelector
import androidx.camera.core.SurfaceRequest
import id.yuana.product.scanner.app.data.model.ProductInfo

data class CameraState(
    val surfaceRequest: SurfaceRequest? = null,
    val selector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
//    temp bitmap?
    val analyzedProductInfo: ProductInfo = ProductInfo()
)