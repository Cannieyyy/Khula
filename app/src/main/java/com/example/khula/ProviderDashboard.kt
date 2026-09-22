package com.example.khula

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.khula.Data.remote.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class ProviderDashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_provider_dashboard)

        val tvProviderName = findViewById<TextView>(R.id.tvProviderName)

        // Fetch logged-in user's name from the API
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getMyProfile()
                if (response.isSuccessful) {
                    val body = response.body()
                    val fullName = "${body?.firstName ?: ""} ${body?.lastName ?: ""}".trim()
                    if (fullName.isNotEmpty()) tvProviderName.text = fullName

                    findViewById<TextView>(R.id.tvProviderLocation).text =
                        body?.suburb ?: body?.city ?: "Location not set"

                } else {
                    val err = response.errorBody()?.string() ?: "err ${response.code()}"
                    Toast.makeText(this@ProviderDashboard, err, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProviderDashboard, "Network: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_dashboard
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true
                R.id.nav_services -> {
                    startActivity(android.content.Intent(this, ServicesActivity::class.java))
                    true
                }
                R.id.nav_bookings -> {
                    Toast.makeText(this, "Bookings — coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_messages -> {
                    Toast.makeText(this, "Messages — coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(android.content.Intent(this, Settings::class.java))
                    true
                }
                else -> false
            }
        }

        // See All link
        findViewById<TextView>(R.id.tvSeeAll).setOnClickListener {
            Toast.makeText(this, "All requests — coming soon", Toast.LENGTH_SHORT).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}