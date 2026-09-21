package com.example.khula

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.khula.Data.Models.ServiceModels.CreateServiceRequest
import com.example.khula.Data.remote.RetrofitClient
import kotlinx.coroutines.launch

class AddServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_service)

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etCategory = findViewById<EditText>(R.id.etCategory)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val etDuration = findViewById<EditText>(R.id.etDuration)
        val btnSave = findViewById<Button>(R.id.btnSave)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val category = etCategory.text.toString().trim()
            val priceStr = etPrice.text.toString().trim()
            val durationStr = etDuration.text.toString().trim()

            if (title.isEmpty() || category.isEmpty() || priceStr.isEmpty() || durationStr.isEmpty()) {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull()
            val duration = durationStr.toIntOrNull()

            if (price == null || duration == null) {
                Toast.makeText(this, "Invalid price or duration", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSave.isEnabled = false
            btnSave.text = "Saving..."

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.createService(
                        CreateServiceRequest(
                            title = title,
                            description = description,
                            category = category,
                            price = price,
                            durationMinutes = duration
                        )
                    )

                    if (response.isSuccessful) {
                        Toast.makeText(this@AddServiceActivity, "Service saved!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val err = response.errorBody()?.string() ?: "Error ${response.code()}"
                        Toast.makeText(this@AddServiceActivity, err, Toast.LENGTH_LONG).show()
                        btnSave.isEnabled = true
                        btnSave.text = "Save Service"
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@AddServiceActivity, "Network: ${e.message}", Toast.LENGTH_LONG).show()
                    btnSave.isEnabled = true
                    btnSave.text = "Save Service"
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