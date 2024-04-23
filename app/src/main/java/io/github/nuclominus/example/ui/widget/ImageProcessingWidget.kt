package io.github.nuclominus.example.ui.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.nuclominus.example.state.ImageOptimizingState

@Composable
fun BoxScope.ImageProcessingWidget(state: ImageOptimizingState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize()
    ) {
        val original =
            when (state) {
                is ImageOptimizingState.Optimizing -> state.original
                is ImageOptimizingState.Selected -> state.original
                is ImageOptimizingState.Success -> state.original
                else -> null
            }

        val optimized =
            when (state) {
                is ImageOptimizingState.Success -> state.optimized
                else -> null
            }

        if (original != null) {
            ImageWidget(original, "Original Image")
        }

        if (optimized != null) {
            ImageWidget(optimized, "Optimized Image")
        }
    }

    val isOptimizing =
        when (state) {
            is ImageOptimizingState.Optimizing -> true
            else -> false
        }

    if (isOptimizing) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
        )
    }
}