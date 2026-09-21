package com.example.khula

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.khula.Data.remote.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class CustomerHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_home)

        // Fetch logged-in user's name from the API
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getMyProfile()
                if (response.isSuccessful) {
                    val body = response.body()
                    val fullName = "${body?.firstName ?: ""} ${body?.lastName ?: ""}".trim()

                } else {
                    val err = response.errorBody()?.string() ?: "err ${response.code()}"
                    Toast.makeText(this@CustomerHome, err, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CustomerHome, "Network: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

// Bottom navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_dashboard
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true

                R.id.nav_bookings -> {
                    startActivity(android.content.Intent(this, BookingHistory::class.java))
                    true
                }
                R.id.nav_messages -> {
                    Toast.makeText(this, "Messages — coming soon", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_favorites -> {
                    Toast.makeText(this, "Favourites — coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(android.content.Intent(this, Settings::class.java))
                    true
                }
                else -> false
            }
        }

       // See All links
        findViewById<TextView>(R.id.btnSeeAllRecommended).setOnClickListener {
            Toast.makeText(this, "All requests — coming soon", Toast.LENGTH_SHORT).show()
        }








        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}