package com.example.khula

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.khula.Data.Models.ServiceModels
import com.example.khula.Data.remote.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

class EditProfileActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private lateinit var ivEditAvatar: ImageView

    // This handles picking an image from the phone's gallery
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            Glide.with(this).load(uri).into(ivEditAvatar)
            uploadImageToServer(uri) // Automatically upload when selected
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        ivEditAvatar = findViewById(R.id.ivEditAvatar)

        // Open Gallery on image tap
        ivEditAvatar.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Load current data first
        loadCurrentData()

        findViewById<Button>(R.id.btnSaveProfile).setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun loadCurrentData() {
        lifecycleScope.launch {
            val response = RetrofitClient.api.getMyProfile()
            if (response.isSuccessful) {
                val body = response.body()
                findViewById<EditText>(R.id.etFirstName).setText(body?.firstName)
                findViewById<EditText>(R.id.etLastName).setText(body?.lastName)
                findViewById<EditText>(R.id.etPhone).setText(body?.phoneNumber)
                findViewById<EditText>(R.id.etProvince).setText(body?.province)
                findViewById<EditText>(R.id.etCity).setText(body?.city)
                findViewById<EditText>(R.id.etSuburb).setText(body?.suburb)

                if (!body?.avatarUrl.isNullOrEmpty()) {
                    Glide.with(this@EditProfileActivity).load(body.avatarUrl).into(ivEditAvatar)
                }
            }
        }
    }

    private fun saveProfileChanges() {
        val firstName = findViewById<EditText>(R.id.etFirstName).text.toString()
        val lastName = findViewById<EditText>(R.id.etLastName).text.toString()
        val phone = findViewById<EditText>(R.id.etPhone).text.toString()
        val province = findViewById<EditText>(R.id.etProvince).text.toString()
        val city = findViewById<EditText>(R.id.etCity).text.toString()
        val suburb = findViewById<EditText>(R.id.etSuburb).text.toString()

        lifecycleScope.launch {
            try {
                val request = ServiceModels.UpdateProfileRequest(firstName, lastName, phone, province, city, suburb)
                val response = RetrofitClient.api.updateProfile(request)

                if (response.isSuccessful) {
                    Toast.makeText(this@EditProfileActivity, "Profile updated!", Toast.LENGTH_SHORT).show()
                    finish() // Go back to Account Information
                } else {
                    Toast.makeText(this@EditProfileActivity, "Failed to update", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToServer(uri: Uri) {
        lifecycleScope.launch {
            try {
                // Convert Uri to Multipart file for Retrofit
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("file", "avatar.jpg", requestBody)

                    val response = RetrofitClient.api.uploadAvatar(part)
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditProfileActivity, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@EditProfileActivity, "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "Upload Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}