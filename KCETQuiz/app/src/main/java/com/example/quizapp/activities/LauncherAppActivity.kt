package com.example.quizapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.room.Room
import com.example.quizapp.database.AppDatabase
import com.example.quizapp.database.UserDao
import com.example.quizapp.R
import com.example.quizapp.database.User
import com.google.android.material.snackbar.Snackbar
import com.example.quizapp.databinding.ActivityLauncherBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LauncherAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLauncherBinding
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_launcher)

        binding.loginButton.setOnClickListener {
            val firstName = binding.firstNameEditText.text.toString().trim()
            val lastName = binding.lastNameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val classStudying = binding.classEditText.text.toString().trim()
            val username = binding.userNameEditText.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || classStudying.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Please enter all the required fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.loginButton.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE
                binding.progressBar.progress = 0

                database = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "quiz-database"
                ).build()
                userDao = database.userDao()

                CoroutineScope(Dispatchers.Main).launch {
                    val duration = 500L
                    val steps = 100
                    val stepDuration = duration / steps


                    val user = User(
                        username = username,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        password = password,
                        classStudying = classStudying.toInt()
                    )

                    withContext(Dispatchers.IO) {
                        userDao.insertUser(user)
                    }

                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", true)
                        putString("username", username) // Store username for future reference
                        apply()
                    }
                    Snackbar.make(
                        binding.root,
                        "Welcome $firstName $lastName",
                        Snackbar.LENGTH_LONG
                    ).show()

                    for (i in 0..steps) {
                        binding.progressBar.progress = i
                        delay(stepDuration)
                    }

                    binding.progressBar.visibility = View.GONE
                    binding.loginButton.isEnabled = true
                    delay(300)
                    startActivity(Intent(this@LauncherAppActivity, MainActivity::class.java))
                    finish()
                }
            }
        }

        binding.deleteLogIn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                userDao.deleteAllUsers()
            }
        }
    }

}
