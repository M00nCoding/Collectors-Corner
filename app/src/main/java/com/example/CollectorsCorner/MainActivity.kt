package com.example.CollectorsCorner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.TextView
import android.util.Patterns
//import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.widget.Button
import android.widget.CheckBox
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuthUserCollisionException
//import com.google.firebase.database.DatabaseReference


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var registerButton: Button // Declare registerButton as a class-level property
    private lateinit var termsCheckBox: CheckBox // declares the checkBox for the agreement of terms of service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        // Find the register_button Button
        registerButton = findViewById(R.id.register_button)

        // Find other views
        val loginLinkTextView: TextView = findViewById(R.id.login_link)
        val emailEditText: EditText = findViewById(R.id.email_edittext)
        val usernameEditText: EditText = findViewById(R.id.username_edittext)
        val passwordEditText: EditText = findViewById(R.id.password_edittext)

        // Find the termsCheckBox
        termsCheckBox = findViewById(R.id.terms_checkBox)

        // Set OnClickListener to the registerButton
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val termsChecked = termsCheckBox.isChecked



            // checks if all fields are empty
            if (email.isEmpty() && username.isEmpty() && password.isEmpty()) {
                Toast.makeText(this, "All fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Check if username field is empty
            if (username.isEmpty()) {
                Toast.makeText(this@MainActivity, "Username cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // checks if the email field are empty
            if (email.isEmpty()) {
                Toast.makeText(this@MainActivity, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // checks if the password field is empty
            if (password.isEmpty()) {
                Toast.makeText(this@MainActivity, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if Terms of Service and Privacy Policy checkbox is checked
            if (!termsChecked) {
                Toast.makeText(this@MainActivity, "Please accept the Terms of Service and Privacy Policy", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // checks if the inputted info is a valid email address
            if (!isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // checks if the inputted username is less than 10 characters long
            if (!isValidUsername(username)) {
                Toast.makeText(this, "Username must be less than 10 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the password meets the requirements
            if (!isValidPassword(password)) {
                // Display validation message for invalid password
                Toast.makeText(this@MainActivity, "Password must be at least 8 characters long and contain both letters and numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            registerUser(email, password, username)
        }

        // Set OnClickListener to the login_link TextView
        loginLinkTextView.setOnClickListener {
            // Create Intent to navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            // Start the LoginActivity
            startActivity(intent)
        }
    }

// register user function
    private fun registerUser(email: String, password: String, username: String) {
    // Disable the register button to prevent multiple clicks
    registerButton.isEnabled = false


    // Check if the password field is valid
    if (!isValidPassword(password)) {
        // Enable the register button
        registerButton.isEnabled = true
        return
    }
    // Reference to the Firebase Realtime Database
    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("User") // Adjusted node name

    // Check if the username already exists
    usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                // Username already exists
                Toast.makeText(this@MainActivity, "Username already exists. Please choose another username.", Toast.LENGTH_SHORT).show()
                // Enable the register button
                registerButton.isEnabled = true
            } else {
                // Username is unique, proceed to check email existence
                checkEmailExistence(email, password, username)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("FirebaseDatabase", "Error checking username existence", databaseError.toException())
            Toast.makeText(this@MainActivity, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
            // Enable the register button
            registerButton.isEnabled = true
        }
    })
}

    // Function to check email existence
    private fun checkEmailExistence(email: String, password: String, username: String) {
        // Reference to the Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("User") // Adjusted node name

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email already exists in the database
                    Toast.makeText(this@MainActivity, "Email already exists. Please choose another email.", Toast.LENGTH_SHORT).show()
                    // Enable the register button
                    registerButton.isEnabled = true
                } else {
                    // Email is unique, proceed to register the user
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this@MainActivity) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                val uid = user?.uid

                                val newUser = mapOf(
                                    "uid" to uid,
                                    "username" to username,
                                    "email" to email
                                )

                                // Save user data to the Firebase Realtime Database
                                usersRef.child(uid!!).setValue(newUser)
                                    .addOnSuccessListener {
                                        Toast.makeText(baseContext, "User Sign-Up successful!", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        if (e is FirebaseAuthUserCollisionException) {
                                            // User already exists with this email
                                            Toast.makeText(this@MainActivity, "The email address is already in use by another account. Please sign in or use a different email.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            // Other failure reasons
                                            Log.e("FirebaseDatabase", "Error adding user", e)
                                            Toast.makeText(baseContext, "User Sign-Up failed. Please try again.", Toast.LENGTH_SHORT).show()
                                        }
                                        // Enable the register button
                                        registerButton.isEnabled = true
                                    }
                            } else {
                                // Registration failed
                                if (task.exception != null) {
                                    val errorMessage = task.exception!!.message
                                    if (errorMessage != null) {
                                        Toast.makeText(baseContext, errorMessage, Toast.LENGTH_SHORT).show()
                                    } else {
                                        Log.e("FirebaseAuth", "User Sign-Up failed", task.exception)
                                        Toast.makeText(baseContext, "User Sign-Up failed. Please try again.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                // Enable the register button
                                registerButton.isEnabled = true
                            }
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseDatabase", "Error checking email existence", databaseError.toException())
                Toast.makeText(this@MainActivity, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
                // Enable the register button
                registerButton.isEnabled = true
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidUsername(username: String): Boolean {
        return username.length < 10
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$".toRegex()
        return passwordPattern.matches(password)
    }



}


