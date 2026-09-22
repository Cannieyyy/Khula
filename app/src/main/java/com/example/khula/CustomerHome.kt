package com.example.khula

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khula.Data.Models.ServiceModels.ServiceBrowseResponse
import com.example.khula.Data.remote.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CustomerHome : AppCompatActivity() {

    private lateinit var rvCategories: RecyclerView
    private lateinit var rvRecommended: RecyclerView
    private lateinit var providerAdapter: ProviderServiceAdapter
    private var allServices: List<ServiceBrowseResponse> = emptyList()
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_home)

        rvCategories = findViewById(R.id.rvCategories)
        rvRecommended = findViewById(R.id.rvRecommendedProviders)
        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvRecommended.layoutManager = LinearLayoutManager(this)

        providerAdapter = ProviderServiceAdapter(emptyList()) { service ->
            val intent = android.content.Intent(this, RequestServiceActivity::class.java)
            intent.putExtra("serviceId", service.serviceId ?: -1)
            intent.putExtra("providerName", service.providerName ?: "Provider")
            intent.putExtra("serviceTitle", service.title ?: "Service")
            intent.putExtra("providerRegion", service.providerSuburb ?: service.providerCity ?: "")
            startActivity(intent)
        }
        rvRecommended.adapter = providerAdapter

        // Fetch logged-in user's name from the API
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getMyProfile()
                if (response.isSuccessful) {
                    val body = response.body()
                    val fullName = "${body?.firstName ?: ""} ${body?.lastName ?: ""}".trim()

                    findViewById<TextView>(R.id.tvCustomerName).text =
                        if (fullName.isNotEmpty()) fullName else "Customer"

                    findViewById<TextView>(R.id.tvUserLocation).text =
                        body?.suburb ?: body?.city ?: "Location not set"

                } else {
                    val err = response.errorBody()?.string() ?: "err ${response.code()}"
                    Toast.makeText(this@CustomerHome, err, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CustomerHome, "Network: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        loadServices()
        setupSearch()

        // Bottom navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_home
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

    private fun loadServices() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.browseServices()
                if (response.isSuccessful) {
                    allServices = response.body() ?: emptyList()
                    providerAdapter.updateData(allServices)

                    val categories = allServices.mapNotNull { it.category }.distinct()
                    rvCategories.adapter = CategoryAdapter(categories) { selected ->
                        val filtered = allServices.filter { it.category == selected }
                        providerAdapter.updateData(filtered)
                    }
                } else {
                    Toast.makeText(this@CustomerHome, "Error ${response.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CustomerHome, "Network: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSearch() {
        findViewById<EditText>(R.id.etSearch).doOnTextChanged { text, _, _, _ ->
            val query = text?.toString()?.trim()
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(400)
                searchServices(query)
            }
        }
    }

    private fun searchServices(query: String?) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.browseServices(
                    search = if (query.isNullOrEmpty()) null else query
                )
                if (response.isSuccessful) {
                    providerAdapter.updateData(response.body() ?: emptyList())
                } else {
                    Toast.makeText(this@CustomerHome, "Error ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CustomerHome, "Network: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}