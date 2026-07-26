package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SavedSigil
import kotlinx.coroutines.flow.Flow

@Dao
interface SigilDao {
    @Query("SELECT * FROM saved_sigils ORDER BY timestamp DESC")
    fun getAllSavedSigils(): Flow<List<SavedSigil>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSigil(sigil: SavedSigil): Long

    @Query("DELETE FROM saved_sigils WHERE id = :id")
    suspend fun deleteSigil(id: Long)
}
