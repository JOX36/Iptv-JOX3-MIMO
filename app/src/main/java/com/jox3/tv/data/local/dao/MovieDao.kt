package com.jox3.tv.data.local.dao

import androidx.room.*
import com.jox3.tv.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE accountId = :accountId ORDER BY name")
    fun getAll(accountId: Long): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE accountId = :accountId AND categoryId = :categoryId ORDER BY name")
    fun getByCategory(accountId: Long, categoryId: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE accountId = :accountId AND isFavorite = 1 ORDER BY name")
    fun getFavorites(accountId: Long): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE accountId = :accountId AND name LIKE '%' || :query || '%' ORDER BY name")
    fun search(accountId: Long, query: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE accountId = :accountId AND lastWatched IS NOT NULL ORDER BY lastWatched DESC LIMIT :limit")
    fun getRecentlyWatched(accountId: Long, limit: Int = 10): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Query("UPDATE movies SET isFavorite = :isFavorite WHERE streamId = :streamId AND accountId = :accountId")
    suspend fun setFavorite(streamId: Int, accountId: Long, isFavorite: Boolean)

    @Query("UPDATE movies SET lastWatched = :timestamp, watchProgress = :progress WHERE streamId = :streamId AND accountId = :accountId")
    suspend fun updateWatchProgress(streamId: Int, accountId: Long, timestamp: Long = System.currentTimeMillis(), progress: Long = 0)

    @Query("DELETE FROM movies WHERE accountId = :accountId")
    suspend fun deleteAllForAccount(accountId: Long)
}
