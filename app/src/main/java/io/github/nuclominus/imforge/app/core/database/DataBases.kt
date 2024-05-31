package io.github.nuclominus.imforge.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.nuclominus.imforge.app.core.database.dao.ConfigurationDao
import io.github.nuclominus.imforge.app.core.database.dao.ImageDetailsDao
import io.github.nuclominus.imforge.app.core.database.entity.ConfigurationEntity
import io.github.nuclominus.imforge.app.core.database.entity.ImageDetailsEntity

@Database(
    entities = [
        ImageDetailsEntity::class,
        ConfigurationEntity::class
    ],
    version = AppDataBase.VERSION
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun imageDetailsDao(): ImageDetailsDao

    abstract fun configurationDao(): ConfigurationDao

    companion object {
        internal const val NAME = "image_data_base"
        internal const val VERSION = 1
        internal const val IMAGE_DETAILS_TABLE = "image_details"
        internal const val CONFIGURATION_TABLE = "configuration"
    }
}
