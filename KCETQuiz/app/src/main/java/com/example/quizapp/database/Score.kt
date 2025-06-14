package com.example.quizapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Score(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val subject: String,
    val correctCount: Int,
    val totalQuestions: Int,
    val timestamp: Long
)