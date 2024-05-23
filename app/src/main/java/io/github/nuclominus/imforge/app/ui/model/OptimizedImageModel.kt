package io.github.nuclominus.imforge.app.ui.model

import androidx.room.Embedded
import androidx.room.Relation
import io.github.nuclominus.imforge.app.core.database.entity.ConfigurationEntity
import io.github.nuclominus.imforge.app.core.database.entity.ImageDetailsEntity

data class OptimizedImageModel(
    @Embedded val entity: ImageDetailsEntity,
    @Relation(
        parentColumn = "config_id",
        entityColumn = "id"
    )
    val config: ConfigurationEntity,
)