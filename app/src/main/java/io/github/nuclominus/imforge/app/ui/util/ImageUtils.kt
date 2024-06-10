package io.github.nuclominus.imforge.app.ui.util

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi

sealed class BitmapFormats(val format: Bitmap.CompressFormat) {
    data object Jpeg : BitmapFormats(Bitmap.CompressFormat.JPEG)
    data object Png : BitmapFormats(Bitmap.CompressFormat.PNG)
    data object Webp : BitmapFormats(Bitmap.CompressFormat.WEBP)

    @RequiresApi(Build.VERSION_CODES.R)
    data object WebpLossy : BitmapFormats(Bitmap.CompressFormat.WEBP_LOSSY)

    @RequiresApi(Build.VERSION_CODES.R)
    data object WebpLossless : BitmapFormats(Bitmap.CompressFormat.WEBP_LOSSLESS)

    companion object {
        val formats: List<BitmapFormats>
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                listOf(Jpeg, Png, WebpLossy, WebpLossless)
            } else {
                listOf(Jpeg, Png, Webp)
            }
    }

}