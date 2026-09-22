package com.example.khula

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.khula.Data.Models.BookingModels.CreateBookingRequest
import com.example.khula.Data.remote.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Calendar

class RequestServiceActivity : AppCompatActivity() {

    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_request_service)

        val serviceId = intent.getIntExtra("serviceId", -1)
        val providerName = intent.getStringExtra("providerName") ?: "Provider"
        val serviceTitle = intent.getStringExtra("serviceTitle") ?: "Service"
        val providerRegion = intent.getStringExtra("providerRegion") ?: ""

        findViewById<TextView>(R.id.tvProviderName).text = providerName
        findViewById<TextView>(R.id.tvServiceSummary).text = "$serviceTitle · $providerRegion"

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val tvDate = findViewById<TextView>(R.id.tvDate)
        val tvTime = findViewById<TextView>(R.id.tvTime)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnSend = findViewById<Button>(R.id.btnSendRequest)

        tvDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                selectedDate = "%04d-%02d-%02d".format(year, month + 1, day)
                tvDate.text = "%02d/%02d/%04d".format(day, month + 1, year)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        tvTime.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->
                selectedTime = "%02d:%02d".format(hour, minute)
                tvTime.text = selectedTime
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        btnSend.setOnClickListener {
            if (serviceId == -1) {
                Toast.makeText(this, "Missing service info", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedDate == null || selectedTime == null) {
                Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSend.isEnabled = false
            btnSend.text = "Sending..."

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.createBooking(
                        CreateBookingRequest(
                            serviceID = serviceId,
                            preferredDate = selectedDate!!,
                            preferredTime = selectedTime!!,
                            description = etDescription.text.toString().trim().ifEmpty { null }
                        )
                    )

                    if (response.isSuccessful) {
                        Toast.makeText(this@RequestServiceActivity, "Request sent!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val err = response.errorBody()?.string() ?: "Error ${response.code()}"
                        Toast.makeText(this@RequestServiceActivity, err, Toast.LENGTH_LONG).show()
                        btnSend.isEnabled = true
                        btnSend.text = "Send Request"
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@RequestServiceActivity, "Network: ${e.message}", Toast.LENGTH_LONG).show()
                    btnSend.isEnabled = true
                    btnSend.text = "Send Request"
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}