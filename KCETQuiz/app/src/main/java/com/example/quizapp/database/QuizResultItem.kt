package com.example.quizapp.database

data class QuizResultItem(
    val questionText: String,
    val options: List<Option>,
    val selectedOptionId: Int?,
    val correctOptionId: Int
)
