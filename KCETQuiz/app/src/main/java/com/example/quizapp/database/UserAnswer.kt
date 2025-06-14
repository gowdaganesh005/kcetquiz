package com.example.quizapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserAnswer(
    @PrimaryKey val questionId: Int,
    val selectedOptionId: Int
)