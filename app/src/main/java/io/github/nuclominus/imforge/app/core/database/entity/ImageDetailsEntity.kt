package io.github.nuclominus.imforge.app.core.database.entity

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.work.WorkInfo
import io.github.nuclominus.imforge.app.core.database.AppDataBase

@Entity(tableName = AppDataBase.IMAGE_DETAILS_TABLE)
data class ImageDetailsEntity(

    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "width")
    val width: Float,

    @ColumnInfo(name = "height")
    val height: Float,

    @ColumnInfo(name = "originSize")
    val originSize: Long,

    @ColumnInfo(name = "compressedSize")
    val compressedSize: Long,

    @ColumnInfo(name = "mimeType")
    val mimeType: String,

    @ColumnInfo(name = "compression")
    val compression: Int,

    @ColumnInfo(name = "thumbPath")
    val thumbPath: String,

    @ColumnInfo(name = "originalPath")
    val originalPath: String,

    @ColumnInfo(name = "compressedPath")
    val compressedPath: String?,

    @ColumnInfo(name = "config_id")
    val configurationId: String?
) {
    @Ignore
    val workInfo: LiveData<WorkInfo>? = null
}