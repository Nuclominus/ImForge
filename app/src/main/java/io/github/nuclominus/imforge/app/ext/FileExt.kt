package io.github.nuclominus.imforge.app.ext

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import io.github.nuclominus.imforge.app.FileConstants
import io.github.nuclominus.imagecompressor.ImageOptimizer
import io.github.nuclominus.imagecompressor.ext.optimize
import java.io.File
import java.util.Locale

internal fun Context.createThumbnail(original: File): File {
    val thumb = original.optimize(
        context = this,
        configuration = ImageOptimizer.Configuration(
            compressFormat = Bitmap.CompressFormat.JPEG,
            quality = 10,
        )
    )

    val thumbName =
        "${original.nameWithoutExtension}${FileConstants.THUMB_SUFFIX}.${thumb.extension}"
    thumb.renameTo(File(thumb, thumbName))

    return thumb
}

internal fun Context.deleteLocalCache() {
    val fileOrDirectory = cacheDir
    if (fileOrDirectory.isDirectory)
        fileOrDirectory.listFiles()?.forEach { it.delete() }
    fileOrDirectory.delete()
}

internal fun Context.copyFile(uri: Uri): File? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val mimeType = contentResolver.getType(uri)
        val suffix = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        contentResolver.openInputStream(uri)?.use { input ->
            return File(cacheDir, "raw_${System.currentTimeMillis()}.$suffix").also {
                it.createNewFile()
                it.outputStream()
                    .use { output ->
                        input.copyTo(output)
                    }
            }

        }
    } else {
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            val name = cursor.getString(nameIndex)
            val file = File(cacheDir, name)
            contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            return file
        }
    }

    return null
}

internal fun Long.formatSize(): String {
    if (this < 1024) return "$this B"
    val z = (63 - countLeadingZeroBits()) / 10
    return String.format(
        Locale.getDefault(),
        "%.1f %sB",
        this.toDouble() / (1L shl (z * 10).coerceAtLeast(1)),
        " KMGTPE"[z]
    )
}