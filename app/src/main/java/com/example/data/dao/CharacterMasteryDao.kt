package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CharacterMasteryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterMasteryDao {
    @Query("SELECT * FROM character_mastery")
    fun getAllMasteryRecords(): Flow<List<CharacterMasteryEntity>>

    @Query("SELECT * FROM character_mastery WHERE letterName = :letterName")
    suspend fun getMasteryForLetter(letterName: String): CharacterMasteryEntity?

    @Query("SELECT COUNT(*) FROM character_mastery")
    suspend fun getMasteryRecordCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInitialRecords(records: List<CharacterMasteryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: CharacterMasteryEntity)

    @Update
    suspend fun updateMastery(record: CharacterMasteryEntity)

    @Query("UPDATE character_mastery SET masteryLevel = :newLevel, timesReviewed = timesReviewed + 1, correctCount = correctCount + :isCorrect, lastReviewedTimestamp = :timestamp WHERE letterName = :letterName")
    suspend fun recordReviewResult(letterName: String, newLevel: Int, isCorrect: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE character_mastery SET masteryLevel = 0, timesReviewed = 0, correctCount = 0")
    suspend fun resetAllProgress()
}
