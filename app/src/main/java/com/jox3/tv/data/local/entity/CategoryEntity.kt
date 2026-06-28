package com.jox3.tv.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "categories",
    primaryKeys = ["categoryId", "type", "accountId"],
    indices = [Index("type", "accountId")]
)
data class CategoryEntity(
    val categoryId: String,
    val type: String, // "live", "vod", "series"
    val accountId: Long,
    val categoryName: String,
    val parentId: Int? = null
)
