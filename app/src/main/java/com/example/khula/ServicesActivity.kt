package com.example.khula

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.khula.Data.Models.ServiceModels.ServiceResponse
import com.example.khula.Data.remote.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ServicesActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_services)

        container = findViewById(R.id.servicesContainer)
        tvEmpty = findViewById(R.id.tvEmpty)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, AddServiceActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        loadServices()
    }

    private fun loadServices() {
        container.removeAllViews()
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getMyServices()
                if (response.isSuccessful) {
                    val services = response.body() ?: emptyList()
                    if (services.isEmpty()) {
                        tvEmpty.visibility = TextView.VISIBLE
                    } else {
                        tvEmpty.visibility = TextView.GONE
                        services.forEach { renderService(it) }
                    }
                } else {
                    Toast.makeText(
                        this@ServicesActivity,
                        "Error ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ServicesActivity, "Network: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun renderService(service: ServiceResponse) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_service, container, false)

        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        view.findViewById<TextView>(R.id.tvTitle).text = service.title ?: "Untitled"
        view.findViewById<TextView>(R.id.tvDescription).text = service.description ?: ""
        view.findViewById<TextView>(R.id.tvCategory).text = service.category ?: ""
        view.findViewById<TextView>(R.id.tvPrice).text = "R${service.price ?: 0.0}"
        view.findViewById<TextView>(R.id.tvDuration).text = "${service.durationMinutes ?: 0} min"

        view.findViewById<TextView>(R.id.tvDelete).setOnClickListener {
            confirmDelete(service)
        }

        container.addView(view)
    }

    private fun confirmDelete(service: ServiceResponse) {
        AlertDialog.Builder(this)
            .setTitle("Delete service")
            .setMessage("Delete \"${service.title}\"? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteService(service)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteService(service: ServiceResponse) {
        lifecycleScope.launch {
            try {
                val id = service.serviceId ?: return@launch
                val response = RetrofitClient.api.deleteService(id)
                if (response.isSuccessful) {
                    Toast.makeText(this@ServicesActivity, "Deleted", Toast.LENGTH_SHORT).show()
                    loadServices()
                } else {
                    Toast.makeText(this@ServicesActivity, "Error ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ServicesActivity, "Network: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}