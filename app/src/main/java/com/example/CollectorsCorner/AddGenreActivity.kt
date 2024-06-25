package com.example.CollectorsCorner

import android.os.Bundle
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddGenreActivity: AppCompatActivity() {

    //Declarations
    private lateinit var spinnerGenreName: Spinner
    private lateinit var etGenreGoal: EditText
    private lateinit var btnAddGenre: Button

    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    // Predefined list of genres
    private val genres = listOf("Fiction", "Fantasy", "Science Fiction", "Mystery", "Romance", "Thriller", "Horror", "Non-fiction", "Biography", "History", "Self-help")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addgenre)

        // Initialize variables
        spinnerGenreName = findViewById(R.id.genreNameSpinner)
        etGenreGoal = findViewById(R.id.genreGoalEt)
        btnAddGenre = findViewById(R.id.addGenreBtn)

        // Populate spinner with predefined genres
        val genresWithPrompt = mutableListOf("Select Genre")
        genresWithPrompt.addAll(genres)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            genresWithPrompt
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenreName.adapter = adapter
        spinnerGenreName.setSelection(0)

        // Database reference
        dbRef = FirebaseDatabase.getInstance().getReference("Genre")
        auth = FirebaseAuth.getInstance()

        // Set on click listener to the Add Genre button
        btnAddGenre.setOnClickListener {
            checkAndSaveGenreData()
        }


        // Set OnClickListener for the "Library" button to navigate back
        findViewById<Button>(R.id.library_btn1).setOnClickListener {
            // Create intent to navigate back to HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            // Finish this activity
            finish()
        }

        // Set OnClickListener for the "Add Book" button to navigate to AddBookActivity
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


    // Function for checking existing genre goals that have been set
    private fun checkAndSaveGenreData() {
        val uid = auth.currentUser?.uid ?: return

        // Check for active genre goals
        dbRef.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var hasActiveGoal = false
                for (genreSnapshot in dataSnapshot.children) {
                    val goal = genreSnapshot.child("genreGoal").getValue(String::class.java)?.toIntOrNull()
                    val progress = genreSnapshot.child("progress").getValue(Int::class.java) ?: 0
                    if (goal != null && progress < goal) {
                        hasActiveGoal = true
                        break
                    }
                }

                if (hasActiveGoal) {
                    Toast.makeText(this@AddGenreActivity, "Please complete your current genre goal first.", Toast.LENGTH_SHORT).show()
                } else {
                    saveGenreData()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@AddGenreActivity, "Error checking genre goals: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // Function for setting the genre data to be stored in the database
    private fun saveGenreData() {
        // Get values from fields
        val selectedGenre = spinnerGenreName.selectedItem.toString()
        val genreGoal = etGenreGoal.text.toString()

        // Display error message if the user leaves the genre name field empty
        if (selectedGenre == "Select Genre") {
            Toast.makeText(this, "Please select a genre", Toast.LENGTH_SHORT).show()
            return
        }

        // Display error message if the user leaves the genre goal field empty
        if (genreGoal.isEmpty()) {
            Toast.makeText(this, "Please set a genre goal", Toast.LENGTH_SHORT).show()
            return
        }

        // Creates a unique Genre Id
        val genreId = dbRef.push().key!!
        // Gets the User Id
        val uid = auth.currentUser?.uid.toString()
        // Insert values into the GenreModel class
        val genre = GenreModel(genreId, selectedGenre, genreGoal, uid)
        // Insert genre data into the firebase real-time database
        dbRef.child(genreId).setValue(genre).addOnCompleteListener {
            // Display message if inserting the genre data was successful
            Toast.makeText(this, "Genre added successfully!", Toast.LENGTH_LONG).show()
            // Clear edit text fields
            spinnerGenreName.setSelection(0)
            etGenreGoal.text.clear()
        }.addOnFailureListener { err ->
            // Display message if inserting the genre data was not successful
            Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
        }
    }
}