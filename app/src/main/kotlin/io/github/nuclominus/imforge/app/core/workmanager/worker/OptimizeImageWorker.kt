package io.github.nuclominus.imforge.app.core.workmanager.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.nuclominus.imforge.app.WorkerConstants
import io.github.nuclominus.imforge.app.core.database.AppDataBase
import io.github.nuclominus.imforge.app.core.notification.NotificationManager.createForegroundInfo
import io.github.nuclominus.imforge.app.ext.buildResultFailureWith
import io.github.nuclominus.imforge.app.ext.toObject
import io.github.nuclominus.imagecompressor.ImageOptimizer
import io.github.nuclominus.imagecompressor.ext.optimize
import java.io.File

@HiltWorker
class OptimizeImageWorker @AssistedInject constructor(
    private val db: AppDataBase,
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = with(db) {
        val originalEntityId: String = inputData.getString(WorkerConstants.KEY_ENTITY_ID)
            ?: return buildResultFailureWith("Uri is empty")

        setForeground(applicationContext.createForegroundInfo(originalEntityId))

        val config = inputData.getString(WorkerConstants.KEY_COMPRESSING_CONFIG)
            ?.toObject<ImageOptimizer.Configuration>()
            ?: return buildResultFailureWith("Configuration is empty")

        val entity = imageDetailsDao().getById(originalEntityId)

        runCatching {
            val originalFile = File(entity.originalPath)

            val compressedFile = originalFile.optimize(
                context = applicationContext,
                configuration = config
            )

            val updatedEntity = entity.copy(
                compressedPath = compressedFile.absolutePath,
                compressedSize = compressedFile.length(),
            )

            imageDetailsDao().insertOrReplace(updatedEntity)
        }.onFailure {
            buildResultFailureWith("Failed to optimize image")
        }

        return Result.success()
    }

}