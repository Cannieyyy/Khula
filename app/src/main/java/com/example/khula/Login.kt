package com.example.khula

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.khula.Data.Models.AuthenticateModel
import com.example.khula.Data.SessionManager
import com.example.khula.Data.Models.AuthenticateModel.LoginRequest
import com.example.khula.Data.remote.RetrofitClient
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {

    // UI elements
    lateinit var emailEdit: EditText
    lateinit var passEdit: EditText
    lateinit var btnLogin: Button
    lateinit var forgotlink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)


        forgotlink = findViewById(R.id.tvForgotPassword)
        emailEdit = findViewById(R.id.etEmail)
        passEdit = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        //Login button - now calls the API
        btnLogin.setOnClickListener {
            val email = emailEdit.text.toString().trim()
            val password = passEdit.text.toString()

            // Quick local validation before hitting the API
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading state
            btnLogin.isEnabled = false
            btnLogin.text = "Logging in..."

            //Calling the API
            lifecycleScope.launch {
                try {
                    // 4. Call the live API
                    val response = RetrofitClient.api.login(
                        AuthenticateModel.LoginRequest(
                            email,
                            password
                        )
                    )

                    if (response.isSuccessful) {
                        val body = response.body()
                        val token = body?.token
                        val userId = body?.userId ?: 0
                        val accountType = body?.accountType ?: "Customer"

                        // Save the token + user info to SharedPreferences
                        val session = SessionManager(this@Login)
                        if (token != null) {
                            session.saveSession(token, userId, accountType)
                        }

                        Toast.makeText(this@Login, "Welcome! You are a $accountType", Toast.LENGTH_LONG).show()

                        val intent = if (accountType == "Provider") {
                            Intent(this@Login, ProviderDashboard::class.java)   // Replace with ProviderHome later
                        } else {
                            Intent(this@Login, CustomerHome::class.java)
                        }
                        startActivity(intent)
                        finish()
                    }else {
                        // displaying this is the user entered the wrong credentials
                        Toast.makeText(
                            this@Login,
                            "Invalid email or password",
                            Toast.LENGTH_LONG
                        ).show()
                        btnLogin.isEnabled = true
                        btnLogin.text = "Login"
                    }

                } catch (e: Exception) {
                    // Catching the exception if there is any NETWORK FAILURE, if the server is not reacged
                    Toast.makeText(
                        this@Login,
                        "Network error. The server might be waking up — try again in a minute.",
                        Toast.LENGTH_LONG
                    ).show()
                    btnLogin.isEnabled = true
                    btnLogin.text = "Login"
                }
            }
        }

        // Sign up link
        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterStep1::class.java))
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}