package com.jox3.tv.data.local.dao

import androidx.room.*
import com.jox3.tv.data.local.entity.SeriesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {
    @Query("SELECT * FROM series WHERE accountId = :accountId ORDER BY name")
    fun getAll(accountId: Long): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series WHERE accountId = :accountId AND categoryId = :categoryId ORDER BY name")
    fun getByCategory(accountId: Long, categoryId: String): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series WHERE accountId = :accountId AND isFavorite = 1 ORDER BY name")
    fun getFavorites(accountId: Long): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series WHERE accountId = :accountId AND name LIKE '%' || :query || '%' ORDER BY name")
    fun search(accountId: Long, query: String): Flow<List<SeriesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(series: List<SeriesEntity>)

    @Query("UPDATE series SET isFavorite = :isFavorite WHERE seriesId = :seriesId AND accountId = :accountId")
    suspend fun setFavorite(seriesId: Int, accountId: Long, isFavorite: Boolean)

    @Query("UPDATE series SET lastWatched = :timestamp WHERE seriesId = :seriesId AND accountId = :accountId")
    suspend fun updateLastWatched(seriesId: Int, accountId: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM series WHERE accountId = :accountId")
    suspend fun deleteAllForAccount(accountId: Long)
}
