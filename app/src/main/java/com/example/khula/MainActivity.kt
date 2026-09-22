package com.example.khula

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, SplashScreen::class.java)
        startActivity(intent)

        // Close MainActivity so it doesn't sit in the back stack
        finish()
    }
}

