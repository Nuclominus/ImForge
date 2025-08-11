package io.github.nuclominus.imforge.app.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.nuclominus.imforge.app.core.database.AppDataBase

@Entity(tableName = AppDataBase.CONFIGURATION_TABLE)
data class ConfigurationEntity(

    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "compressFormat")
    val compressFormat: Int,

    @ColumnInfo(name = "maxWidth")
    val maxWidth: Float,

    @ColumnInfo(name = "maxHeight")
    val maxHeight: Float,

    @ColumnInfo(name = "useMaxScale")
    val useMaxScale: Boolean,

    @ColumnInfo(name = "quality")
    val quality: Int,

    @ColumnInfo(name = "minWidth")
    val minWidth: Int,

    @ColumnInfo(name = "minHeight")
    val minHeight: Int
)