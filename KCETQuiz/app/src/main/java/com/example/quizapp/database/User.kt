package com.example.quizapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val classStudying: Int
)