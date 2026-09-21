package com.example.khula

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.khula.Data.SessionManager

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Row taps
        findViewById<LinearLayout>(R.id.rowAccountInfo).setOnClickListener {
            startActivity(android.content.Intent(this, AccountInformation::class.java))
        }
        findViewById<LinearLayout>(R.id.rowChangePassword).setOnClickListener {
            Toast.makeText(this, "Change Password — coming soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<LinearLayout>(R.id.rowLanguage).setOnClickListener {
            Toast.makeText(this, "Language — coming soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<LinearLayout>(R.id.rowNotifications).setOnClickListener {
            Toast.makeText(this, "Notifications — coming soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<LinearLayout>(R.id.rowHelp).setOnClickListener {
            Toast.makeText(this, "Help & Support — coming soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<LinearLayout>(R.id.rowPrivacy).setOnClickListener {
            Toast.makeText(this, "Privacy Policy — coming soon", Toast.LENGTH_SHORT).show()
        }

        // Dark Mode switch
        findViewById<SwitchCompat>(R.id.switchDarkMode).setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                if (isChecked) "Dark Mode ON (coming soon)" else "Dark Mode OFF",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Log Out
        findViewById<LinearLayout>(R.id.rowLogout).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out") { _, _ ->
                    // Clear saved token
                    SessionManager(this).clearSession()

                    // Go to Login and clear back stack
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}