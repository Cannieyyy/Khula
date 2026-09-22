package com.example.khula

import CategoryAdapter
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khula.Data.Models.ServiceModels.ServiceBrowseResponse
import com.example.khula.Data.remote.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class CustomerHome : AppCompatActivity() {

    // =========================================================
    // RECYCLER VIEWS
    // =========================================================

    private lateinit var rvCategories: RecyclerView

    private lateinit var rvRecommended: RecyclerView


    // =========================================================
    // ADAPTERS
    // =========================================================

    private lateinit var providerAdapter: ProviderServiceAdapter


    // =========================================================
    // SERVICE DATA
    // =========================================================

    // Stores all services received from the backend.
    private var allServices: List<ServiceBrowseResponse> =
        emptyList()


    // =========================================================
    // FILTERING
    // =========================================================

    // Stores the category currently selected.
    private var selectedCategory: String = "All"

    // Stores the current search text.
    private var currentSearchQuery: String = ""

    // Used to delay search while the customer is typing.
    private var searchJob: Job? = null


    // =========================================================
    // LOCATION
    // =========================================================

    // Google location client.
    private lateinit var fusedLocationClient:
            FusedLocationProviderClient

    // Registered location from the user's profile.
    //
    // This is used when GPS location cannot be retrieved.
    private var profileLocation: String =
        "Location not set"


    // =========================================================
    // CONSTANTS
    // =========================================================

    companion object {

        // Unique request code for location permission.
        private const val LOCATION_PERMISSION_REQUEST_CODE =
            1001
    }


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display.
        enableEdgeToEdge()

        // Connect Activity to XML.
        setContentView(R.layout.activity_customer_home)


        // =====================================================
        // INITIALISE LOCATION
        // =====================================================

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(
                this
            )


        // =====================================================
        // FIND RECYCLER VIEWS
        // =====================================================

        rvCategories =
            findViewById(
                R.id.rvCategories
            )

        rvRecommended =
            findViewById(
                R.id.rvRecommendedProviders
            )


        // =====================================================
        // RECYCLER VIEW LAYOUTS
        // =====================================================

        // Categories scroll horizontally.
        rvCategories.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        // Services scroll vertically.
        rvRecommended.layoutManager =
            LinearLayoutManager(
                this
            )


        // =====================================================
        // PROVIDER / SERVICE ADAPTER
        // =====================================================

        providerAdapter =
            ProviderServiceAdapter(
                emptyList()
            ) { service ->

                // =================================================
                // KEEP GROUP MEMBERS' REQUEST SERVICE FEATURE
                // =================================================

                // Open RequestServiceActivity.
                val intent =
                    Intent(
                        this,
                        RequestServiceActivity::class.java
                    )


                // Send real service ID.
                intent.putExtra(
                    "serviceId",
                    service.serviceId ?: -1
                )


                // Send provider name.
                intent.putExtra(
                    "providerName",
                    service.providerName
                        ?: "Provider"
                )


                // Send service title.
                intent.putExtra(
                    "serviceTitle",
                    service.title
                        ?: "Service"
                )


                // Send provider region.
                intent.putExtra(
                    "providerRegion",
                    service.providerSuburb
                        ?: service.providerCity
                        ?: ""
                )


                // Open RequestServiceActivity.
                startActivity(intent)
            }


        // Connect adapter to RecyclerView.
        rvRecommended.adapter =
            providerAdapter


        // =====================================================
        // LOAD PROFILE
        // =====================================================

        loadUserProfile()


        // =====================================================
        // DEVICE LOCATION
        // =====================================================

        checkLocationPermission()


        // =====================================================
        // LOAD SERVICES
        // =====================================================

        loadServices()


        // =====================================================
        // SEARCH
        // =====================================================

        setupSearch()


        // =====================================================
        // BOTTOM NAVIGATION
        // =====================================================

        setupBottomNavigation()


        // =====================================================
        // SEE ALL RECOMMENDED
        // =====================================================

        findViewById<TextView>(
            R.id.btnSeeAllRecommended
        ).setOnClickListener {

            // Reset category.
            selectedCategory =
                "All"

            // Reset search.
            currentSearchQuery =
                ""

            // Clear search field.
            findViewById<EditText>(
                R.id.etSearch
            ).setText("")

            // Show all services.
            applyFilters()
        }


        // =====================================================
        // SEE ALL CATEGORIES
        // =====================================================

        findViewById<TextView>(
            R.id.btnSeeAllCategories
        ).setOnClickListener {

            // Remove category filtering.
            selectedCategory =
                "All"

            // Apply remaining search filter.
            applyFilters()
        }


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
    // LOAD USER PROFILE
    // =========================================================

    private fun loadUserProfile() {

        lifecycleScope.launch {

            try {

                // Request logged-in user's profile.
                val response =
                    RetrofitClient.api
                        .getMyProfile()


                if (response.isSuccessful) {

                    // Get profile from response.
                    val body =
                        response.body()


                    // =================================================
                    // NAME
                    // =================================================

                    // Build full name safely.
                    val fullName =
                        "${body?.firstName ?: ""} ${body?.lastName ?: ""}"
                            .trim()


                    // Display customer name.
                    findViewById<TextView>(
                        R.id.tvCustomerName
                    ).text =
                        if (fullName.isNotEmpty()) {

                            fullName

                        } else {

                            "Customer"
                        }


                    // =================================================
                    // PROFILE LOCATION
                    // =================================================

                    // Build a registered location from
                    // suburb, city and province.
                    profileLocation =
                        listOfNotNull(
                            body?.suburb,
                            body?.city,
                            body?.province
                        )
                            .filter {
                                it.isNotBlank()
                            }
                            .distinct()
                            .joinToString(", ")


                    // If the profile contains no location.
                    if (profileLocation.isBlank()) {

                        profileLocation =
                            "Location not set"
                    }


                    // Temporarily show profile location.
                    //
                    // GPS will replace this if available.
                    findViewById<TextView>(
                        R.id.tvUserLocation
                    ).text =
                        profileLocation


                } else {

                    // Read backend error.
                    val error =
                        response.errorBody()
                            ?.string()
                            ?: "Error ${response.code()}"


                    // Display error.
                    Toast.makeText(
                        this@CustomerHome,
                        error,
                        Toast.LENGTH_LONG
                    ).show()
                }


            } catch (e: Exception) {

                // Display network error.
                Toast.makeText(
                    this@CustomerHome,
                    "Network: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // =========================================================
    // LOCATION PERMISSION
    // =========================================================

    private fun checkLocationPermission() {

        // Check precise location permission.
        val fineLocationGranted =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED


        // Check approximate location permission.
        val coarseLocationGranted =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED


        // Check whether we already have permission.
        if (
            fineLocationGranted ||
            coarseLocationGranted
        ) {

            // Permission already exists.
            getCurrentLocation()

        } else {

            // Request location permission.
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


    // =========================================================
    // LOCATION PERMISSION RESULT
    // =========================================================

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )


        // Check whether this is our location request.
        if (
            requestCode ==
            LOCATION_PERMISSION_REQUEST_CODE
        ) {

            // Check if either location permission
            // was granted.
            val permissionGranted =
                grantResults.any {
                    it == PackageManager.PERMISSION_GRANTED
                }


            if (permissionGranted) {

                // Get phone location.
                getCurrentLocation()

            } else {

                // Use profile location.
                showProfileLocation()
            }
        }
    }


    // =========================================================
    // GET CURRENT DEVICE LOCATION
    // =========================================================

    private fun getCurrentLocation() {

        // Check precise permission again.
        val fineLocationGranted =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED


        // Check approximate permission again.
        val coarseLocationGranted =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED


        // Stop if no permission exists.
        if (
            !fineLocationGranted &&
            !coarseLocationGranted
        ) {

            showProfileLocation()

            return
        }


        // Create cancellation token.
        val cancellationTokenSource =
            CancellationTokenSource()


        // Ask Google Play Services for
        // the current device location.
        fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            )
            .addOnSuccessListener { location ->

                // Check whether location exists.
                if (location != null) {

                    // Convert coordinates into
                    // readable location.
                    getAddressFromLocation(
                        location.latitude,
                        location.longitude
                    )

                } else {

                    // GPS returned nothing.
                    showProfileLocation()
                }
            }
            .addOnFailureListener {

                // GPS failed.
                showProfileLocation()
            }
    }


    // =========================================================
    // CONVERT GPS LOCATION TO ADDRESS
    // =========================================================

    private fun getAddressFromLocation(
        latitude: Double,
        longitude: Double
    ) {

        try {

            // Create Android Geocoder.
            val geocoder =
                Geocoder(
                    this,
                    Locale.getDefault()
                )


            // Convert coordinates to an address.
            @Suppress("DEPRECATION")
            val addresses =
                geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )


            // Get first address.
            val address =
                addresses?.firstOrNull()


            // Check whether address exists.
            if (address != null) {

                // Get suburb/local area.
                val area =
                    address.subLocality
                        ?: address.locality
                        ?: address.subAdminArea


                // Get city.
                val city =
                    address.locality
                        ?: address.subAdminArea
                        ?: address.adminArea


                // Build readable location.
                val readableLocation =
                    listOfNotNull(
                        area,
                        city
                    )
                        .filter {
                            it.isNotBlank()
                        }
                        .distinct()
                        .joinToString(", ")


                // Display GPS location when available.
                if (readableLocation.isNotBlank()) {

                    findViewById<TextView>(
                        R.id.tvUserLocation
                    ).text =
                        readableLocation

                } else {

                    // Otherwise use profile location.
                    showProfileLocation()
                }


            } else {

                // Geocoder returned no address.
                showProfileLocation()
            }


        } catch (_: Exception) {

            // Geocoder failed.
            showProfileLocation()
        }
    }


    // =========================================================
    // SHOW PROFILE LOCATION
    // =========================================================

    private fun showProfileLocation() {

        findViewById<TextView>(
            R.id.tvUserLocation
        ).text =
            profileLocation
    }


    // =========================================================
    // LOAD SERVICES
    // =========================================================

    private fun loadServices() {

        lifecycleScope.launch {

            try {

                // Request real services from API.
                val response =
                    RetrofitClient.api
                        .browseServices()


                if (response.isSuccessful) {

                    // Save complete original list.
                    allServices =
                        response.body()
                            ?: emptyList()


                    // Initially display all services.
                    providerAdapter.updateData(
                        allServices
                    )


                    // =================================================
                    // REAL CATEGORIES
                    // =================================================

                    // Extract real categories from services.
                    val realCategories =
                        allServices
                            .mapNotNull {
                                it.category
                            }
                            .filter {
                                it.isNotBlank()
                            }
                            .distinct()
                            .sorted()


                    // Add "All" as an Android filtering option.
                    val categories =
                        listOf("All") +
                                realCategories


                    // Create category adapter.
                    rvCategories.adapter =
                        CategoryAdapter(
                            categories
                        ) { selected ->

                            // Remember selected category.
                            selectedCategory =
                                selected


                            // Apply category + search.
                            applyFilters()
                        }


                } else {

                    // API error.
                    Toast.makeText(
                        this@CustomerHome,
                        "Error ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }


            } catch (e: Exception) {

                // Network error.
                Toast.makeText(
                    this@CustomerHome,
                    "Network: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // =========================================================
    // SET UP SEARCH
    // =========================================================

    private fun setupSearch() {

        // Find search field.
        val searchField =
            findViewById<EditText>(
                R.id.etSearch
            )


        // Listen while customer types.
        searchField.doOnTextChanged { text, _, _, _ ->

            // Save search query.
            currentSearchQuery =
                text
                    ?.toString()
                    ?.trim()
                    .orEmpty()


            // Cancel previous unfinished search.
            searchJob?.cancel()


            // Start a new search delay.
            searchJob =
                lifecycleScope.launch {

                    // Wait briefly before filtering.
                    delay(400L)

                    // Apply category + search together.
                    applyFilters()
                }
        }
    }


    // =========================================================
    // APPLY SEARCH + CATEGORY FILTERS
    // =========================================================

    private fun applyFilters() {

        // Always begin with the complete list.
        var filteredServices =
            allServices


        // =====================================================
        // CATEGORY
        // =====================================================

        // Only filter category if the customer
        // has not selected "All".
        if (
            !selectedCategory.equals(
                "All",
                ignoreCase = true
            )
        ) {

            filteredServices =
                filteredServices.filter { service ->

                    // Safe because category may be null.
                    service.category?.equals(
                        selectedCategory,
                        ignoreCase = true
                    ) == true
                }
        }


        // =====================================================
        // SEARCH
        // =====================================================

        // Only search when text exists.
        if (currentSearchQuery.isNotBlank()) {

            val query =
                currentSearchQuery


            filteredServices =
                filteredServices.filter { service ->


                    // Search service title.
                    val titleMatches =
                        service.title?.contains(
                            query,
                            ignoreCase = true
                        ) == true


                    // Search description.
                    val descriptionMatches =
                        service.description?.contains(
                            query,
                            ignoreCase = true
                        ) == true


                    // Search category.
                    val categoryMatches =
                        service.category?.contains(
                            query,
                            ignoreCase = true
                        ) == true


                    // Search provider name.
                    val providerMatches =
                        service.providerName?.contains(
                            query,
                            ignoreCase = true
                        ) == true


                    // Search provider suburb.
                    val suburbMatches =
                        service.providerSuburb?.contains(
                            query,
                            ignoreCase = true
                        ) == true


                    // Search provider city.
                    val cityMatches =
                        service.providerCity?.contains(
                            query,
                            ignoreCase = true
                        ) == true


                    // Search provider province.
                    val provinceMatches =
                        service.providerProvince?.contains(
                            query,
                            ignoreCase = true
                        ) == true


                    // Keep the service if any
                    // real field matches.
                    titleMatches ||
                            descriptionMatches ||
                            categoryMatches ||
                            providerMatches ||
                            suburbMatches ||
                            cityMatches ||
                            provinceMatches
                }
        }


        // Update RecyclerView.
        providerAdapter.updateData(
            filteredServices
        )
    }


    // =========================================================
    // BOTTOM NAVIGATION
    // =========================================================

    private fun setupBottomNavigation() {

        // Find bottom navigation.
        val bottomNavigation =
            findViewById<BottomNavigationView>(
                R.id.bottomNavigation
            )


        // Home is the current screen.
        bottomNavigation.selectedItemId =
            R.id.nav_home


        // Listen for navigation clicks.
        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                // HOME
                R.id.nav_home -> {

                    true
                }


                // BOOKINGS
                R.id.nav_bookings -> {

                    startActivity(
                        Intent(
                            this,
                            BookingHistory::class.java
                        )
                    )

                    true
                }


                // MESSAGES
                R.id.nav_messages -> {

                    Toast.makeText(
                        this,
                        "Messages — coming soon",
                        Toast.LENGTH_SHORT
                    ).show()

                    true
                }


                // FAVOURITES
                R.id.nav_favorites -> {

                    Toast.makeText(
                        this,
                        "Favourites — coming soon",
                        Toast.LENGTH_SHORT
                    ).show()

                    true
                }


                // PROFILE
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