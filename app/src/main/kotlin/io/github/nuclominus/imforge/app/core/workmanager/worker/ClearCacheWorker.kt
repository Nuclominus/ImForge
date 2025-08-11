package io.github.nuclominus.imforge.app.core.workmanager.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.nuclominus.imforge.app.core.database.AppDataBase
import io.github.nuclominus.imforge.app.ext.deleteLocalCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class ClearCacheWorker @AssistedInject constructor(
    private val db: AppDataBase,
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = with(db) {
        applicationContext.deleteLocalCache()
        withContext(Dispatchers.IO) {
            clearAllTables()
        }
        return Result.success()
    }
}