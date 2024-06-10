package io.github.nuclominus.imforge.app.ui.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import io.github.nuclominus.imforge.app.core.database.entity.ImageDetailsEntity

@Composable
fun ImageListItem(details: ImageDetailsEntity, onClick: () -> Unit) {
    val aspectRatio = details.width / details.height

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .aspectRatio(aspectRatio)
            .animateContentSize()
            .clickable { onClick() }
            .shadow(4.dp),
    ) {

        ImageDetailsWidget(
            details = details
        ) {
            Tag(title = "${details.compression}%")
            Tag(title = details.mimeType.uppercase())
        }
    }
}
