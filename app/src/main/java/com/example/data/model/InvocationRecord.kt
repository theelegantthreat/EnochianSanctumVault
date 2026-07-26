package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invocation_records")
data class InvocationRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val callNumber: Int,
    val callTitle: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int,
    val vibrationCount: Int,
    val watchtower: String,
    val notes: String = ""
)
