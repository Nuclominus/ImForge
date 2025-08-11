package io.github.nuclominus.imforge.app.core.usecase

import android.net.Uri
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import io.github.nuclominus.imforge.app.WorkerConstants
import io.github.nuclominus.imforge.app.core.workmanager.worker.CreateThumbnailWorker
import io.github.nuclominus.imforge.app.core.workmanager.worker.OptimizeImageWorker
import io.github.nuclominus.imforge.app.ext.toJson
import io.github.nuclominus.imagecompressor.ImageOptimizer
import java.util.UUID
import javax.inject.Inject

class OptimizeImageUseCase @Inject constructor(private val workManager: WorkManager) {

    operator fun invoke(uri: Uri?, config: ImageOptimizer.Configuration) {

        val uuid = UUID.randomUUID()

        val startWork = OneTimeWorkRequestBuilder<CreateThumbnailWorker>()
            .setInputData(
                workDataOf(
                    WorkerConstants.KEY_ENTITY_ID to uuid.toString(),
                    WorkerConstants.KEY_CONTENT_URI to uri?.toString(),
                    WorkerConstants.KEY_COMPRESSING_CONFIG to config.toJson()
                )
            )
            .setConstraints(Constraints(requiresStorageNotLow = true))
            .build()

        workManager.beginUniqueWork(
            uuid.toString(),
            ExistingWorkPolicy.REPLACE,
            startWork
        ).then(
            OneTimeWorkRequestBuilder<OptimizeImageWorker>().build()
        ).enqueue()
    }
}