@file:OptIn(ExperimentalLayoutApi::class)

package io.github.nuclominus.imforge.app.ui.widget

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.github.nuclominus.imforge.app.core.database.entity.ImageDetailsEntity

@Composable
fun ImageDetailsWidget(
    modifier: Modifier = Modifier,
    isOriginal: Boolean = false,
    details: ImageDetailsEntity,
    tags: @Composable (ImageBitmap) -> Unit = {},
) {
    val imageBitmap by remember(details) {
        mutableStateOf(
            BitmapFactory.decodeFile(
                if (isOriginal) details.originalPath
                else details.compressedPath ?: details.thumbPath
            ).asImageBitmap()
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            bitmap = imageBitmap,
            contentDescription = "Image ${details.id}",
            contentScale = ContentScale.Crop,
        )

        FlowRow(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tags(imageBitmap)
        }
    }
}