package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.EnochianKeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EnochianKeyDao {
    @Query("SELECT * FROM enochian_keys ORDER BY id ASC")
    fun getAllKeys(): Flow<List<EnochianKeyEntity>>

    @Query("SELECT * FROM enochian_keys WHERE id = :keyId")
    suspend fun getKeyById(keyId: Int): EnochianKeyEntity?

    @Query("SELECT * FROM enochian_keys WHERE id = :keyId")
    fun observeKeyById(keyId: Int): Flow<EnochianKeyEntity?>

    @Query("SELECT COUNT(*) FROM enochian_keys")
    suspend fun getKeyCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertKeys(keys: List<EnochianKeyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(key: EnochianKeyEntity)

    @Update
    suspend fun updateKey(key: EnochianKeyEntity)

    @Query("UPDATE enochian_keys SET totalInvocations = totalInvocations + 1, lastInvokedTimestamp = :timestamp, masteryLevel = MIN(100, masteryLevel + 10) WHERE id = :keyId")
    suspend fun recordKeyInvocation(keyId: Int, timestamp: Long = System.currentTimeMillis())
}
