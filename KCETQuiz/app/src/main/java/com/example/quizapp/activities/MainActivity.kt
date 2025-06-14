package com.example.quizapp.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quizapp.R
import com.example.quizapp.adapters.ItemAdapter
import com.example.quizapp.databinding.ActivityMainBinding
import com.example.quizapp.models.Item
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
	private lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        
       sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        if (intent.getBooleanExtra("pinShortcut", true))
            if (isDarkMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawerLayout = binding.drawerLayout
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val inflater = LayoutInflater.from(this)
        val customTitleView = inflater.inflate(R.layout.toolbar_title, null)

        toolbar.addView(
            customTitleView, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        if (!isDarkMode)
            toggle.drawerArrowDrawable.color = Color.BLACK
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navView = binding.navView

        val headerView = navView.getHeaderView(0)

        val welcomeTextView = headerView.findViewById<TextView>(R.id.welcomeTextView)
        val classTextView = headerView.findViewById<TextView>(R.id.classTextView)
        val usernameText = headerView.findViewById<TextView>(R.id.userName)

        val firstName = sharedPreferences.getString("firstName", "Guest")
        val lastName = sharedPreferences.getString("lastName", "Guest")
        val username = sharedPreferences.getString("username", "Guest")
        val classStudying = sharedPreferences.getString("classStudying", "!2")

        welcomeTextView.text = "Profile Name: $firstName $lastName"
        classTextView.text = "Class Studying: $classStudying"
        usernameText.text = "Username: $username"
        navView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.nav_home -> drawerLayout.closeDrawer(GravityCompat.START)

                R.id.delete -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    AlertDialog.Builder(this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Delete all attended quizzes?")
                        .setPositiveButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                            Toast.makeText(this, "Delete operation cancelled", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Delete") { dialog, _ ->
                            dialog.dismiss()
                            getSharedPreferences("quiz_prefs", MODE_PRIVATE).edit { clear() }
                            Snackbar.make(binding.root, "Deleted Successfully", Snackbar.LENGTH_SHORT).show()
                        }.show()
                }
                R.id.nav_telegram -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(Intent.ACTION_VIEW, "https://t.me/karnataka_kea".toUri()))
                }
                R.id.nav_telegram2 -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(Intent.ACTION_VIEW, "https://t.me/karnataka_neet".toUri()))
                }

                R.id.puc -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=com.puc.pyp".toUri()))
                }

                R.id.nav_dark -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    if (!isDarkMode)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    else
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    sharedPreferences.edit { putBoolean("dark_mode", !isDarkMode) }
                }

                R.id.nav_share -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Check out KCET NOTES AND QUESTION PAPERS App on PlayStore! https://play.google.com/store/apps/details?id=com.example.quizapp")
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share app via"))
                }

                R.id.nav_privacy -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(Intent.ACTION_VIEW, "https://sites.google.com/view/karpyp/home".toUri()))
                }

                R.id.nav_apps -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/search?q=pub:AppInnoVenture".toUri()))
                }

                R.id.nav_exit -> {
                    Snackbar.make(binding.root, "See you soon $firstName $lastName", Snackbar.LENGTH_SHORT).show()
                    sharedPreferences.edit { putBoolean("isLoggedIn", false) }
                    getSharedPreferences("quiz_prefs", MODE_PRIVATE).edit { clear() }
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        startActivity(Intent(this@MainActivity, LauncherActivity::class.java))
                        finish()
                    }
                }
            }
            navView.menu.setGroupCheckable(0, true, true)
            menuItem.isChecked = true
            navView.menu.setGroupCheckable(0, false, false)

            true
        }

        val items = listOf(
            Item(R.drawable.phy, "Physics", "phy"),
            Item(R.drawable.che, "Chemistry", "che"),
            Item(R.drawable.mat, "Maths", "maths"),
            Item(R.drawable.bio, "Biology", "bio"),
            Item(R.drawable.kan, "ಕನ್ನಡ", "kann_")
        )

        val itemAdapter = ItemAdapter(items) { item ->
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("extraTag", item.extra)
            intent.putExtra("subject", item.description)
            intent.putExtra("suffix","qwer$4tyui")
            intent.putExtra("img",item.img)
            intent.putExtra("name","https://raw.githubusercontent.com/TheKingOfSevenSeas/kcet/main/")
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = itemAdapter

        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START)
            else
                finish()
        }
    }
}