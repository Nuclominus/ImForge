package io.github.nuclominus.imagecompressor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.annotation.RestrictTo
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min

object ImageOptimizer {

    private const val DEFAULT_QUALITY = 90

    /**
     * @param context the application environment
     * @param imageUri the input image uri. usually "content://..."
     * @param configuration the optimisation params
     *
     * @return output image [android.net.Uri]
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun optimize(
        context: Context,
        imageUri: Uri,
        configuration: Configuration,
        flip: Boolean = false,
    ): File? {
        /**
         * Decode uri bitmap from activity result using content provider
         */
        val bmOptions: BitmapFactory.Options = decodeBitmapFromUri(context, imageUri)

        /**
         * Calculate scale factor of the bitmap relative to [maxWidth] and [maxHeight]
         */
        val scaleDownFactor: Float = calculateScaleDownFactor(
            bmOptions, configuration.useMaxScale, configuration.maxWidth, configuration.maxHeight
        )

        /**
         * Since [BitmapFactory.Options.inSampleSize] only accept value with power of 2,
         * we calculate the nearest power of 2 to the previously calculated scaleDownFactor
         * check doc [BitmapFactory.Options.inSampleSize]
         */
        setNearestInSampleSize(bmOptions, scaleDownFactor)

        /**
         * 2 things we do here with image matrix:
         * - Adjust image rotation
         * - Scale image matrix based on remaining [scaleDownFactor / bmOption.inSampleSize]
         */
        val matrix: Matrix = calculateImageMatrix(
            context, imageUri, flip, scaleDownFactor, bmOptions
        ) ?: return null

        /**
         * Create new bitmap based on defined bmOptions and calculated matrix
         */
        val newBitmap: Bitmap = generateNewBitmap(
            context, imageUri, bmOptions, matrix
        ) ?: return null
        val newBitmapWidth = newBitmap.width
        val newBitmapHeight = newBitmap.height

        /**
         * Determine whether to scale up the image or not if the
         * image width and height is below minimum dimension
         */
        val shouldScaleUp: Boolean = shouldScaleUp(
            newBitmapWidth, newBitmapHeight, configuration.minWidth, configuration.minHeight
        )

        /**
         * Calculate the final scaleUpFactor if the image need to be scaled up.
         */
        val scaleUpFactor: Float = calculateScaleUpFactor(
            newBitmapWidth.toFloat(),
            newBitmapHeight.toFloat(),
            configuration.maxWidth,
            configuration.maxHeight,
            configuration.minWidth,
            configuration.minHeight,
            shouldScaleUp
        )

        /**
         * calculate the final width and height based on final scaleUpFactor
         */
        val finalWidth: Int = finalWidth(newBitmapWidth.toFloat(), scaleUpFactor)
        val finalHeight: Int = finalHeight(newBitmapHeight.toFloat(), scaleUpFactor)

        /**
         * Generate the final bitmap, by scaling up if needed
         */
        val finalBitmap: Bitmap = scaleUpBitmapIfNeeded(
            newBitmap, finalWidth, finalHeight, scaleUpFactor, shouldScaleUp
        )

        /**
         * compress and save image
         */
        val imageFilePath: String = compressAndSaveImage(
            bitmap = finalBitmap,
            compressFormat = configuration.compressFormat,
            quality = configuration.quality
        ) ?: return null

