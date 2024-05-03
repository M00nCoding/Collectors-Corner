package com.example.CollectorsCorner

import android.content.Intent
import android.os.Bundle
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import android.widget.Button


class HomeActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        // Check if the user has logged in previously
        val isLoggedIn = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getBoolean("isLoggedIn", false)

        // Display a success message when user has been successfully logged in for the first time
        if (!isLoggedIn) {
            Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_SHORT).show()
            // Save the login status to SharedPreferences
            getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit()
                .putBoolean("isLoggedIn", true).apply()
        }


        // Set OnClickListener for navigating to the add genre page
        findViewById<Button>(R.id.add_genre_btn).setOnClickListener {
            // Create intent to navigate to AddGenreActivity
            val intent = Intent(this, AddGenreActivity::class.java)
            startActivity(intent)
        }

        // Set OnClickListener for navigating to the add book page
        findViewById<Button>(R.id.add_book_btn).setOnClickListener {
            // Create intent to navigate to AddBookActivity
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }


        // Set OnClickListener for navigating to the achievements page
        findViewById<Button>(R.id.achievements_button).setOnClickListener {
            // Create intent to navigate to AchievementsActivity
            val intent = Intent(this, AchievementsActivity::class.java)
            startActivity(intent)
        }




    }
}