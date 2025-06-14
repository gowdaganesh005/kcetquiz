package com.example.quizapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserAnswerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userAnswer: UserAnswer)

    @Query("SELECT * FROM userAnswer WHERE questionId = :questionId")
    suspend fun getUserAnswerForQuestion(questionId: Int): UserAnswer?

    @Query("DELETE FROM userAnswer")
    suspend fun clearUserAnswers()
}