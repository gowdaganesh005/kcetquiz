package com.example.quizapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {
    @Insert
    suspend fun insert(score: Score)

    @Query("SELECT * FROM score WHERE subject = :subject ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestScoreForSubject(subject: String): Score?
}