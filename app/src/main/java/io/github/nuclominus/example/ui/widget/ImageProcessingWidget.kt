package io.github.nuclominus.example.ui.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import io.github.nuclominus.example.MainActivity
import io.github.nuclominus.example.viewmodel.ImageOptimizingState
import io.github.nuclominus.example.viewmodel.ListViewModel

@Composable
fun ImageProcessingWidget() {
    val viewModel: ListViewModel = hiltViewModel(LocalContext.current as MainActivity)
    val state by viewModel.imageState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize()
    ) {
        val original =
            when (state) {
                is ImageOptimizingState.Success -> (state as ImageOptimizingState.Success).original
                else -> null
            }

        val optimized =
            when (state) {
                is ImageOptimizingState.Success -> (state as ImageOptimizingState.Success).optimized
                else -> null
            }

        if (original != null) {
            ImageWidget(original, "Original Image")
        }

        if (optimized != null) {
            ImageWidget(optimized, "Optimized Image")
        }
    }
}