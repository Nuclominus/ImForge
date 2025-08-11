package io.github.nuclominus.imforge.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.nuclominus.imforge.app.core.database.AppDataBase
import io.github.nuclominus.imforge.app.core.database.entity.ImageDetailsEntity
import io.github.nuclominus.imforge.app.ui.model.OptimizedImageModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(imageDetailsEntity: ImageDetailsEntity)

    @Query("SELECT * FROM ${AppDataBase.IMAGE_DETAILS_TABLE}")
    fun getAll(): Flow<List<ImageDetailsEntity>>

    @Query("SELECT * FROM ${AppDataBase.IMAGE_DETAILS_TABLE} WHERE id = :id")
    fun getById(id: String): ImageDetailsEntity

    @Transaction
    @Query("SELECT * FROM ${AppDataBase.IMAGE_DETAILS_TABLE} WHERE id = :id")
    fun getAllDetailsById(id: String): OptimizedImageModel

}