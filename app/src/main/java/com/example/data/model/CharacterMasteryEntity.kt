package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_mastery")
data class CharacterMasteryEntity(
    @PrimaryKey val letterName: String,
    val enochianChar: String,
    val englishChar: String,
    val masteryLevel: Int = 0, // 0: New/Unstudied, 1: Learning, 2: Familiar, 3: Mastered
    val timesReviewed: Int = 0,
    val correctCount: Int = 0,
    val lastReviewedTimestamp: Long = System.currentTimeMillis()
)
