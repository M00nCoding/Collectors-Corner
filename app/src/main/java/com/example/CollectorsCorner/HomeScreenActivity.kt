package com.example.CollectorsCorner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class HomeScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Display a success message
        Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_SHORT).show()

    }
}