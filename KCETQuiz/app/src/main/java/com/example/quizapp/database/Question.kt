package com.example.quizapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Question(
    @PrimaryKey val id: Int,
    val text: String,
    val subject: String
)