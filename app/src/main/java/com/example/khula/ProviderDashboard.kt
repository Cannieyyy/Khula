package com.example.khula

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.khula.Data.Models.BookingModels
import com.example.khula.Data.remote.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.util.Locale

class ProviderDashboard : AppCompatActivity() {

    // =========================================================
    // LOCATION
    // =========================================================
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var profileLocation: String = "Location not set"

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1002 // Unique code for provider
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_provider_dashboard)

        // Initialise Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Overview TextViews
        val tvNewRequests = findViewById<TextView>(R.id.tvNewRequests)
        val tvIncoming = findViewById<TextView>(R.id.tvIncoming)
        val tvCompleted = findViewById<TextView>(R.id.tvCompleted)
        val tvRating = findViewById<TextView>(R.id.tvRating)

        // Load provider requests
        loadProviderRequests()

        // Load provider overview
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getProviderOverview()

                if (response.isSuccessful) {
                    val overview = response.body()

                    tvNewRequests.text = overview?.newRequests?.toString() ?: "0"
                    tvIncoming.text = overview?.incoming?.toString() ?: "0"
                    tvCompleted.text = overview?.completed?.toString() ?: "0"
                    tvRating.text = overview?.rating?.toString() ?: "0.0"

                } else {
                    Toast.makeText(
                        this@ProviderDashboard,
                        "Could not load overview: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@ProviderDashboard,
                    "Overview error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
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

        // Check GPS Location
        checkLocationPermission()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()

        loadProfileData()
        loadProviderRequests()
    }

    private fun loadProfileData() {
        val tvProviderName = findViewById<TextView>(R.id.tvProviderName)
        val tvProviderLocation = findViewById<TextView>(R.id.tvProviderLocation)
        val ivAvatar = findViewById<ImageView>(R.id.ivAvatar) // Make sure your XML has this ID!

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getMyProfile()

                if (response.isSuccessful) {
                    val body = response.body()

                    val fullName = "${body?.firstName ?: ""} ${body?.lastName ?: ""}".trim()
                    if (fullName.isNotEmpty()) {
                        tvProviderName.text = fullName
                    }

                    // Save profile location as a fallback
                    profileLocation = listOfNotNull(body?.suburb, body?.city, body?.province)
                        .filter { it.isNotBlank() }
                        .distinct()
                        .joinToString(", ")

                    if (profileLocation.isBlank()) {
                        profileLocation = "Location not set"
                    }

                    // Show profile location temporarily (GPS will override if available)
                    tvProviderLocation.text = profileLocation

                    if (!body?.avatarUrl.isNullOrEmpty()) {
                        Glide.with(this@ProviderDashboard)
                            .load(body.avatarUrl)
                            .placeholder(R.drawable.ic_customer)
                            .into(ivAvatar)
                    }
                } else {
                    val err = response.errorBody()?.string() ?: "err ${response.code()}"
                    Toast.makeText(this@ProviderDashboard, err, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@ProviderDashboard, "Network: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // =========================================================
    // LOCATION METHODS
    // =========================================================

    private fun checkLocationPermission() {
        val fineLocationGranted = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            val permissionGranted = grantResults.any { it == PackageManager.PERMISSION_GRANTED }

            if (permissionGranted) {
                getCurrentLocation()
            } else {
                showProfileLocation()
            }
        }
    }

    private fun getCurrentLocation() {
        val fineLocationGranted = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationGranted && !coarseLocationGranted) {
            showProfileLocation()
            return
        }

        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            )
            .addOnSuccessListener { location ->
                if (location != null) {
                    getAddressFromLocation(location.latitude, location.longitude)
                } else {
                    showProfileLocation()
                }
            }
            .addOnFailureListener {
                showProfileLocation()
            }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val address = addresses?.firstOrNull()

            if (address != null) {
                // Get suburb/local area.
                val area = address.subLocality ?: address.locality ?: address.subAdminArea
                // Get city.
                val city = address.locality ?: address.subAdminArea ?: address.adminArea

                // Build readable location.
                val readableLocation = listOfNotNull(area, city)
                    .filter { it.isNotBlank() }
                    .distinct()
                    .joinToString(", ")

                // Display GPS location when available.
                if (readableLocation.isNotBlank()) {
                    findViewById<TextView>(R.id.tvProviderLocation).text = readableLocation
                } else {
                    showProfileLocation()
                }
            } else {
                showProfileLocation()
            }
        } catch (_: Exception) {
            showProfileLocation()
        }
    }

    private fun showProfileLocation() {
        findViewById<TextView>(R.id.tvProviderLocation).text = profileLocation
    }

    // =========================================================
    // BOOKINGS
    // =========================================================

    private fun loadProviderRequests() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getMyBookingsAsProvider()

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

    private fun displayRequests(bookings: List<BookingModels.BookingResponse>) {
        val requestsContainer = findViewById<LinearLayout>(R.id.requestsContainer)
        requestsContainer.removeAllViews()

        if (bookings.isEmpty()) {
            val emptyText = TextView(this)
            emptyText.text = "No requests yet"
            emptyText.textSize = 14f
            emptyText.setTextColor(android.graphics.Color.parseColor("#64748B"))
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

            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            cardParams.bottomMargin = 10
            requestCard.layoutParams = cardParams
            requestCard.setBackgroundResource(R.drawable.bg_request_card)

            val details = LinearLayout(this)
            details.orientation = LinearLayout.VERTICAL

            val detailsParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            detailsParams.weight = 1f
            detailsParams.setMargins(12, 0, 0, 0)
            details.layoutParams = detailsParams

            val customerName = TextView(this)
            customerName.text = booking.customerName ?: "Customer"
            customerName.textSize = 14f
            customerName.setTextColor(android.graphics.Color.parseColor("#0F172A"))
            customerName.setTypeface(null, android.graphics.Typeface.BOLD)

            val serviceDate = TextView(this)
            serviceDate.text = "${booking.serviceTitle ?: "Service"} • ${booking.preferredDate ?: "Date not set"}"
            serviceDate.textSize = 12f
            serviceDate.setTextColor(android.graphics.Color.parseColor("#64748B"))

            val location = TextView(this)
            location.text = booking.customerSuburb ?: "Location not set"
            location.textSize = 12f
            location.setTextColor(android.graphics.Color.parseColor("#64748B"))

            details.addView(customerName)
            details.addView(serviceDate)
            details.addView(location)

            val status = TextView(this)
            status.text = booking.status ?: "Pending"
            status.textSize = 11f
            status.setTypeface(null, android.graphics.Typeface.BOLD)
            status.setPadding(14, 6, 14, 6)
            status.setBackgroundResource(R.drawable.bg_status_pending)
            status.setTextColor(android.graphics.Color.parseColor("#D97706"))

            requestCard.addView(details)
            requestCard.addView(status)

            requestCard.setOnClickListener {
                val intent = android.content.Intent(this, ProviderRequestDetails::class.java)
                intent.putExtra("bookingId", booking.bookingId)
                intent.putExtra("customerName", booking.customerName)
                intent.putExtra("serviceTitle", booking.serviceTitle)
                intent.putExtra("customerSuburb", booking.customerSuburb)
                intent.putExtra("preferredDate", booking.preferredDate)
                intent.putExtra("preferredTime", booking.preferredTime)
                intent.putExtra("status", booking.status)
                intent.putExtra("description", booking.description)
                startActivity(intent)
            }

            requestsContainer.addView(requestCard)
        }
    }
}