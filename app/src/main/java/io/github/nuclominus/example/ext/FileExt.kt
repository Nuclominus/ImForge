package io.github.nuclominus.example.ext

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.annotation.RestrictTo
import java.io.File

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal fun Context.deleteLocalCache() {
    val fileOrDirectory = cacheDir
    if (fileOrDirectory.isDirectory)
        fileOrDirectory.listFiles()?.forEach { it.delete() }
    fileOrDirectory.delete()
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
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