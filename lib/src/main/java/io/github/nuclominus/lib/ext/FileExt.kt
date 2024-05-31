package io.github.nuclominus.lib.ext

import android.content.Context
import androidx.core.net.toUri
import io.github.nuclominus.lib.Configuration
import io.github.nuclominus.lib.ImageOptimizer
import java.io.File

/**
 * Optimize the image/bitmap file.
 *
 * @param context The context.
 * @param configuration The configuration.
 * @param flip Whether to flip/mirror the image.
 * @return The optimized image file.
 */
fun File.optimize(
    context: Context,
    configuration: Configuration = Configuration(),
    flip: Boolean = false,
): File =
    ImageOptimizer.optimize(
        context = context,
        imageUri = toUri(),
        configuration = configuration,
        flip = flip,
    ) ?: this