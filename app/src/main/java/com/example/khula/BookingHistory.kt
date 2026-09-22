package com.example.khula

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khula.Data.Models.BookingModels
import com.example.khula.Data.remote.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class BookingHistory : AppCompatActivity() {

    // =========================================================
    // UI
    // =========================================================

    private lateinit var rvBookingHistory:
            RecyclerView

    private lateinit var progressBookings:
            ProgressBar

    private lateinit var tvNoBookings:
            TextView


    // =========================================================
    // ADAPTER
    // =========================================================

    private lateinit var bookingAdapter:
            BookingHistoryAdapter


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(
            R.layout.activity_booking_history
        )


        // =====================================================
        // FIND VIEWS
        // =====================================================

        rvBookingHistory =
            findViewById(
                R.id.rvBookingHistory
            )

        progressBookings =
            findViewById(
                R.id.progressBookings
            )

        tvNoBookings =
            findViewById(
                R.id.tvNoBookings
            )


        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        // Create adapter with an empty list initially.
        bookingAdapter =
            BookingHistoryAdapter(
                emptyList()
            )


        // Display bookings vertically.
        rvBookingHistory.layoutManager =
            LinearLayoutManager(
                this
            )


        // Connect adapter.
        rvBookingHistory.adapter =
            bookingAdapter


        // =====================================================
        // BOTTOM NAVIGATION
        // =====================================================

        setupBottomNavigation()


        // =====================================================
        // SYSTEM BARS
        // =====================================================

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )


            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )


            insets
        }
    }


    // =========================================================
    // ON RESUME
    // =========================================================

    override fun onResume() {

        super.onResume()


        // Reload bookings every time the customer
        // returns to this screen.
        //
        // This means a newly created request can
        // appear without restarting the application.
        loadBookings()
    }


    // =========================================================
    // LOAD REAL CUSTOMER BOOKINGS
    // =========================================================

    private fun loadBookings() {

        // Show loading indicator.
        progressBookings.visibility =
            View.VISIBLE


        // Hide empty state while loading.
        tvNoBookings.visibility =
            View.GONE


        lifecycleScope.launch {

            try {

                // =================================================
                // CALL BACKEND
                // =================================================

                // GET api/Bookings/customer
                val response =
                    RetrofitClient.api
                        .getMyBookingsAsCustomer()


                // Hide loader when request finishes.
                progressBookings.visibility =
                    View.GONE


                // =================================================
                // SUCCESS
                // =================================================

                if (response.isSuccessful) {

                    val bookings:
                            List<BookingModels.BookingResponse> =
                        response.body()
                            ?: emptyList()


                    // Update RecyclerView.
                    bookingAdapter.updateData(
                        bookings
                    )


                    // =================================================
                    // EMPTY LIST
                    // =================================================

                    if (bookings.isEmpty()) {

                        rvBookingHistory.visibility =
                            View.GONE

                        tvNoBookings.text =
                            "No bookings yet"

                        tvNoBookings.visibility =
                            View.VISIBLE


                    } else {

                        // We have real bookings.
                        rvBookingHistory.visibility =
                            View.VISIBLE

                        tvNoBookings.visibility =
                            View.GONE
                    }


                } else {

                    // =================================================
                    // API ERROR
                    // =================================================

                    rvBookingHistory.visibility =
                        View.GONE


                    tvNoBookings.text =
                        "Could not load your bookings."


                    tvNoBookings.visibility =
                        View.VISIBLE


                    Toast.makeText(
                        this@BookingHistory,
                        "Could not load bookings: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }


            } catch (e: Exception) {

                // =================================================
                // NETWORK ERROR
                // =================================================

                progressBookings.visibility =
                    View.GONE


                rvBookingHistory.visibility =
                    View.GONE


                tvNoBookings.text =
                    "Unable to load bookings. Check your connection."


                tvNoBookings.visibility =
                    View.VISIBLE


                Toast.makeText(
                    this@BookingHistory,
                    "Network: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // =========================================================
    // BOTTOM NAVIGATION
    // =========================================================

    private fun setupBottomNavigation() {

        val bottomNavigation =
            findViewById<BottomNavigationView>(
                R.id.bottomNavigation
            )


        // We are currently on the bookings page.
        bottomNavigation.selectedItemId =
            R.id.nav_bookings


        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {


                // =================================================
                // HOME
                // =================================================

                R.id.nav_home -> {

                    // Return to CustomerHome.
                    startActivity(
                        Intent(
                            this,
                            CustomerHome::class.java
                        )
                    )

                    // Close BookingHistory so we do not
                    // unnecessarily stack duplicate screens.
                    finish()

                    true
                }


                // =================================================
                // BOOKINGS
                // =================================================

                R.id.nav_bookings -> {

                    // Already on this page.
                    true
                }


                // =================================================
                // MESSAGES
                // =================================================

                R.id.nav_messages -> {

                    Toast.makeText(
                        this,
                        "Messages — coming soon",
                        Toast.LENGTH_SHORT
                    ).show()

                    true
                }


                // =================================================
                // FAVOURITES
                // =================================================

                R.id.nav_favorites -> {

                    Toast.makeText(
                        this,
                        "Favourites — coming soon",
                        Toast.LENGTH_SHORT
                    ).show()

                    true
                }


                // =================================================
                // PROFILE
                // =================================================

                R.id.nav_profile -> {

                    startActivity(
                        Intent(
                            this,
                            Settings::class.java
                        )
                    )

                    true
                }


                else -> false
            }
        }
    }
}