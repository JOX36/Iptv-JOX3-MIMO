package com.jox3.tv.data.local.dao

import androidx.room.*
import com.jox3.tv.data.local.entity.LiveChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LiveChannelDao {
    @Query("SELECT * FROM live_channels WHERE accountId = :accountId ORDER BY name")
    fun getAll(accountId: Long): Flow<List<LiveChannelEntity>>

    @Query("SELECT * FROM live_channels WHERE accountId = :accountId AND categoryId = :categoryId ORDER BY name")
    fun getByCategory(accountId: Long, categoryId: String): Flow<List<LiveChannelEntity>>

    @Query("SELECT * FROM live_channels WHERE accountId = :accountId AND isFavorite = 1 ORDER BY name")
    fun getFavorites(accountId: Long): Flow<List<LiveChannelEntity>>

    @Query("SELECT * FROM live_channels WHERE accountId = :accountId AND name LIKE '%' || :query || '%' ORDER BY name")
    fun search(accountId: Long, query: String): Flow<List<LiveChannelEntity>>

    @Query("SELECT * FROM live_channels WHERE accountId = :accountId AND lastWatched IS NOT NULL ORDER BY lastWatched DESC LIMIT :limit")
    fun getRecentlyWatched(accountId: Long, limit: Int = 10): Flow<List<LiveChannelEntity>>

    @Query("SELECT * FROM live_channels WHERE streamId = :streamId AND accountId = :accountId")
    suspend fun getByStreamId(streamId: Int, accountId: Long): LiveChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(channels: List<LiveChannelEntity>)

    @Query("UPDATE live_channels SET isFavorite = :isFavorite WHERE streamId = :streamId AND accountId = :accountId")
    suspend fun setFavorite(streamId: Int, accountId: Long, isFavorite: Boolean)

    @Query("UPDATE live_channels SET lastWatched = :timestamp WHERE streamId = :streamId AND accountId = :accountId")
    suspend fun updateLastWatched(streamId: Int, accountId: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM live_channels WHERE accountId = :accountId")
    suspend fun deleteAllForAccount(accountId: Long)
}
