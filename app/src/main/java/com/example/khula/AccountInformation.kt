package com.example.khula

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.khula.Data.remote.RetrofitClient
import kotlinx.coroutines.launch

class AccountInformation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_information)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val tvFullName = findViewById<TextView>(R.id.tvFullName)
        val tvAccountType = findViewById<TextView>(R.id.tvAccountType)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvPhone = findViewById<TextView>(R.id.tvPhone)
        val tvProvince = findViewById<TextView>(R.id.tvProvince)
        val tvCity = findViewById<TextView>(R.id.tvCity)
        val tvSuburb = findViewById<TextView>(R.id.tvSuburb)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getMyProfile()

                if (response.isSuccessful) {
                    val body = response.body()

                    val fullName = "${body?.firstName ?: ""} ${body?.lastName ?: ""}".trim()
                    tvFullName.text = if (fullName.isNotEmpty()) fullName else "—"
                    tvAccountType.text = body?.accountType ?: "—"
                    tvEmail.text = body?.email ?: "—"
                    tvPhone.text = body?.phoneNumber ?: "—"
                    tvProvince.text = body?.province ?: "—"
                    tvCity.text = body?.city ?: "—"
                    tvSuburb.text = body?.suburb ?: "—"
                } else {
                    val err = response.errorBody()?.string() ?: "Error ${response.code()}"
                    Toast.makeText(this@AccountInformation, err, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AccountInformation, "Network: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}