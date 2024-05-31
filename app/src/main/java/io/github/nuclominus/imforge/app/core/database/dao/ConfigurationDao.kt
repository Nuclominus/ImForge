package io.github.nuclominus.imforge.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.nuclominus.imforge.app.core.database.entity.ConfigurationEntity

@Dao
interface ConfigurationDao {

    @Insert
    fun insert(configuration: ConfigurationEntity)

    @Query("SELECT * FROM configuration WHERE id = :id")
    fun getById(id: String): ConfigurationEntity
}