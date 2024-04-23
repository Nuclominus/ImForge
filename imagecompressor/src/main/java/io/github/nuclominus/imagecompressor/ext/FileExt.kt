package io.github.nuclominus.imagecompressor.ext

import android.content.Context
import androidx.core.net.toUri
import io.github.nuclominus.imagecompressor.ImageOptimizer
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
    configuration: ImageOptimizer.Configuration = ImageOptimizer.Configuration(),
    flip: Boolean = false,
): File =
    ImageOptimizer.optimize(
        context = context,
        imageUri = toUri(),
        configuration = configuration,
        flip = flip,
    ) ?: this