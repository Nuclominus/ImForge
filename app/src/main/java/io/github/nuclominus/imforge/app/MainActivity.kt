package io.github.nuclominus.imforge.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.nuclominus.imforge.app.ui.state.ImageOptimizingScreen
import io.github.nuclominus.imforge.app.ui.theme.ImageCompressorTheme
import io.github.nuclominus.imforge.app.ui.screen.DashboardScreen
import io.github.nuclominus.imforge.app.ui.screen.DetailsScreen
import io.github.nuclominus.imforge.app.ui.viewmodel.DashboardViewModel
import io.github.nuclominus.imforge.app.ImageConstants

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels<DashboardViewModel> {
        defaultViewModelProviderFactory
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        viewModel.processData(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        viewModel.processData(intent)

        setContent {
            ImageCompressorTheme {
                val navController = rememberNavController()

                BackHandler(true) {
                    if (navController.backQueue.size <= 1) {
                        finish()
                    } else {
                        navController.popBackStack()
                    }
                }

                Navigation(navController = navController)


            }
        }
    }
}

@Composable
fun Navigation(
    navController: NavHostController
) {
    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        navController = navController,
        startDestination = ImageOptimizingScreen.LIST.routeName,
    ) {
        composable(ImageOptimizingScreen.LIST.routeName) {
            DashboardScreen {
                navController.navigate("${ImageOptimizingScreen.DETAILS.routeName}/${it}")
            }
        }
        composable(ImageOptimizingScreen.DETAILS.routeName) {
            val modelId = it.arguments?.getString(ImageConstants.IMAGE_MODEL_ID) ?: run {
                navController.popBackStack()
                return@composable
            }
            DetailsScreen(modelId)
        }
    }
}