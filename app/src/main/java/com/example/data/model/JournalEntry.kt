package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val keyOrCallUsed: String,
    val planetaryHour: String,
    val moonPhase: String,
    val intention: String,
    val outcomeNotes: String,
    val insights: String,
    val rating: Int, // 1 to 5 stars/energy rating
    val mood: String = "Serene 🕯️",
    val isCloudSynced: Boolean = true // Simulated secure cloud backup status
)
