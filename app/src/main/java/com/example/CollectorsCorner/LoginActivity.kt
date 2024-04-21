package com.example.CollectorsCorner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()


        val signUpTextView: TextView = findViewById(R.id.textViewSignUp)
        val loginButton: Button = findViewById(R.id.btnlogin)
        val usernameEditText: EditText = findViewById(R.id.username_edittext)
        val passwordEditText: EditText = findViewById(R.id.password_edittext)


        signUpTextView.setOnClickListener {
            // Create Intent to navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            // Start the MainActivity
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                // Show an error message if either field is empty
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Authenticate user with Firebase
            signInUser(username, password)

        }
    }

    private fun signInUser(username: String, password: String) {
        // Get a reference to the "User" node in the database
        val userRef = FirebaseDatabase.getInstance().getReference("User")

        // Check if the provided username exists in the database
        userRef.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // If the username exists, get the email associated with it
                        val userEmail = snapshot.children.first().child("email").getValue(String::class.java)

                        if (userEmail != null) {
                            // Attempt to sign in with Firebase Authentication using email and password
                            auth.signInWithEmailAndPassword(userEmail, password)
                                .addOnCompleteListener(this@LoginActivity) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        val user = auth.currentUser
                                        // Proceed to the HomeScreenActivity
                                        startActivity(
                                            Intent(
                                                this@LoginActivity,
                                                HomeScreenActivity::class.java
                                            )
                                        )
                                        finish()
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(
                                            baseContext,
                                            "Username or password is incorrect. Please try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            // If email is null, handle the case
                            Toast.makeText(
                                baseContext,
                                "Error: Email not found for the username.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // If the username does not exist, notify the user to sign up
                        Toast.makeText(
                            this@LoginActivity,
                            "User does not exist. Please sign up or double check your username and password.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Toast.makeText(
                        this@LoginActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }
}