package io.github.nuclominus.example.ui.widget

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ColumnScope.ImageWidget(source: Any?, contentDescription: String) {
    GlideImage(
        model = source,
        contentDescription = contentDescription,
        modifier = Modifier.weight(1f),
        contentScale = ContentScale.FillHeight,
    )
}