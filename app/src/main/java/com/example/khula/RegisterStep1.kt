package com.example.khula

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterStep1 : AppCompatActivity() {

    private var selectedRole: String = "Customer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_step1)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnNextStep = findViewById<Button>(R.id.btnNextStep)
        val cardCustomer = findViewById<LinearLayout>(R.id.cardCustomer)
        val cardProvider = findViewById<LinearLayout>(R.id.cardProvider)

        // If the user came BACK from Step 2, restore their previous selection
        val previousRole = intent.getStringExtra("accountType")
        if (previousRole == "Provider") {
            selectedRole = "Provider"
            cardProvider.setBackgroundResource(R.drawable.bg_role_card_selected)
            cardCustomer.setBackgroundResource(R.drawable.bg_role_card_default)
        }

        btnBack.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

        // THE KEY CHANGE: pass accountType to Step 2
        btnNextStep.setOnClickListener {
            val intent = Intent(this, RegisterStep2::class.java)
            intent.putExtra("accountType", selectedRole)
            startActivity(intent)
        }

        cardCustomer.setOnClickListener {
            selectedRole = "Customer"
            cardCustomer.setBackgroundResource(R.drawable.bg_role_card_selected)
            cardProvider.setBackgroundResource(R.drawable.bg_role_card_default)
        }

        cardProvider.setOnClickListener {
            selectedRole = "Provider"
            cardProvider.setBackgroundResource(R.drawable.bg_role_card_selected)
            cardCustomer.setBackgroundResource(R.drawable.bg_role_card_default)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}