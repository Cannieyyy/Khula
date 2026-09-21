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
import com.example.khula.Data.SessionManager

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

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

        handler.postDelayed({ fadeInView(ivLogoPeople) }, 300)
        handler.postDelayed({ fadeInView(ivLogoPlant) }, 1000)
        handler.postDelayed({ fadeInView(ivLogoName) }, 1700)
        handler.postDelayed({ fadeInView(ivLogoTagline) }, 2400)

        // Check login state and route accordingly
        handler.postDelayed({
            val session = SessionManager(this)

            if (session.isLoggedIn()) {
                // Token exists → skip login
                val accountType = session.getAccountType() ?: "Customer"
                val intent = if (accountType == "Provider") {
                    Intent(this, ProviderDashboard::class.java)
                } else {
                    Intent(this, CustomerHome::class.java)
                }
                startActivity(intent)
            } else {
                // No token → Login
                startActivity(Intent(this, Login::class.java))
            }
            finish()
        }, 3800)
    }

    private fun fadeInView(view: View) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(600)
            .start()
    }
}