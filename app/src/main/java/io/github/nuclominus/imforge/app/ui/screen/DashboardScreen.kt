package io.github.nuclominus.imforge.app.ui.screen

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.nuclominus.imforge.app.ui.state.SideEffect
import io.github.nuclominus.imforge.app.ui.viewmodel.DashboardViewModel
import io.github.nuclominus.imforge.app.ui.widget.BottomAppBar
import io.github.nuclominus.imforge.app.ui.widget.ImageListItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

private const val LIST_SCROLL_OFFSET = 5

@Composable
fun DashboardScreen(navigateToDetails: (String) -> Unit) {

    val viewModel: DashboardViewModel = hiltViewModel()
    val contextRef = WeakReference(LocalContext.current)
    val galleryResult =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            contextRef.get()?.let {
                viewModel.optimizeImage(uri)
            }
        }

    val imageDetails by viewModel.details.collectAsStateWithLifecycle(emptyList())
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            BottomAppBar(
                countFiles = imageDetails.size,
                galleryResult = galleryResult,
                onDeleteCache = viewModel::deleteCache
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
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { sideEffect ->
            when (sideEffect) {
                is SideEffect.ScrollTo -> coroutineScope.launch {
                    listState.animateScrollToItem(sideEffect.position, LIST_SCROLL_OFFSET)
                }

                else -> Unit
            }
        }
    }
}