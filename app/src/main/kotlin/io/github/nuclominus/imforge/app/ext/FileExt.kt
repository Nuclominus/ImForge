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
import kotlin.math.log10
import kotlin.math.pow

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

/**
 * Extension function to copy a file from a given Uri to the application's cache directory.
 *
 * This function first determines the name of the file to be created. If the Android version is Q or above,
 * it generates a new name using the current timestamp and the file extension derived from the mime type of the Uri.
 * If the Android version is below Q, it retrieves the display name of the file from the Uri.
 *
 * After determining the file name, it creates a new file in the cache directory with that name.
 * It then opens an input stream from the Uri and an output stream to the new file, and copies the data from the input
 * stream to the output stream. The newly created file is then returned.
 *
 * If any step fails (e.g. the Uri does not exist, or the file cannot be created), the function returns null.
 *
 * @receiver The context in which the function is called.
 * @param uri The Uri of the file to be copied.
 * @return The newly created file in the cache directory, or null if the operation failed.
 */
internal fun Context.copyFile(uri: Uri): File? {
    val name = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val mimeType = contentResolver.getType(uri)
        val suffix = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        "raw_${System.currentTimeMillis()}.$suffix"
    } else {
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    }

    return name?.let {
        val file = File(cacheDir, it)
        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file
    }
}

/**
 * Extension function to format a Long value into a human-readable file size string.
 *
 * This function converts the size from bytes to the most appropriate unit (B, KB, MB, GB, TB, PB, EB),
 * depending on the size of the file. The conversion is done by using logarithmic operations to calculate
 * the index for the units array. This approach is more straightforward and easier to understand for those
 * not familiar with bitwise operations.
 *
 * The function first checks if the size is less than 1024 bytes. If so, it simply appends "B" to the size
 * and returns the result. If the size is 1024 bytes or more, it calculates the number of digit groups by
 * taking the logarithm base 10 of the size and dividing it by the logarithm base 10 of 1024. It then formats
 * the size as a string with one decimal place, followed by the appropriate unit.
 *
 * @receiver The size in bytes to be formatted.
 * @return The formatted size string.
 */
private const val KILOBYTE = 1024.0

internal fun Long.formatSize(): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB")
    val digitGroups = (log10(this.toDouble()) / log10(KILOBYTE)).toInt()
    return "%.1f %s".format(this / KILOBYTE.pow(digitGroups.toDouble()), units[digitGroups])
}