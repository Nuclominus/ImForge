package io.github.nuclominus.imforge.app.core.usecase

import io.github.nuclominus.imforge.app.core.database.AppDataBase
import io.github.nuclominus.imforge.app.ui.model.OptimizedImageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetImageDetailsUseCase @Inject constructor(
    private val database: AppDataBase
) {

    suspend operator fun invoke(id: String): OptimizedImageModel =
        withContext(Dispatchers.Default) {
            database.imageDetailsDao().getAllDetailsById(id)
        }
}
