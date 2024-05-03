package com.example.CollectorsCorner



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AddBookActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addbook)


        // Set OnClickListener for the "Library" button to navigate back
        findViewById<Button>(R.id.library_btn3).setOnClickListener {
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

        // Set OnClickListener for navigating to the achievements page
        findViewById<Button>(R.id.achievements_button).setOnClickListener {
            // Create intent to navigate to AchievementsActivity
            val intent = Intent(this, AchievementsActivity::class.java)
            startActivity(intent)
        }
    }
}