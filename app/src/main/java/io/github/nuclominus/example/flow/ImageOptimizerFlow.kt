package io.github.nuclominus.example.flow

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.github.nuclominus.imagecompressor.ImageOptimizer
import io.github.nuclominus.example.ext.copyFile
import io.github.nuclominus.example.ext.deleteLocalCache
import io.github.nuclominus.imagecompressor.ext.optimize
import io.github.nuclominus.example.state.ImageOptimizingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class ImageOptimizerFlow : DefaultLifecycleObserver {

    private var galleryResult: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private var context: WeakReference<Context>? = null

    private val _imageState = MutableStateFlow<ImageOptimizingState>(ImageOptimizingState.Idle)
    val imageState = _imageState.asStateFlow()

    override fun onCreate(owner: LifecycleOwner) {
        val activity = owner as ComponentActivity
        activity.deleteLocalCache()

        context = WeakReference(activity)

        galleryResult =
            activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                val context = context?.get() ?: return@registerForActivityResult
                if (uri != null) {
                    Toast.makeText(context, "Selected URI: $uri", Toast.LENGTH_SHORT).show()
                    optimizeImage(uri)
                } else {
                    updateState(ImageOptimizingState.Idle)
                    Toast.makeText(context, "No media selected", Toast.LENGTH_SHORT).show()
                }
            }
        super.onCreate(owner)
    }

    fun pickImage() {
        galleryResult?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun optimizeImage(uri: Uri) = CoroutineScope(Dispatchers.IO).launch {
        runCatching {
            val context = context?.get() ?: throw IllegalStateException("Context is null")

            val originalFile =
                context.copyFile(uri) ?: throw IllegalStateException("Failed to copy image")

            updateState(ImageOptimizingState.Selected(originalFile))
            // Separate the optimization process from the main thread
            updateState(ImageOptimizingState.Optimizing(originalFile))

            val optimizedFile = originalFile.optimize(
                context = context,
                configuration = ImageOptimizer.Configuration(
                    quality = 50,
                    maxWidth = Float.MAX_VALUE,
                    maxHeight = Float.MAX_VALUE
                )
            )

            updateState(ImageOptimizingState.Success(originalFile, optimizedFile))
        }.onFailure {
            Log.e("ImageOptimizerFlow", "Failed to optimize image: ${it.message}", it)
            updateState(ImageOptimizingState.Error("Failed to optimize image: ${it.message}"))
            updateState(ImageOptimizingState.Idle)
        }
    }

    private fun updateState(state: ImageOptimizingState) = CoroutineScope(Dispatchers.IO).launch {
        _imageState.emit(state)
    }

}