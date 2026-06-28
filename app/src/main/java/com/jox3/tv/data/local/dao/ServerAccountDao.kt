package com.jox3.tv.data.local.dao

import androidx.room.*
import com.jox3.tv.data.local.entity.ServerAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerAccountDao {
    @Query("SELECT * FROM server_accounts ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ServerAccount>>

    @Query("SELECT * FROM server_accounts WHERE isActive = 1 LIMIT 1")
    fun getActive(): Flow<ServerAccount?>

    @Query("SELECT * FROM server_accounts WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveSync(): ServerAccount?

    @Query("SELECT * FROM server_accounts WHERE id = :id")
    suspend fun getById(id: Long): ServerAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: ServerAccount): Long

    @Update
    suspend fun update(account: ServerAccount)

    @Delete
    suspend fun delete(account: ServerAccount)

    @Query("UPDATE server_accounts SET isActive = 0")
    suspend fun deactivateAll()

    @Query("UPDATE server_accounts SET isActive = 1 WHERE id = :id")
    suspend fun activate(id: Long)

    @Transaction
    suspend fun setActive(id: Long) {
        deactivateAll()
        activate(id)
    }
}
