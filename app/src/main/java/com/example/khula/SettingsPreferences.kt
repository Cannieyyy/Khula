package com.example.khula.Data

import android.content.Context

class SettingsPreferences(context: Context) {

    // SharedPreferences file used only for app settings.
    private val preferences =
        context.getSharedPreferences(
            "khula_settings",
            Context.MODE_PRIVATE
        )


    companion object {

        // Keys used to save settings.
        private const val KEY_DARK_MODE = "dark_mode"

        private const val KEY_NOTIFICATIONS = "notifications"

        private const val KEY_LANGUAGE = "language"
    }


    // =========================================================
    // DARK MODE
    // =========================================================

    fun setDarkMode(enabled: Boolean) {

        preferences
            .edit()
            .putBoolean(
                KEY_DARK_MODE,
                enabled
            )
            .apply()
    }


    fun isDarkModeEnabled(): Boolean {

        return preferences.getBoolean(
            KEY_DARK_MODE,
            false
        )
    }


    // =========================================================
    // NOTIFICATIONS
    // =========================================================

    fun setNotificationsEnabled(
        enabled: Boolean
    ) {

        preferences
            .edit()
            .putBoolean(
                KEY_NOTIFICATIONS,
                enabled
            )
            .apply()
    }


    fun areNotificationsEnabled(): Boolean {

        return preferences.getBoolean(
            KEY_NOTIFICATIONS,
            true
        )
    }


    // =========================================================
    // LANGUAGE
    // =========================================================

    fun setLanguage(
        language: String
    ) {

        preferences
            .edit()
            .putString(
                KEY_LANGUAGE,
                language
            )
            .apply()
    }


    fun getLanguage(): String {

        return preferences.getString(
            KEY_LANGUAGE,
            "English"
        ) ?: "English"
    }
}