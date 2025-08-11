package io.github.nuclominus.imforge.app.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.nuclominus.imforge.app.ext.formatSize
import io.github.nuclominus.imforge.app.ui.model.OptimizedImageModel
import io.github.nuclominus.imforge.app.ui.viewmodel.DetailsViewModel
import io.github.nuclominus.imforge.app.ui.widget.ImageDetailsWidget
import io.github.nuclominus.imforge.app.ui.widget.Tag

@Composable
internal fun DetailsScreen(modelId: String) {

    val viewModel =
        hiltViewModel<DetailsViewModel, DetailsViewModel.DetailsViewModelFactory> { factory ->
            factory.create(modelId)
        }

    val data by viewModel.model.collectAsStateWithLifecycle(null)

    Column {
        data?.let { model ->
            val details = model.entity

            ImageDetailsWidget(
                modifier = Modifier.weight(1f),
                isOriginal = true,
                details = details
            ) {
                DetailsTags(
                    image = it,
                    isOriginal = true,
                    model = model
                )
            }

            HorizontalDivider(
                modifier = Modifier.height(5.dp)
            )

            ImageDetailsWidget(
                modifier = Modifier.weight(1f),
                details = details
            ) {
                DetailsTags(
                    image = it,
                    model = model
                )
            }
        }
    }
}

@Composable
internal fun DetailsTags(
    image: ImageBitmap,
    isOriginal: Boolean = false,
    model: OptimizedImageModel
) {
    val entity = model.entity

    // Tag with image size
    Tag(title = "${image.width}x${image.height}")
    // Tag with compression percentage
    Tag(title = if (isOriginal) "100%" else "${entity.compression}%")
    // Tag with compressed size
    Tag(title = (if (isOriginal) entity.originSize else entity.compressedSize).formatSize())
    // Tag with image mime type
    if (isOriginal) {
        Tag(title = entity.mimeType.uppercase())
    } else {
        model.config.compressFormat.toBitmapCompressFormat()
            ?.let { format ->
                Tag(title = format.name)
            }
    }
}

/**
 * Extension function to convert an integer to a corresponding Bitmap.CompressFormat.
 *
 * This function uses the ordinal of the Bitmap.CompressFormat enum entries to match with the input integer.
 * If a match is found, it returns the corresponding Bitmap.CompressFormat. If no match is found, it returns null.
 *
 * @receiver The integer to be converted to Bitmap.CompressFormat.
 * @return The corresponding Bitmap.CompressFormat if a match is found, null otherwise.
 */
fun Int.toBitmapCompressFormat(): Bitmap.CompressFormat? =
    Bitmap.CompressFormat.entries.find {
        it.ordinal == this
    }