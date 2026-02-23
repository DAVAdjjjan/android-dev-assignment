package com.assignments.lab3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_results")
data class SavedResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val selectedFont: String,
    val createdAt: Long
)
