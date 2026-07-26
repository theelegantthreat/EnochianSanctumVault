package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.InvocationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface InvocationDao {
    @Query("SELECT * FROM invocation_records ORDER BY timestamp DESC")
    fun getAllInvocations(): Flow<List<InvocationRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvocation(record: InvocationRecord): Long

    @Query("DELETE FROM invocation_records WHERE id = :id")
    suspend fun deleteInvocation(id: Long)

    @Query("SELECT COUNT(*) FROM invocation_records")
    fun getInvocationCount(): Flow<Int>

    @Query("SELECT SUM(durationSeconds) FROM invocation_records")
    fun getTotalDurationSeconds(): Flow<Int?>
}
