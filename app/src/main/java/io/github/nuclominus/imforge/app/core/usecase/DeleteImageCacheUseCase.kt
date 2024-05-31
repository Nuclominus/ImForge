package io.github.nuclominus.imforge.app.core.usecase

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.github.nuclominus.imforge.app.core.workmanager.worker.ClearCacheWorker
import javax.inject.Inject

class DeleteImageCacheUseCase @Inject constructor(private val workManager: WorkManager) {

    operator fun invoke() {
        val work = OneTimeWorkRequestBuilder<ClearCacheWorker>().build()
        workManager.enqueue(work)
    }
}