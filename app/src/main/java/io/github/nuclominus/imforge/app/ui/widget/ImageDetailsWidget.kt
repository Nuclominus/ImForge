package io.github.nuclominus.imforge.app.ui.widget

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.github.nuclominus.imforge.app.core.database.entity.ImageDetailsEntity

@Composable
fun BoxScope.ImageDetailsWidget(
    modifier: Modifier = Modifier,
    details: ImageDetailsEntity,
    tags: @Composable () -> Unit = {},
) {
    val imageBitmap by remember(details) {
        mutableStateOf(
            BitmapFactory.decodeFile(details.compressedPath ?: details.thumbPath)
                .asImageBitmap()
        )
    }

    val imageAspectRatio by remember(details) {
        mutableFloatStateOf(imageBitmap.width.toFloat() / imageBitmap.height.toFloat())
    }

    Image(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .aspectRatio(imageAspectRatio)
        ),
        bitmap = imageBitmap,
        contentDescription = "Image ${details.id}",
        contentScale = ContentScale.Fit,
    )

    Row(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tags()
    }
}