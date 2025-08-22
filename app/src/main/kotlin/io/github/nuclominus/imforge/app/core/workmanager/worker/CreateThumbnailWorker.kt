package io.github.nuclominus.imforge.app.core.workmanager.worker

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.nuclominus.imforge.app.WorkerConstants
import io.github.nuclominus.imforge.app.core.database.AppDataBase
import io.github.nuclominus.imforge.app.core.database.entity.ConfigurationEntity
import io.github.nuclominus.imforge.app.core.database.entity.ImageDetailsEntity
import io.github.nuclominus.imforge.app.ext.buildResultFailureWith
import io.github.nuclominus.imforge.app.ext.copyFile
import io.github.nuclominus.imforge.app.ext.createThumbnail
import io.github.nuclominus.imforge.app.ext.toObject
import java.io.File
import java.util.UUID
import androidx.core.net.toUri
import io.github.nuclominus.lib.Configuration

@HiltWorker
class CreateThumbnailWorker @AssistedInject constructor(
    private val db: AppDataBase,
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = with(db) {
        val uuid: String = inputData.getString(WorkerConstants.KEY_ENTITY_ID)
            ?: return buildResultFailureWith("Uri is empty")

        val uri: Uri = inputData.getString(WorkerConstants.KEY_CONTENT_URI)?.toUri()
            ?: return buildResultFailureWith("Uri is empty")

        // Copy original image to cache directory
        val originalFile = applicationContext.copyFile(uri)
            ?: return buildResultFailureWith("Failed to copy image")

        // Get compressing configuration
        val config = inputData.getString(WorkerConstants.KEY_COMPRESSING_CONFIG)
            ?.toObject<Configuration>()
            ?: return buildResultFailureWith("Configuration is empty")

        // Save compressing configuration
        val confEntity = saveConfiguration(config)

        // Create thumbnail
        val thumb = applicationContext.createThumbnail(originalFile)

        // Save image details
        val entity = createImageEntity(
            uuid = uuid,
            file = originalFile,
            thumb = thumb,
            configuration = confEntity,
        )

        return Result.success(
            workDataOf(
                WorkerConstants.KEY_ENTITY_ID to entity.id,
                WorkerConstants.KEY_COMPRESSING_CONFIG to inputData.getString(WorkerConstants.KEY_COMPRESSING_CONFIG)
            )
        )
    }

    context(appData: AppDataBase)
    private fun createImageEntity(
        uuid: String,
        file: File,
        thumb: File,
        configuration: ConfigurationEntity
    ): ImageDetailsEntity {
        val (width, height) = BitmapFactory.decodeStream(file.inputStream())
            .run {
                val width = width.toFloat()
                val height = height.toFloat()
                recycle()
                width to height
            }

        val entity = ImageDetailsEntity(
            id = uuid,
            name = file.nameWithoutExtension,
            width = width,
            height = height,
            compression = configuration.quality,
            originSize = file.length(),
            compressedSize = 0,
            mimeType = file.extension,
            thumbPath = thumb.absolutePath,
            originalPath = file.absolutePath,
            compressedPath = null,
            configurationId = configuration.id,
        )

        appData.imageDetailsDao().insertOrReplace(entity)

        return entity
    }

    context(appData: AppDataBase)
    private fun saveConfiguration(config: Configuration): ConfigurationEntity {
        val configEntity = ConfigurationEntity(
            id = UUID.randomUUID().toString(),
            compressFormat = config.compressFormat.ordinal,
            quality = config.quality,
            maxWidth = config.maxWidth,
            maxHeight = config.maxHeight,
            useMaxScale = config.useMaxScale,
            minWidth = config.minWidth,
            minHeight = config.minHeight
        )

        appData.configurationDao().insert(configEntity)
        return configEntity
    }

}