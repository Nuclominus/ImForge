package io.github.nuclominus.imforge.app.ui.widget

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.nuclominus.imagecompressor.ImageOptimizer
import io.github.nuclominus.imforge.app.R
import io.github.nuclominus.imforge.app.ui.theme.Typography
import io.github.nuclominus.imforge.app.ui.util.BitmapFormats

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
internal fun SettingsWidget(
    configuration: ImageOptimizer.Configuration = ImageOptimizer.Configuration(),
    onConfigurationChange: (ImageOptimizer.Configuration) -> Unit = {}
) {
    var format by remember { mutableStateOf(configuration.compressFormat) }
    var quality by remember { mutableIntStateOf(configuration.quality) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.settings),
            style = Typography.titleLarge
        )
        DividerBlock()
        QualitySlider(quality) { quality = it }
        DividerBlock()
        FormatSelector(format) { format = it }
        DividerBlock()

        Button(
            onClick = {
                onConfigurationChange(
                    configuration.copy(
                        compressFormat = format,
                        quality = quality
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.save_settings))
        }
    }
}

@Composable
internal fun QualitySlider(
    quality: Int = 50,
    onQualityChange: (Int) -> Unit
) {

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        Row {
            Text(
                text = stringResource(id = R.string.quality),
                style = Typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = quality.toString(),
                style = Typography.titleLarge
            )
        }

        Slider(
            value = quality.toFloat(),
            valueRange = 0f..100f,
            onValueChange = { onQualityChange(it.toInt()) },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FormatSelector(
    imageFormat: Bitmap.CompressFormat,
    selectedFormat: (Bitmap.CompressFormat) -> Unit
) {
    val formats = BitmapFormats.formats

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.select_format),
            style = Typography.titleLarge
        )

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            formats.forEach { format ->
                FilterChip(
                    label = { Text(format.format.name) },
                    selected = format.format == imageFormat,
                    onClick = {
                        selectedFormat(format.format)
                    }
                )
            }
        }
    }
}

@Composable
internal fun DividerBlock(height: Dp = 8.dp) {
    HorizontalDivider(modifier = Modifier.padding(vertical = height))
}