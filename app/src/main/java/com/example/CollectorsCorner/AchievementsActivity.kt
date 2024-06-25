package com.example.CollectorsCorner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AchievementsActivity: AppCompatActivity() {

// TAG predefined
    companion object {
        private const val TAG = "AchievementsActivity"
    }

    // Declared variables used
    private lateinit var database: DatabaseReference
    private lateinit var pieChartView: PieChartView
    private lateinit var auth: FirebaseAuth
    private lateinit var goalNameTextView: TextView
    private lateinit var totalBooksCollectedTextView: TextView
    private lateinit var totalGenresAchievedTextView: TextView
    private var isRefreshPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        pieChartView = findViewById(R.id.pie_chart)
        goalNameTextView = findViewById(R.id.goal_name)
        totalBooksCollectedTextView = findViewById(R.id.total_books_collected)
        totalGenresAchievedTextView = findViewById(R.id.total_genres_achieved)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser


        if (currentUser != null) {
            val uid = currentUser.uid
            database = FirebaseDatabase.getInstance().reference
            loadAchievementProgress(uid)
            loadTotalBooksCollected(uid)
            loadTotalGenresAchieved(uid)
        }

// PLEASE LEAVE THIS AS THIS CAN BE REUSED IN THE FUTURE THANKS
        /*// Check if user is logged in
        if (currentUser != null) {
            val uid = currentUser.uid
            database = FirebaseDatabase.getInstance().getReference("Achievements").child(uid)

            loadAchievementProgress()
        }*/

        /* // Firebase reference
        database = FirebaseDatabase.getInstance().getReference("users").child("user_id_123")
            .child("achievements_progress")
*/

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

        // Set OnClickListener for the "View User Achievements" button to navigate to UserAchievementsActivity
        findViewById<Button>(R.id.view_user_achievements_btn).setOnClickListener {
            val intent = Intent(this, UserAchievementsActivity::class.java)
            startActivity(intent)
        }


        // refresh button to refresh the data of the users achieved goals
        findViewById<Button>(R.id.refresh_button).setOnClickListener {
            if (!isRefreshPressed) {
                val uid = currentUser?.uid ?: return@setOnClickListener
                loadAchievementProgress(uid)
                loadTotalBooksCollected(uid)
                loadTotalGenresAchieved(uid)
                Toast.makeText(this, "The data has been refreshed.", Toast.LENGTH_SHORT).show()
                isRefreshPressed = true
            } else {
                Toast.makeText(this, "You can't do multiple clicks, the data has already been refreshed.", Toast.LENGTH_SHORT).show()
            }
        }

    }


    // start of the load achievements progress for the percentages as the user adds books with the genre goal that was set
    private fun loadAchievementProgress(uid: String) {
        val genreRef = database.child("Genre").orderByChild("uid").equalTo(uid)

        genreRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(genreSnapshot: DataSnapshot) {
                // Find the first active genre goal
                val activeGenre = genreSnapshot.children.firstOrNull { genre ->
                    val goal = genre.child("genreGoal").getValue(String::class.java)?.toIntOrNull() ?: 0
                    val progress = genre.child("progress").getValue(Int::class.java) ?: 0
                    progress < goal
                }

                if (activeGenre == null) {
                    // No active genre goal found, update UI accordingly
                    Toast.makeText(this@AchievementsActivity, "No active genre goals. Please add a new genre goal.", Toast.LENGTH_LONG).show()
                    pieChartView.setProgressData(emptyMap())
                    goalNameTextView.text = "Current Goal: No active goals set"
                    return
                }

                // If an active genre goal is found, proceed to update the progress
                val genreName = activeGenre.child("genreName").getValue(String::class.java) ?: return
                val genreGoal = activeGenre.child("genreGoal").getValue(String::class.java)?.toIntOrNull() ?: return

                goalNameTextView.text = getString(R.string.current_goal, genreName)

                val booksRef = database.child("Book").orderByChild("uid").equalTo(uid)

                booksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(bookSnapshot: DataSnapshot) {
                        val progress = bookSnapshot.children.count { book ->
                            val genre = book.child("genre").getValue(String::class.java) ?: ""
                            genre == genreName
                        }

                        val percentage = if (genreGoal > 0) {
                            ((progress.toFloat() / genreGoal) * 100).toInt()
                        } else {
                            0
                        }

                        Log.d(TAG, "Genre: $genreName, Goal: $genreGoal, Progress: $progress, Calculated Percentage: $percentage")

                        pieChartView.setProgressData(mapOf(genreName to percentage))

                        activeGenre.ref.child("progress").setValue(progress)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Failed to load books: ${error.message}")
                        Toast.makeText(this@AchievementsActivity, "Failed to load books", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to load genres: ${error.message}")
                Toast.makeText(this@AchievementsActivity, "Failed to load genres", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // start of the load total books collected function to display inside the activity_achievements.xml layout file
    private fun loadTotalBooksCollected(uid: String) {
        val booksRef = database.child("Book").orderByChild("uid").equalTo(uid)

        booksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(bookSnapshot: DataSnapshot) {
                val totalBooks = bookSnapshot.childrenCount.toInt()
                totalBooksCollectedTextView.text = getString(R.string.total_books_collected, totalBooks)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to load total books: ${error.message}")
                Toast.makeText(this@AchievementsActivity, "Failed to load total books", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // start of the load total genres achieved to display the achieved goals so far inside the activity_achievements.xml layout file
    private fun loadTotalGenresAchieved(uid: String) {
        val genreRef = database.child("Genre").orderByChild("uid").equalTo(uid)

        genreRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(genreSnapshot: DataSnapshot) {
                val achievedGenres = genreSnapshot.children.count { genre ->
                    val goal = genre.child("genreGoal").getValue(String::class.java)?.toIntOrNull() ?: 0
                    val progress = genre.child("progress").getValue(Int::class.java) ?: 0
                    progress >= goal
                }
                totalGenresAchievedTextView.text = getString(R.string.genres_achieved, achievedGenres)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to load achieved genres: ${error.message}")
                Toast.makeText(this@AchievementsActivity, "Failed to load achieved genres", Toast.LENGTH_SHORT).show()
            }
        })
    }
}