package id.yuana.product.scanner.app.ui.components.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import id.yuana.product.scanner.app.integration.firebase.FirebaseAI
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CameraViewModel(
    private val firebaseAI: FirebaseAI
) : ViewModel() {

    private val _state = MutableStateFlow<CameraState>(CameraState())
    val state: StateFlow<CameraState> = _state.asStateFlow()

    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _state.update { it.copy(surfaceRequest = newSurfaceRequest) }
        }
    }

    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()


    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        processCameraProvider.bindToLifecycle(
            lifecycleOwner, state.value.selector, cameraPreviewUseCase, imageCapture
        )

        // Cancellation signals we're done with the camera
        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }

    suspend fun onAnalyze(context: Context) {
        _state.update { it.copy(isAnalyzing = true) }

        val image = suspendCoroutine<Bitmap> { continuation ->
            imageCapture.takePicture(
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {

                        try {
                            val matrix = Matrix().apply {
                                postRotate(image.imageInfo.rotationDegrees.toFloat())
                            }
                            val rotatedBitmap = Bitmap.createBitmap(
                                image.toBitmap(),
                                0,
                                0,
                                image.width,
                                image.height,
                                matrix,
                                true
                            )
                            continuation.resume(rotatedBitmap)
                        } catch (e: Exception) {
                            continuation.resumeWithException(e)
                        } finally {
                            image.close()
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {

                        continuation.resumeWithException(exception)
                    }
                }
            )
        }

        val response = firebaseAI.fetchInfoFromImage(image)

        Log.d("CameraViewModel", "FirebaseAI response: $response")

        response?.let {

            _state.update { it.copy(analyzedProductInfo = response) }
        }

        _state.update { it.copy(isAnalyzing = false) }

    }
}