        return File(imageFilePath)
    }

    private fun decodeBitmapFromUri(
        context: Context,
        imageUri: Uri
    ): BitmapFactory.Options {
        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        val input: InputStream? = context.contentResolver.openInputStream(imageUri)
        BitmapFactory.decodeStream(input, null, bmOptions)
        input?.close()
        return bmOptions
    }

    private fun calculateScaleDownFactor(
        bmOptions: BitmapFactory.Options,
        useMaxScale: Boolean,
        maxWidth: Float,
        maxHeight: Float
    ): Float {
        val photoW = bmOptions.outWidth.toFloat()
        val photoH = bmOptions.outHeight.toFloat()
        val widthRatio = photoW / maxWidth
        val heightRatio = photoH / maxHeight
        var scaleFactor = if (useMaxScale) {
            max(widthRatio, heightRatio)
        } else {
            min(widthRatio, heightRatio)
        }
        if (scaleFactor < 1) {
            scaleFactor = 1f
        }
        return scaleFactor
    }

    private fun setNearestInSampleSize(
        bmOptions: BitmapFactory.Options,
        scaleFactor: Float
    ) {
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor.toInt()
        if (bmOptions.inSampleSize % 2 != 0) { // check if sample size is divisible by 2
            var sample = 1
            while (sample * 2 < bmOptions.inSampleSize) {
                sample *= 2
            }
            bmOptions.inSampleSize = sample
        }
    }

    private fun calculateImageMatrix(
        context: Context,
        imageUri: Uri,
        flip: Boolean,
        scaleFactor: Float,
        bmOptions: BitmapFactory.Options
    ): Matrix? {
        val input: InputStream = context.contentResolver.openInputStream(imageUri) ?: return null
        val exif = ExifInterface(input)
        val matrix = Matrix()
        val orientation: Int = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(
                90f
            )

            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(
                180f
            )

            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(
                270f
            )

            else -> Unit
        }
        val remainingScaleFactor = scaleFactor / bmOptions.inSampleSize.toFloat()
        if (remainingScaleFactor > 1) {
            matrix.postScale(1.0f / remainingScaleFactor, 1.0f / remainingScaleFactor)
        }

        // Perform matrix rotations/mirrors depending on camera that took the photo
        if (flip) {
            val mirrorY = floatArrayOf(-1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
            val matrixMirrorY = Matrix()
            matrixMirrorY.setValues(mirrorY)
            matrix.postConcat(matrixMirrorY)
        }
        input.close()
        return matrix
    }

    private fun generateNewBitmap(
        context: Context,
        imageUri: Uri,
        bmOptions: BitmapFactory.Options,
        matrix: Matrix
    ): Bitmap? {
        var bitmap: Bitmap? = null
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        try {
            bitmap = BitmapFactory.decodeStream(inputStream, null, bmOptions)
            if (bitmap != null) {
                val matrixScaledBitmap: Bitmap = Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                )
                if (matrixScaledBitmap != bitmap) {
                    bitmap.recycle()
                    bitmap = matrixScaledBitmap
                }
            }
            inputStream?.close()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun shouldScaleUp(
        photoW: Int,
        photoH: Int,
        minWidth: Int,
        minHeight: Int
    ): Boolean {
        return (minWidth != 0 && minHeight != 0 && (photoW < minWidth || photoH < minHeight))
    }

    private fun calculateScaleUpFactor(
        photoW: Float,
        photoH: Float,
        maxWidth: Float,
        maxHeight: Float,
        minWidth: Int,
        minHeight: Int,
        shouldScaleUp: Boolean
    ): Float {
        var scaleUpFactor: Float = max(photoW / maxWidth, photoH / maxHeight)
        if (shouldScaleUp) {
            scaleUpFactor = if (photoW < minWidth && photoH > minHeight) {
                photoW / minWidth
            } else if (photoW > minWidth && photoH < minHeight) {
                photoH / minHeight
            } else {
                max(photoW / minWidth, photoH / minHeight)
            }
        }
        return scaleUpFactor
    }

    private fun finalWidth(
        photoW: Float, scaleUpFactor: Float
    ): Int {
        return (photoW / scaleUpFactor).toInt()
    }

    private fun finalHeight(
        photoH: Float, scaleUpFactor: Float
    ): Int {
        return (photoH / scaleUpFactor).toInt()
    }

    private fun scaleUpBitmapIfNeeded(
        bitmap: Bitmap,
        finalWidth: Int,
        finalHeight: Int,
        scaleUpFactor: Float,
        shouldScaleUp: Boolean
    ): Bitmap {
        val scaledBitmap: Bitmap = if (scaleUpFactor > 1 || shouldScaleUp) {
            Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
        } else {
            bitmap
        }
        if (scaledBitmap != bitmap) {
            bitmap.recycle()
        }
        return scaledBitmap
    }

    private fun compressAndSaveImage(
        bitmap: Bitmap,
        compressFormat: Bitmap.CompressFormat,
        quality: Int,
    ): String? {
        @Suppress("DEPRECATION")
        val suffix = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when (compressFormat) {
                Bitmap.CompressFormat.WEBP_LOSSY,
                Bitmap.CompressFormat.WEBP_LOSSLESS -> ".webp"

                Bitmap.CompressFormat.PNG -> ".png"
                else -> ".jpg"
            }
        } else {
            when (compressFormat) {
                Bitmap.CompressFormat.WEBP -> ".webp"
                Bitmap.CompressFormat.PNG -> ".png"
                else -> ".jpg"
            }
        }
        val imageFile = File.createTempFile("image", suffix)
        val stream = FileOutputStream(imageFile)
        bitmap.compress(compressFormat, quality, stream)
        stream.close()
        bitmap.recycle()
        return imageFile.absolutePath
    }

    /**
     * @param compressFormat the output image file format
     * @param maxWidth the output image max width
     * @param maxHeight the output image max height
     * @param useMaxScale determine whether to use the bigger dimension between [maxWidth] or [maxHeight]
     * @param quality the output image compress quality
     * @param minWidth the output image min width
     * @param minHeight the output image min height
     */
    data class Configuration(
        val compressFormat: Bitmap.CompressFormat = @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP,
        val maxWidth: Float = Float.MAX_VALUE,
        val maxHeight: Float = Float.MAX_VALUE,
        val useMaxScale: Boolean = true,
        val quality: Int = DEFAULT_QUALITY,
        val minWidth: Int = Int.MIN_VALUE,
        val minHeight: Int = Int.MIN_VALUE
    )
}