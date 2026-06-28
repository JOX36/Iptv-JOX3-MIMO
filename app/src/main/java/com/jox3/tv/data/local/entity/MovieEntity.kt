package com.jox3.tv.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "movies",
    primaryKeys = ["streamId", "accountId"],
    indices = [Index("categoryId"), Index("accountId")]
)
data class MovieEntity(
    val streamId: Int,
    val accountId: Long,
    val name: String,
    val streamIcon: String?,
    val rating: String?,
    val categoryId: String?,
    val categoryName: String?,
    val containerExtension: String?,
    val added: String?,
    val isFavorite: Boolean = false,
    val lastWatched: Long? = null,
    val watchProgress: Long = 0
)
