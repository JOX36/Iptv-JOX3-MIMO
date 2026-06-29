package com.jox3.tv.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jox3.tv.data.local.dao.*
import com.jox3.tv.data.local.entity.*

@Database(
    entities = [
        ServerAccount::class,
        LiveChannelEntity::class,
        MovieEntity::class,
        SeriesEntity::class,
        CategoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class Jox3Database : RoomDatabase() {
    abstract fun serverAccountDao(): ServerAccountDao
    abstract fun liveChannelDao(): LiveChannelDao
    abstract fun movieDao(): MovieDao
    abstract fun seriesDao(): SeriesDao
    abstract fun categoryDao(): CategoryDao
}
