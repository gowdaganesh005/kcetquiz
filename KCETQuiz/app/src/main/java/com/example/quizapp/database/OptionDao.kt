package com.example.quizapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OptionDao {
    @Query("SELECT * FROM option WHERE questionId = :questionId")
    suspend fun getOptionsForQuestion(questionId: Int): List<Option>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(options: List<Option>)
}