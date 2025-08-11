package io.github.nuclominus.imforge.app.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.nuclominus.imforge.app.ui.state.SideEffect
import io.github.nuclominus.imforge.app.ui.viewmodel.DashboardViewModel
import io.github.nuclominus.imforge.app.ui.widget.BottomAppBar
import io.github.nuclominus.imforge.app.ui.widget.BottomSheet
import io.github.nuclominus.imforge.app.ui.widget.ImageListItem
import io.github.nuclominus.imforge.app.ui.widget.ResolutionWidget
import io.github.nuclominus.imforge.app.ui.widget.SettingsWidget
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

private const val LIST_SCROLL_OFFSET = 5

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    navigateToDetails: (String) -> Unit
) {
    val contextRef = WeakReference(LocalContext.current)

    var resolutionBottomSheet by rememberSaveable { mutableStateOf(false) }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val galleryResult =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            imageUri = uri
            resolutionBottomSheet = true
        }

    val imageDetails by viewModel.details.collectAsStateWithLifecycle(emptyList())
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var settingsBottomSheet by rememberSaveable { mutableStateOf(false) }
    val settings by viewModel.config.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            BottomAppBar(
//                countFiles = imageDetails.size,
                galleryResult = galleryResult,
                onDeleteCache = viewModel::deleteCache,
                onOpenSettings = { settingsBottomSheet = true }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(
                    items = imageDetails,
                    key = { it.id + it.compressedPath }
                ) { image ->
                    ImageListItem(image) {
                        navigateToDetails(image.id)
                    }
                }
            }
            if (imageDetails.isEmpty()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Press + to add image for compressing"
                )
            }

            if (settingsBottomSheet) {
                BottomSheet(
                    onDismiss = { settingsBottomSheet = false }
                ) {
                    SettingsWidget(
                        configuration = settings,
                        onConfigurationChange = {
                            viewModel.setConfig(it)
                            settingsBottomSheet = false
                        }
                    )
                }
            }

            if (resolutionBottomSheet) {
                imageUri?.let { uri ->


                    BottomSheet(
                        onDismiss = { resolutionBottomSheet = false }
                    ) {
                        val baseResolution = viewModel.getImageResolution(
                            context = contextRef.get(),
                            uri = uri
                        ).getOrNull() ?: run {
                            resolutionBottomSheet = false
                            return@BottomSheet
                        }

                        ResolutionWidget(
                            baseResolution = baseResolution,
                            onSelectedResolution = { resolution ->
                                viewModel.setConfig(
                                    settings.copy(
                                        maxWidth = resolution.first.toFloat(),
                                        maxHeight = resolution.second.toFloat(),
                                    )
                                )
                                viewModel.optimizeImage(imageUri)
                                resolutionBottomSheet = false
                            }
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { sideEffect ->
            when (sideEffect) {
                is SideEffect.ScrollTo -> coroutineScope.launch {
                    listState.animateScrollToItem(sideEffect.position, LIST_SCROLL_OFFSET)
                }

                is SideEffect.ShowResolutionPicker -> {
                    imageUri = sideEffect.uri
                    resolutionBottomSheet = true
                }

                else -> Unit
            }
        }
    }
}