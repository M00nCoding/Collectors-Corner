package com.example.CollectorsCorner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AchievementsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        // Set OnClickListener for the "Library" button to navigate back
        findViewById<Button>(R.id.library_btn2).setOnClickListener {
            // Create intent to navigate back to HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            // Finish this activity
            finish()
        }

        // Set OnClickListener for the "Add Genre" button to navigate to AddGenreActivity
        findViewById<Button>(R.id.add_genre_btn).setOnClickListener {
            val intent = Intent(this, AddGenreActivity::class.java)
            startActivity(intent)
        }

        // Set OnClickListener for navigating to the add book page
        findViewById<Button>(R.id.add_book_btn).setOnClickListener {
            // Create intent to navigate to AddBookActivity
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }

    }
}