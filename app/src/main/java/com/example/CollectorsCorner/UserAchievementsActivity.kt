package com.example.CollectorsCorner



import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserAchievementsActivity: AppCompatActivity() {

    //Declarations
    private lateinit var starterProgressBar: ProgressBar
    private lateinit var collectorProgressBar: ProgressBar
    private lateinit var packratProgressBar: ProgressBar
    private lateinit var masterProgressBar: ProgressBar
    private lateinit var legendProgressBar: ProgressBar

    private lateinit var starterPercentage: TextView
    private lateinit var collectorPercentage: TextView
    private lateinit var packratPercentage: TextView
    private lateinit var masterPercentage: TextView
    private lateinit var legendPercentage: TextView

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "UserAchievementsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_achievements)

        // Initialize variables
        starterProgressBar = findViewById(R.id.pd_achievement_bar_starter)
        collectorProgressBar = findViewById(R.id.pd_achievement_bar_collector)
        packratProgressBar = findViewById(R.id.pd_achievement_bar_packrat)
        masterProgressBar = findViewById(R.id.pd_achievement_bar_master)
        legendProgressBar = findViewById(R.id.pd_achievement_bar_legend)

        starterPercentage = findViewById(R.id.pd_achievement_percentage_starter)
        collectorPercentage = findViewById(R.id.pd_achievement_percentage_collector)
        packratPercentage = findViewById(R.id.pd_achievement_percentage_packrat)
        masterPercentage = findViewById(R.id.pd_achievement_percentage_master)
        legendPercentage = findViewById(R.id.pd_achievement_percentage_legend)

        // Gets the current user that is logged in
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Gets the user's ID
            val uid = currentUser.uid
            database = FirebaseDatabase.getInstance().reference

            // Method to execute
            setProgressBars(uid)
        }


        // Set OnClickListener for the "Library" button to navigate back
        findViewById<Button>(R.id.library_btn4).setOnClickListener {
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

        // Handle back button click to the genre goals page
        findViewById<Button>(R.id.back_button_user_achievements).setOnClickListener {
            val intent = Intent(this, AchievementsActivity::class.java)
            startActivity(intent)
            finish()  // Finish the current activity
        }

    }


    private fun setProgressBars(uid: String) {
        val booksRef = database.child("Book").orderByChild("uid").equalTo(uid)

        booksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(bookSnapshot: DataSnapshot) {
                // Gets the user's total books from the Firebase real-time database
                val totalBooks = bookSnapshot.childrenCount.toInt()

                // The current progress is equal to the total books collected
                val currentProgress = totalBooks

                // Set max values for the progress bars (NB: Max values were multiplied by 100 to give a smoother animation to the progress bars)
                starterProgressBar.max = 1 * 100
                collectorProgressBar.max = 3 * 100
                packratProgressBar.max = 10 * 100
                masterProgressBar.max = 50 * 100
                legendProgressBar.max = 100 * 100

                // Set progress values for the progress bars (NB: Current progress values were multiplied by 100 to give a smoother animation to the progress bars)
                ObjectAnimator.ofInt(starterProgressBar, "progress", currentProgress * 100)
                    .setDuration(2000).start()
                ObjectAnimator.ofInt(collectorProgressBar, "progress", currentProgress * 100)
                    .setDuration(2000).start()
                ObjectAnimator.ofInt(packratProgressBar, "progress", currentProgress * 100)
                    .setDuration(2000).start()
                ObjectAnimator.ofInt(masterProgressBar, "progress", currentProgress * 100)
                    .setDuration(2000).start()
                ObjectAnimator.ofInt(legendProgressBar, "progress", currentProgress * 100)
                    .setDuration(2000).start()

                // Calculate and display percentage for the starter achievement
                starterPercentage.text = if (currentProgress >= 1) {
                    getString(R.string.complete_achievement)
                } else {
                    getString(R.string.p_achievement, (currentProgress * 100) / 1)
                }

                // Calculate and display percentage for the collector achievement
                collectorPercentage.text = if (currentProgress >= 3) {
                    getString(R.string.complete_achievement)
                } else {
                    getString(R.string.p_achievement, (currentProgress * 100) / 3)
                }

                // Calculate and display percentage for the packrat achievement
                packratPercentage.text = if (currentProgress >= 10) {
                    getString(R.string.complete_achievement)
                } else {
                    getString(R.string.p_achievement, (currentProgress * 100) / 10)
                }

                // Calculate and display percentage for the master achievement
                masterPercentage.text = if (currentProgress >= 50) {
                    getString(R.string.complete_achievement)
                } else {
                    getString(R.string.p_achievement, (currentProgress * 100) / 50)
                }

                // Calculate and display percentage for the legend achievement
                legendPercentage.text = if (currentProgress >= 100) {
                    getString(R.string.complete_achievement)
                } else {
                    getString(R.string.p_achievement, (currentProgress * 100) / 100)
                }
            }

            // Error handling if the application failed to retrieve the user's total books
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to retrieve total books: ${error.message}")
                Toast.makeText(
                    this@UserAchievementsActivity,
                    "Failed to retrieve total books",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
