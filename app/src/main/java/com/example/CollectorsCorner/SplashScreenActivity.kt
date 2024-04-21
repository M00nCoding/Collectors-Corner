package com.example.CollectorsCorner


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity



@SuppressLint("CustomSplashScreen")
class SplashScreenActivity: AppCompatActivity() {

    private val splashScreenDelay = 2000L // Splash screen delay in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        // Apply animations to views
        val imageView2 = findViewById<ImageView>(R.id.imageView2)
        val topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        imageView2.startAnimation(topAnimation)

        // Navigate to LoginActivity after splash screen delay
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Finish SplashScreenActivity to prevent going back to it
        }, splashScreenDelay)
    }

}