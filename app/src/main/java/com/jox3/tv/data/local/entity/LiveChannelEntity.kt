package com.jox3.tv.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "live_channels",
    primaryKeys = ["streamId", "accountId"],
    indices = [Index("categoryId"), Index("accountId")]
)
data class LiveChannelEntity(
    val streamId: Int,
    val accountId: Long,
    val name: String,
    val streamIcon: String?,
    val epgChannelId: String?,
    val categoryId: String?,
    val categoryName: String?,
    val added: String?,
    val isFavorite: Boolean = false,
    val lastWatched: Long? = null
)
