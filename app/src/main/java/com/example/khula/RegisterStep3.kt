package com.example.khula

import android.content.Intent
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
import com.example.khula.Data.Models.AuthenticateModel
import com.example.khula.Data.remote.RetrofitClient
import kotlinx.coroutines.launch

class RegisterStep3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_step3)

        //Read ALL the data collected from Steps 1 and 2
        val accountType = intent.getStringExtra("accountType") ?: "Customer"
        val firstName = intent.getStringExtra("firstName") ?: ""
        val lastName = intent.getStringExtra("lastName") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val phone = intent.getStringExtra("phoneNumber") ?: ""
        val password = intent.getStringExtra("password") ?: ""
        val confirmPassword = intent.getStringExtra("confirmPassword") ?: ""


        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnComplete = findViewById<Button>(R.id.btnCompleteRegistration)
        val etProvince = findViewById<EditText>(R.id.etProvince)
        val etCity = findViewById<EditText>(R.id.etCity)
        val etSuburb = findViewById<EditText>(R.id.etSuburb)

        //Back button
        btnBack.setOnClickListener {
            startActivity(Intent(this, RegisterStep2::class.java))
        }

        // Complete Registration — calls the API
        btnComplete.setOnClickListener {
            val province = etProvince.text.toString().trim()
            val city = etCity.text.toString().trim()
            val suburb = etSuburb.text.toString().trim()

            if (province.isEmpty() || city.isEmpty() || suburb.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Loading state
            btnComplete.isEnabled = false
            btnComplete.text = "Creating account..."

            // Call the API
            lifecycleScope.launch {
                try {
                    val request = AuthenticateModel.RegisterRequest(
                        firstName = firstName,
                        lastName = lastName,
                        EmailAddress = email,
                        phoneNumber = phone,
                        password = password,
                        ComfirmPassword = confirmPassword,
                        accountType = accountType,
                        province = province,
                        city = city,
                        suburb = suburb
                    )

                    val response = RetrofitClient.api.register(request)

                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@RegisterStep3,
                            "Registration successful! Please log in.",
                            Toast.LENGTH_LONG
                        ).show()

                        // Clear the whole back stack and go to Login
                        val intent = Intent(this@RegisterStep3, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(
                            this@RegisterStep3,
                            "Error ${response.code()}: $errorBody",
                            Toast.LENGTH_LONG
                        ).show()
                        btnComplete.isEnabled = true
                        btnComplete.text = "Complete Registration"
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        this@RegisterStep3,
                        "Network error. Try again in a minute.",
                        Toast.LENGTH_LONG
                    ).show()
                    btnComplete.isEnabled = true
                    btnComplete.text = "Complete Registration"
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