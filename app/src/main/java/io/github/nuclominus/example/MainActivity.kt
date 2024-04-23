package io.github.nuclominus.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import io.github.nuclominus.example.flow.ImageOptimizerFlow
import io.github.nuclominus.example.state.ImageOptimizingState
import io.github.nuclominus.example.ui.theme.ImageCompressorTheme
import io.github.nuclominus.example.ui.widget.ImageIdleWidget
import io.github.nuclominus.example.ui.widget.ImageProcessingWidget

class MainActivity : ComponentActivity() {

    private val optimizerFlow = ImageOptimizerFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(optimizerFlow)

        setContent {
            ImageCompressorTheme {

                val state by optimizerFlow.imageState.collectAsState()

                // A surface container using the 'background' color from the theme
                ImageComparingWidget(state) {
                    optimizerFlow.pickImage()
                }
            }
        }
    }
}

@Composable
fun BoxScope.ImageComparingWidget(
    state: ImageOptimizingState = ImageOptimizingState.Idle,
    pickAction: () -> Unit
) {
    when (state) {
        is ImageOptimizingState.Error -> {
            // show error message
            Toast.makeText(
                LocalContext.current,
                state.message,
                Toast.LENGTH_SHORT
            ).show()
        }

        is ImageOptimizingState.Idle -> {
            // show idle state
            ImageIdleWidget(pickAction)
        }

        else -> {
            ImageProcessingWidget(state)
        }
    }
}