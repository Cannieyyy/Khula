package com.example.khula

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.khula.Data.Models.BookingModels
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

                    val fullName =
                        "${body?.firstName ?: ""} ${body?.lastName ?: ""}".trim()

                    if (fullName.isNotEmpty()) {
                        tvProviderName.text = fullName
                    }

                    findViewById<TextView>(R.id.tvProviderLocation).text =
                        body?.suburb ?: body?.city ?: "Location not set"

                } else {
                    val err = response.errorBody()?.string()
                        ?: "err ${response.code()}"

                    Toast.makeText(
                        this@ProviderDashboard,
                        err,
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@ProviderDashboard,
                    "Network: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Load provider requests
        loadProviderRequests()

        // Bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.selectedItemId = R.id.nav_dashboard

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_dashboard -> true

                R.id.nav_services -> {
                    startActivity(
                        android.content.Intent(
                            this,
                            ServicesActivity::class.java
                        )
                    )
                    true
                }

                R.id.nav_bookings -> {
                    Toast.makeText(
                        this,
                        "Bookings — coming soon",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                R.id.nav_messages -> {
                    Toast.makeText(
                        this,
                        "Messages — coming soon",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                R.id.nav_profile -> {
                    startActivity(
                        android.content.Intent(
                            this,
                            Settings::class.java
                        )
                    )
                    true
                }

                else -> false
            }
        }

        // See All link
        findViewById<TextView>(R.id.tvSeeAll).setOnClickListener {
            Toast.makeText(
                this,
                "All requests — coming soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    private fun loadProviderRequests() {

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.api.getMyBookingsAsProvider()

                if (response.isSuccessful) {

                    val bookings = response.body() ?: emptyList()

                    displayRequests(bookings.take(2))

                } else {

                    Toast.makeText(
                        this@ProviderDashboard,
                        "Could not load requests: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@ProviderDashboard,
                    "Request error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun displayRequests(
        bookings: List<BookingModels.BookingResponse>
    ) {

        val requestsContainer =
            findViewById<LinearLayout>(R.id.requestsContainer)

        requestsContainer.removeAllViews()

        if (bookings.isEmpty()) {

            val emptyText = TextView(this)

            emptyText.text = "No requests yet"
            emptyText.textSize = 14f
            emptyText.setTextColor(
                android.graphics.Color.parseColor("#64748B")
            )
            emptyText.gravity = Gravity.CENTER
            emptyText.setPadding(0, 20, 0, 20)

            requestsContainer.addView(emptyText)

            return
        }

        for (booking in bookings) {

            val requestCard = LinearLayout(this)

            requestCard.orientation = LinearLayout.HORIZONTAL
            requestCard.gravity = Gravity.CENTER_VERTICAL
            requestCard.setPadding(12, 12, 12, 12)

            val cardParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            cardParams.bottomMargin = 10

            requestCard.layoutParams = cardParams

            requestCard.setBackgroundResource(
                R.drawable.bg_request_card
            )

            val details = LinearLayout(this)

            details.orientation = LinearLayout.VERTICAL

            val detailsParams =
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            detailsParams.weight = 1f
            detailsParams.setMargins(12, 0, 0, 0)
            details.layoutParams = detailsParams

            val customerName = TextView(this)

            customerName.text =
                booking.customerName ?: "Customer"

            customerName.textSize = 14f
            customerName.setTextColor(
                android.graphics.Color.parseColor("#0F172A")
            )
            customerName.setTypeface(null, android.graphics.Typeface.BOLD)

            val serviceDate = TextView(this)

            serviceDate.text =
                "${booking.serviceTitle ?: "Service"} • " +
                        "${booking.preferredDate ?: "Date not set"}"

            serviceDate.textSize = 12f
            serviceDate.setTextColor(
                android.graphics.Color.parseColor("#64748B")
            )

            val location = TextView(this)

            location.text =
                booking.customerSuburb ?: "Location not set"

            location.textSize = 12f
            location.setTextColor(
                android.graphics.Color.parseColor("#64748B")
            )

            details.addView(customerName)
            details.addView(serviceDate)
            details.addView(location)

            val status = TextView(this)

            status.text =
                booking.status ?: "Pending"

            status.textSize = 11f
            status.setTypeface(null, android.graphics.Typeface.BOLD)
            status.setPadding(14, 6, 14, 6)

            status.setBackgroundResource(
                R.drawable.bg_status_pending
            )

            status.setTextColor(
                android.graphics.Color.parseColor("#D97706")
            )

            requestCard.addView(details)
            requestCard.addView(status)

            requestsContainer.addView(requestCard)
        }
    }
}