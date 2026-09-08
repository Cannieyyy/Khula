package com.example.khula

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        // Find views by ID
        val ivLogoPeople = findViewById<ImageView>(R.id.ivLogoPeople)
        val ivLogoPlant = findViewById<ImageView>(R.id.ivLogoPlant)
        val ivLogoName = findViewById<ImageView>(R.id.ivLogoName)
        val ivLogoTagline = findViewById<ImageView>(R.id.ivLogoTagline)

        val handler = Handler(Looper.getMainLooper())
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fade in People
        handler.postDelayed({
            fadeInView(ivLogoPeople)
        }, 300)

        // Fade in Plant
        handler.postDelayed({
            fadeInView(ivLogoPlant)
        }, 1000)

        // Fade in Khula text
        handler.postDelayed({
            fadeInView(ivLogoName)
        }, 1700)

        //Fade in Tagline
        handler.postDelayed({
            fadeInView(ivLogoTagline)
        }, 2400)

        //Navigate to LoginActivity
        handler.postDelayed({
            startActivity(Intent(this, Login::class.java))
            finish() // Prevents returning to splash screen on Back press
        }, 3800)
    }


     //Helper function to smoothly fade in a View

    private fun fadeInView(view: View) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(600) // Fade-in duration in milliseconds
            .start()
    }

}