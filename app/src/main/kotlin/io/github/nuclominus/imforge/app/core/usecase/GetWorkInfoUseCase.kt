package io.github.nuclominus.imforge.app.core.usecase

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.UUID
import javax.inject.Inject

class GetWorkInfoUseCase @Inject constructor(
    private val workManager: WorkManager
) {
    operator fun invoke(uuid: UUID): LiveData<WorkInfo> {
        return workManager.getWorkInfoByIdLiveData(uuid)
    }
}