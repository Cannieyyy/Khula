package com.example.khula.Data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("khula_session", Context.MODE_PRIVATE)

    // Save everything after a successful login/register
    fun saveSession(token: String, userId: Int, accountType: String) {
        prefs.edit()
            .putString("token", token)
            .putInt("userId", userId)
            .putString("accountType", accountType)
            .apply()
    }

    fun getToken(): String? = prefs.getString("token", null)
    fun getUserId(): Int = prefs.getInt("userId", 0)
    fun getAccountType(): String? = prefs.getString("accountType", null)

    fun isLoggedIn(): Boolean = getToken() != null

    // Clear everything on logout
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}