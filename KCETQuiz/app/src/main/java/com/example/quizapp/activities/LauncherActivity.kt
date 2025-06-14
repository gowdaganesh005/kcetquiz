package com.example.quizapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.example.quizapp.R
import com.google.android.material.snackbar.Snackbar
import com.example.quizapp.databinding.ActivityLauncherBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLauncherBinding

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
                Toast.makeText(this, "Please enter all the required fields", Toast.LENGTH_SHORT).show()
            } else {
                binding.loginButton.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE
                binding.progressBar.progress = 0

                CoroutineScope(Dispatchers.Main).launch {
                    val duration = 500L
                    val steps = 100
                    val stepDuration = duration / steps

                    with(sharedPreferences.edit()) {
                        putString("firstName", firstName)
                        putString("lastName", lastName)
                        putString("email", email)
                        putString("username", username)
                        putString("password", password)
                        putString("classStudying", classStudying)
                        putBoolean("isLoggedIn", true)
                        apply()
                    }
                    Snackbar.make(binding.root, "Welcome $firstName $lastName", Snackbar.LENGTH_LONG).show()

                    for (i in 0..steps) {
                        binding.progressBar.progress = i
                        delay(stepDuration)
                    }

                    binding.progressBar.visibility = View.GONE
                    binding.loginButton.isEnabled = true
                    delay(300)
                    startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}
