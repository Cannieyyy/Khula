package com.example.khula

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.khula.Data.Models.BookingModels
import com.example.khula.Data.remote.RetrofitClient
import kotlinx.coroutines.launch

class ProviderRequestDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_request_details)

        // Get booking information
        val bookingId = intent.getIntExtra("bookingId", -1)
        val customerName = intent.getStringExtra("customerName")
        val serviceTitle = intent.getStringExtra("serviceTitle")
        val customerSuburb = intent.getStringExtra("customerSuburb")
        val preferredDate = intent.getStringExtra("preferredDate")
        val preferredTime = intent.getStringExtra("preferredTime")
        val status = intent.getStringExtra("status")
        val description = intent.getStringExtra("description")

        // Find TextViews
        val tvCustomerName =
            findViewById<TextView>(R.id.tvCustomerName)

        val tvServiceTitle =
            findViewById<TextView>(R.id.tvServiceTitle)

        val tvPreferredDate =
            findViewById<TextView>(R.id.tvPreferredDate)

        val tvPreferredTime =
            findViewById<TextView>(R.id.tvPreferredTime)

        val tvCustomerLocation =
            findViewById<TextView>(R.id.tvCustomerLocation)

        val tvDescription =
            findViewById<TextView>(R.id.tvDescription)

        val tvStatus =
            findViewById<TextView>(R.id.tvStatus)

        // Find buttons
        val btnBack =
            findViewById<ImageView>(R.id.btnBack)

        val btnDecline =
            findViewById<Button>(R.id.btnDecline)

        val btnAccept =
            findViewById<Button>(R.id.btnAccept)

        // Display booking information
        tvCustomerName.text =
            customerName ?: "Customer"

        tvServiceTitle.text =
            serviceTitle ?: "Service"

        tvPreferredDate.text =
            preferredDate ?: "Date not set"

        tvPreferredTime.text =
            preferredTime ?: "Time not set"

        tvCustomerLocation.text =
            customerSuburb ?: "Location not set"

        tvDescription.text =
            description ?: "No description provided"

        tvStatus.text =
            status ?: "Pending"

        // Back button
        btnBack.setOnClickListener {
            finish()
        }

        // Accept button
        btnAccept.setOnClickListener {

            if (bookingId == -1) {

                Toast.makeText(
                    this,
                    "Invalid booking.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            updateBookingStatus(
                bookingId,
                "Accepted"
            )
        }

        // Decline button
        btnDecline.setOnClickListener {

            if (bookingId == -1) {

                Toast.makeText(
                    this,
                    "Invalid booking.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            updateBookingStatus(
                bookingId,
                "Declined"
            )
        }
    }

    private fun updateBookingStatus(
        bookingId: Int,
        newStatus: String
    ) {

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.api.updateBookingStatus(
                        bookingId,
                        BookingModels.UpdateBookingStatusRequest(
                            newStatus
                        )
                    )

                if (response.isSuccessful) {

                    Toast.makeText(
                        this@ProviderRequestDetails,
                        "Request $newStatus.",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    val error =
                        response.errorBody()?.string()
                            ?: "Could not update request."

                    Toast.makeText(
                        this@ProviderRequestDetails,
                        error,
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@ProviderRequestDetails,
                    "Network error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}