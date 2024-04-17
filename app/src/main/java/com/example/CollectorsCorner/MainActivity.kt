package com.example.CollectorsCorner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Find the login_link TextView
        val loginLinkTextView: TextView = findViewById(R.id.login_link)

        // Set OnClickListener to the login_link TextView
        loginLinkTextView.setOnClickListener {
            // Create Intent to navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            // Start the LoginActivity
            startActivity(intent)
        }

    }
}