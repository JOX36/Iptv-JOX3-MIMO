package com.jox3.tv.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "server_accounts")
data class ServerAccount(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val url: String,
    val port: String,
    val username: String,
    val password: String,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
