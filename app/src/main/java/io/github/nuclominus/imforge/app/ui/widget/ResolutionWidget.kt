package io.github.nuclominus.imforge.app.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nuclominus.imforge.app.R
import io.github.nuclominus.imforge.app.ui.theme.Typography

typealias Resolution = Pair<Int, Int>

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
internal fun ResolutionWidget(
    baseResolution: Resolution = 1920 to 1080,
    onSelectedResolution: (Resolution) -> Unit = {}
) {
    val availableResolutions by remember {
        mutableStateOf(generateResolutions(baseResolution))
    }

    var resolution by remember { mutableStateOf(availableResolutions.first()) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ResolutionSelector(
            availableResolutions = availableResolutions,
            selectedResolution = resolution,
            onSelectedResolution = { resolution = it }
        )

        Button(
            onClick = { onSelectedResolution(resolution) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.convert))
        }

        Text(stringResource(R.string.settings_note))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ResolutionSelector(
    availableResolutions: List<Resolution>,
    selectedResolution: Resolution,
    onSelectedResolution: (Resolution) -> Unit
) {
    Text(
        text = stringResource(R.string.select_resolution),
        style = Typography.titleLarge
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        availableResolutions.forEach { resolution ->
            FilterChip(
                label = { Text("${resolution.first} x ${resolution.second}") },
                selected = selectedResolution == resolution,
                onClick = {
                    onSelectedResolution(resolution)
                }
            )
        }
    }
}

/**
 * Generates a list of resolution pairs based on the provided width and height.
 *
 * This function creates a mutable list of pairs of integers, where each pair represents a resolution.
 * The resolutions are generated by scaling down the provided width and height by a percentage, starting from 100% down to 1%,
 * with a step of the provided percent value. This results in a list of resolutions that are smaller or equal to the original size.
 *
 * @param baseResolution The base resolution to generate the list of resolutions from.
 * @param percent The percentage step for scaling down the width and height. Default is 5%.
 * @return A list of pairs of integers, where each pair represents a resolution.
 */
private fun generateResolutions(baseResolution: Resolution, percent: Int = 5) =
    mutableListOf<Pair<Int, Int>>().apply {
        for (i in 100 downTo 1 step percent) {
            add(
                baseResolution.first * i / 100 to baseResolution.second * i / 100
            )
        }
    }.toList()