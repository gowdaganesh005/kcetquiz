package com.example.quizapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Option(
    @PrimaryKey val id: Int,
    val questionId: Int,
    val text: String,
    val isCorrect: Boolean
)