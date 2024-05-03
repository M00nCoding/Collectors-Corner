package com.example.CollectorsCorner

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddGenreActivity: AppCompatActivity() {

    //Declarations
    private lateinit var etGenreName : EditText
    private lateinit var etGenreGoal : EditText
    private lateinit var btnAddGenre : Button

    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addgenre)

        //Initialize variables
        etGenreName = findViewById(R.id.genreNameEt)
        etGenreGoal = findViewById(R.id.genreGoalEt)
        btnAddGenre = findViewById(R.id.addGenreBtn)

        //Database reference
        dbRef = FirebaseDatabase.getInstance().getReference("Genre")

        //Set on click listener to the Add Genre button
        btnAddGenre.setOnClickListener{
            saveGenreData()
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

    //Save genre data function
    private fun saveGenreData(){

        //Get values from fields
        val genreName = etGenreName.text.toString()
        val genreGoal = etGenreGoal.text.toString()

        //Auth
        auth = FirebaseAuth.getInstance()

        //Display error message if the user leaves the genre name field empty
        if(genreName.isEmpty()){
            Toast.makeText(this, "Please enter genre name", Toast.LENGTH_SHORT)
                .show()
            return
        }

        //Display error message if the user leaves the genre goal field empty
        if(genreGoal.isEmpty()){
            Toast.makeText(this, "Please set genre goal", Toast.LENGTH_SHORT)
                .show()
            return
        }

        //Creates a unique Genre Id
        val genreId = dbRef.push().key!!

        //Gets the User Id
        val uid = auth.currentUser?.uid.toString()

        //Insert values into the GenrModel class
        val genre = GenreModel(genreId, genreName, genreGoal, uid)

        //Insert genre data into the firebase real-time database
        dbRef.child(genreId).setValue(genre)
            .addOnCompleteListener{
                //Display message if inserting the genre data was successful
                Toast.makeText(this, "Genre added successfully!", Toast.LENGTH_LONG).show()

                //Clear edit text fields
                etGenreName.text.clear()
                etGenreGoal.text.clear()

            }.addOnFailureListener{ err ->
                //Display message if inserting the genre data was not successful
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }

}