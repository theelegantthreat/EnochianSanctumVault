package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_sigils")
data class SavedSigil(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val originalPhrase: String,
    val eNochianLetters: String,
    val sigilMethod: String, // "Sigil Rose Wheel", "Tablet Grid", "Glyph Converter"
    val pointsJson: String, // Normalized line coordinates JSON or serialized points
    val colorHex: String,
    val timestamp: Long = System.currentTimeMillis()
)
