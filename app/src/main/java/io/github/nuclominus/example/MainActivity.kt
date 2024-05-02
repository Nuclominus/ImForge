package io.github.nuclominus.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.nuclominus.example.ext.deleteLocalCache
import io.github.nuclominus.example.ui.theme.ImageCompressorTheme
import io.github.nuclominus.example.ui.widget.ImageIdleWidget
import io.github.nuclominus.example.ui.widget.ImageProcessingWidget
import io.github.nuclominus.example.viewmodel.ImageOptimizingScreen
import io.github.nuclominus.example.viewmodel.ListViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deleteLocalCache()

        setContent {
            ImageCompressorTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()

                BackHandler(true) {
                    if (navController.backQueue.isEmpty()) {
                        finish()
                    } else {
                        navController.popBackStack()
                    }
                }

                ImageComparingWidget(navController = navController)
            }
        }
    }
}

@Composable
fun BoxScope.ImageComparingWidget(
    viewModel: ListViewModel = hiltViewModel(),
    navController: NavHostController
) {
    Navigation(navController = navController)

    val screen by viewModel.imageState.collectAsState()
    navController.navigate(screen.route.routeName)
}

@Composable
fun BoxScope.Navigation(
    navController: NavHostController
) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = ImageOptimizingScreen.IDLE.routeName,
    ) {
        composable(ImageOptimizingScreen.IDLE.routeName) {
            ImageIdleWidget()
        }
        composable(ImageOptimizingScreen.SUCCESS.routeName) {
            ImageProcessingWidget()
        }
    }
}