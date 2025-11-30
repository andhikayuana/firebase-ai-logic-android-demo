package id.yuana.product.scanner.app.ui.components.camera

import android.Manifest
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import id.yuana.product.scanner.app.data.model.ProductInfo

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {

        LaunchedEffect(lifecycleOwner) {
            viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CameraPreview(state)

//            AnalyzingOverlay(
//                modifier = Modifier.align(Alignment.Center)
//            )

            ProductInfoOverlay(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                productInfo = ProductInfo(),
                onAnalyze = {
                    //todo viewmodel
                }
            )
        }
    } else {
        PermissionInfo(onRequestPermission = {
            cameraPermissionState.launchPermissionRequest()
        })
    }
}

@Composable
private fun CameraPreview(
    state: CameraState
) {
    state.surfaceRequest?.let {
        CameraXViewfinder(
            surfaceRequest = it,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PermissionInfo(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Camera permission is required to scan products",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRequestPermission) {
                Text("Grant Permission")
            }
        }
    }
}

@Composable
private fun AnalyzingOverlay(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Analyzing...",
        modifier = modifier.padding(16.dp),
        style = MaterialTheme.typography.displayMedium.copy(color = Color.LightGray),
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun ProductInfoOverlay(
    modifier: Modifier = Modifier,
    productInfo: ProductInfo,
    onAnalyze: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Name: ${productInfo.name}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Description: ${productInfo.description}",
                style = MaterialTheme.typography.bodySmall
            )
//            Text(
//                text = "Category: ${productInfo.category}",
//                style = MaterialTheme.typography.bodySmall
//            )
//            Text(
//                text = "Confidence: ${(productInfo.confidence * 100).toInt()}%",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.primary
//            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAnalyze,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Analyze Now")
            }
        }
    }
}
