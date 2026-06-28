package com.jox3.tv.data.local.dao

import androidx.room.*
import com.jox3.tv.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE type = :type AND accountId = :accountId ORDER BY categoryName")
    fun getByType(type: String, accountId: Long): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories WHERE type = :type AND accountId = :accountId")
    suspend fun deleteByType(type: String, accountId: Long)

    @Query("DELETE FROM categories WHERE accountId = :accountId")
    suspend fun deleteAllForAccount(accountId: Long)
}
