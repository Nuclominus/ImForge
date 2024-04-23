package io.github.nuclominus.example.state

import java.io.File

sealed class ImageOptimizingState {
    data object Idle : ImageOptimizingState()
    data class Selected(val original: File) : ImageOptimizingState()
    data class Optimizing(val original: File) : ImageOptimizingState()
    data class Success(val original: File, val optimized: File) : ImageOptimizingState()
    data class Error(val message: String) : ImageOptimizingState()
}