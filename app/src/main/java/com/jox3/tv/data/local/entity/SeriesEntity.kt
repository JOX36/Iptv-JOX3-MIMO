package com.jox3.tv.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "series",
    primaryKeys = ["seriesId", "accountId"],
    indices = [Index("categoryId"), Index("accountId")]
)
data class SeriesEntity(
    val seriesId: Int,
    val accountId: Long,
    val name: String,
    val cover: String?,
    val plot: String?,
    val rating: String?,
    val genre: String?,
    val categoryId: String?,
    val categoryName: String?,
    val releaseDate: String?,
    val isFavorite: Boolean = false,
    val lastWatched: Long? = null
)
