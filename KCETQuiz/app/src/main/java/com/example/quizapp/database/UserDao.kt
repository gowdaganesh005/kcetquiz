package com.example.quizapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("DELETE FROM User")
    suspend fun deleteAllUsers()
}