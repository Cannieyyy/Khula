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
import com.example.khula.Data.remote.RetrofitClient
import kotlinx.coroutines.launch

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.example.khula.Data.Models.AuthenticateModel.GoogleLoginRequest

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


        // Setup Google Sign In
        val btnGoogleSignIn = findViewById<android.view.View>(R.id.btnGoogleSignIn)
        val credentialManager = CredentialManager.create(this)

        btnGoogleSignIn.setOnClickListener {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("168913983978-n2nlhq6iqdviq6kbk28bdih1vrc2jt32.apps.googleusercontent.com")
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            lifecycleScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@Login
                    )

                    val credential = result.credential
                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        // Send the token to our C# API
                        val response = RetrofitClient.api.googleLogin(AuthenticateModel.GoogleLoginRequest(idToken))

                        if (response.isSuccessful) {
                            val body = response.body()
                            val session = SessionManager(this@Login)
                            if (body?.token != null) {
                                session.saveSession(body.token, body.userId ?: 0, body.accountType ?: "Customer")
                            }

                            val intent = if (body?.accountType == "Provider") {
                                Intent(this@Login, ProviderDashboard::class.java)
                            } else {
                                Intent(this@Login, CustomerHome::class.java)
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@Login, "API Error: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Login, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }


        val appLinkIntent = intent
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data

        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            val uri = appLinkData

            // Log what we actually received so we can see it in Logcat
            android.util.Log.d("DEEP_LINK_DEBUG", "Received URL: $uri")

            // 1. Check for query parameter (e.g., ?token=xyz)
            val tokenHash = uri.getQueryParameter("token")

            // 2. Check for fragment (e.g., #access_token=xyz)
            val fragment = uri.fragment
            val accessToken = if (fragment != null && fragment.contains("access_token=")) {
                fragment.substringAfter("access_token=").substringBefore("&")
            } else null

            // If we have either, go to the Reset screen
            if (tokenHash != null) {
                val intent = Intent(this, ResetPasswordActivity::class.java)
                intent.putExtra("tokenHash", tokenHash)
                startActivity(intent)
            } else if (accessToken != null) {
                // Fallback for the old format
                val intent = Intent(this, ResetPasswordActivity::class.java)
                intent.putExtra("resetToken", accessToken)
                startActivity(intent)
            } else {
                // If it's an error link, show a toast
                val error = uri.getQueryParameter("error_description") ?: fragment?.substringAfter("error_description=")?.substringBefore("&")
                if (error != null) {
                    Toast.makeText(this, "Link expired or invalid. Request a new one.", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Forgot Password Logic
        forgotlink.setOnClickListener {
            val email = emailEdit.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Sending reset email...", Toast.LENGTH_SHORT).show()

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.forgotPassword(AuthenticateModel.ForgotPasswordRequest(email))
                    if (response.isSuccessful) {
                        Toast.makeText(this@Login, "Check your email for reset link!", Toast.LENGTH_LONG).show()
                    } else {
                        val err = response.errorBody()?.string() ?: "Failed to send email"
                        Toast.makeText(this@Login, err, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Login, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Login button
        btnLogin.setOnClickListener {
            val email = emailEdit.text.toString().trim()
            val password = passEdit.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            btnLogin.text = "Logging in..."

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.login(
                        AuthenticateModel.LoginRequest(email, password)
                    )

                    if (response.isSuccessful) {
                        val body = response.body()
                        val token = body?.token
                        val userId = body?.userId ?: 0
                        val accountType = body?.accountType ?: "Customer"

                        val session = SessionManager(this@Login)
                        if (token != null) {
                            session.saveSession(token, userId, accountType)
                        }

                        Toast.makeText(this@Login, "Welcome! You are a $accountType", Toast.LENGTH_LONG).show()

                        val intent = if (accountType == "Provider") {
                            Intent(this@Login, ProviderDashboard::class.java)
                        } else {
                            Intent(this@Login, CustomerHome::class.java)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Login, "Invalid email or password", Toast.LENGTH_LONG).show()
                        btnLogin.isEnabled = true
                        btnLogin.text = "Login"
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@Login, "Network error. The server might be waking up — try again in a minute.", Toast.LENGTH_LONG).show()
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