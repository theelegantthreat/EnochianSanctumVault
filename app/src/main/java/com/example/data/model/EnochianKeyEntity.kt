package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "enochian_keys")
data class EnochianKeyEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val subtitle: String,
    val element: String,
    val eNochianPhonetic: String,
    val englishTranslation: String,
    val purpose: String,
    val pronunciationGuide: String,
    val frequencyHz: Float,
    val masteryLevel: Int = 0,
    val totalInvocations: Int = 0,
    val lastInvokedTimestamp: Long = 0L
)
