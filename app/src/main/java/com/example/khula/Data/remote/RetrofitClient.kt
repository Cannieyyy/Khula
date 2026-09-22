package com.example.khula.Data.remote

import com.example.khula.Data.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://khularestapi.onrender.com/"

    private lateinit var sessionManager: SessionManager

    // Called once at app startup
    fun init(manager: SessionManager) {
        sessionManager = manager
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)

        // Add auth interceptor only after init() has been called
        if (::sessionManager.isInitialized) {
            builder.addInterceptor(AuthInterceptor(sessionManager))
        }

        builder.build()
    }

    val api: KhulaAPIServices by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KhulaAPIServices::class.java)
    }
}