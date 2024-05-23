package io.github.nuclominus.imforge.app.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.nuclominus.imforge.app.ui.viewmodel.DetailsViewModel
import io.github.nuclominus.imforge.app.ui.widget.ImageDetailsWidget

@Composable
internal fun DetailsScreen(modelId: String) {

    val viewModel =
        hiltViewModel<DetailsViewModel, DetailsViewModel.DetailsViewModelFactory> { factory ->
            factory.create(modelId)
        }

    val data by viewModel.model.collectAsStateWithLifecycle(null)

    Box {
        data?.let { model ->
            ImageDetailsWidget(
                details = model.entity
            )
        }
    }
}