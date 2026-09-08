package com.example.khula

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterStep2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_step2)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnNextStep = findViewById<Button>(R.id.btnNextStep)

        btnBack.setOnClickListener {
            val intent = Intent(this, RegisterStep1::class.java)
            startActivity(intent)
        }

        btnNextStep.setOnClickListener {
            val intent = Intent(this, RegisterStep3::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}