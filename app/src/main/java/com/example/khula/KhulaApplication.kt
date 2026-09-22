package com.example.khula

import android.app.Application
import com.example.khula.Data.SessionManager
import com.example.khula.Data.remote.RetrofitClient

class KhulaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)
    }
}