package com.example.quizapp.models

data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctAnswer: String
)