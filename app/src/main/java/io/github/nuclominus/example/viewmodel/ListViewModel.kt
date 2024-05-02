package io.github.nuclominus.example.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.nuclominus.example.ext.copyFile
import io.github.nuclominus.imagecompressor.ImageOptimizer
import io.github.nuclominus.imagecompressor.ext.optimize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(): ViewModel() {

    private val _imageState = MutableStateFlow<ImageOptimizingState>(ImageOptimizingState.Idle)
    val imageState = _imageState.asStateFlow()

    fun optimizeImage(context: Context?, uri: Uri?) = CoroutineScope(Dispatchers.IO).launch {
        runCatching {

            if (context == null) throw IllegalStateException("Context is null")
            if (uri == null || uri == Uri.EMPTY) throw IllegalStateException("Uri is empty")

            val originalFile =
                context.copyFile(uri) ?: throw IllegalStateException("Failed to copy image")

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
            Log.e(TAG, "Failed to optimize image: ${it.message}", it)
            updateState(ImageOptimizingState.Idle)
        }
    }

    private fun updateState(state: ImageOptimizingState) = CoroutineScope(Dispatchers.IO).launch {
        _imageState.emit(state)
    }

    companion object {
        private const val TAG = "ListViewModel"
    }
}

sealed class ImageOptimizingState(val route: ImageOptimizingScreen) {
    data object Idle : ImageOptimizingState(ImageOptimizingScreen.IDLE)
    data class Success(val original: File, val optimized: File) : ImageOptimizingState(ImageOptimizingScreen.SUCCESS)
}

enum class ImageOptimizingScreen(val routeName: String) {
    IDLE("idle"),
    SUCCESS("Success")
}