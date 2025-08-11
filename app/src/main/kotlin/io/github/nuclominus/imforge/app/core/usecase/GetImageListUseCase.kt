package io.github.nuclominus.imforge.app.core.usecase

import io.github.nuclominus.imforge.app.core.database.AppDataBase
import io.github.nuclominus.imforge.app.core.database.entity.ImageDetailsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class GetImageListUseCase @Inject constructor(
    private val database: AppDataBase
) {

    operator fun invoke(): Flow<List<ImageDetailsEntity>> {
        return database.imageDetailsDao()
            .getAll().map { entities ->
                // Filter out entities that have no file on disk
                entities.filter { entity ->
                    File(entity.originalPath).exists()
                }.reversed()
            }
    }
}