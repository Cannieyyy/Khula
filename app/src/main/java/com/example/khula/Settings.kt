package com.example.khula

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.khula.Data.SessionManager
import com.example.khula.Data.SettingsPreferences

class Settings : AppCompatActivity() {

    // Handles locally saved application settings.
    private lateinit var settingsPreferences: SettingsPreferences

    // Dark mode switch.
    private lateinit var switchDarkMode: SwitchCompat


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(
            R.layout.activity_settings
        )


        // =====================================================
        // SETTINGS PREFERENCES
        // =====================================================

        settingsPreferences =
            SettingsPreferences(this)


        // =====================================================
        // BACK BUTTON
        // =====================================================

        findViewById<ImageButton>(
            R.id.btnBack
        ).setOnClickListener {

            finish()
        }


        // =====================================================
        // ACCOUNT INFORMATION
        // =====================================================

        findViewById<LinearLayout>(
            R.id.rowAccountInfo
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AccountInformation::class.java
                )
            )
        }


        // =====================================================
        // CHANGE PASSWORD
        // =====================================================

        findViewById<LinearLayout>(
            R.id.rowChangePassword
        ).setOnClickListener {

            showChangePasswordInformation()
        }


        // =====================================================
        // LANGUAGE
        // =====================================================

        findViewById<LinearLayout>(
            R.id.rowLanguage
        ).setOnClickListener {

            showLanguageDialog()
        }


        // =====================================================
        // NOTIFICATIONS
        // =====================================================

        findViewById<LinearLayout>(
            R.id.rowNotifications
        ).setOnClickListener {

            showNotificationDialog()
        }


        // =====================================================
        // HELP & SUPPORT
        // =====================================================

        findViewById<LinearLayout>(
            R.id.rowHelp
        ).setOnClickListener {

            showHelpDialog()
        }


        // =====================================================
        // PRIVACY POLICY
        // =====================================================

        findViewById<LinearLayout>(
            R.id.rowPrivacy
        ).setOnClickListener {

            showPrivacyPolicy()
        }


        // =====================================================
        // DARK MODE
        // =====================================================

        setupDarkMode()


        // =====================================================
        // LOGOUT
        // =====================================================

        findViewById<LinearLayout>(
            R.id.rowLogout
        ).setOnClickListener {

            showLogoutDialog()
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
    // DARK MODE
    // =========================================================

    private fun setupDarkMode() {

        switchDarkMode =
            findViewById(
                R.id.switchDarkMode
            )


        // Get saved preference.
        val darkModeEnabled =
            settingsPreferences
                .isDarkModeEnabled()


        // Set switch without triggering the listener first.
        switchDarkMode.isChecked =
            darkModeEnabled


        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->

            // Save setting.
            settingsPreferences
                .setDarkMode(
                    isChecked
                )


            // Actually change Android theme mode.
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) {

                    AppCompatDelegate.MODE_NIGHT_YES

                } else {

                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }


    // =========================================================
    // LANGUAGE
    // =========================================================

    private fun showLanguageDialog() {

        val languages =
            arrayOf(
                "English",
                "isiZulu"
            )


        val currentLanguage =
            settingsPreferences
                .getLanguage()


        val checkedItem =
            if (currentLanguage == "isiZulu") {
                1
            } else {
                0
            }


        AlertDialog.Builder(this)

            .setTitle(
                "Choose Language"
            )

            .setSingleChoiceItems(
                languages,
                checkedItem
            ) { dialog, which ->

                when (which) {

                    // English
                    0 -> {

                        settingsPreferences
                            .setLanguage(
                                "English"
                            )

                        changeApplicationLanguage(
                            "en"
                        )
                    }


                    // isiZulu
                    1 -> {

                        settingsPreferences
                            .setLanguage(
                                "isiZulu"
                            )

                        changeApplicationLanguage(
                            "zu"
                        )
                    }
                }


                dialog.dismiss()
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }


    // =========================================================
    // CHANGE APPLICATION LANGUAGE
    // =========================================================

    private fun changeApplicationLanguage(
        languageCode: String
    ) {

        // Create the locale list.
        val appLocale =
            LocaleListCompat.forLanguageTags(
                languageCode
            )


        // Apply language to the application.
        AppCompatDelegate.setApplicationLocales(
            appLocale
        )


        Toast.makeText(
            this,
            "Language updated",
            Toast.LENGTH_SHORT
        ).show()
    }


    // =========================================================
    // NOTIFICATIONS
    // =========================================================

    private fun showNotificationDialog() {

        val currentlyEnabled =
            settingsPreferences
                .areNotificationsEnabled()


        val options =
            arrayOf(
                "Enable notifications"
            )


        val checkedItems =
            booleanArrayOf(
                currentlyEnabled
            )


        AlertDialog.Builder(this)

            .setTitle(
                "Notifications"
            )

            .setMultiChoiceItems(
                options,
                checkedItems
            ) { _, which, isChecked ->

                if (which == 0) {

                    checkedItems[0] =
                        isChecked
                }
            }

            .setPositiveButton(
                "Save"
            ) { _, _ ->

                val enabled =
                    checkedItems[0]


                settingsPreferences
                    .setNotificationsEnabled(
                        enabled
                    )


                Toast.makeText(
                    this,
                    if (enabled) {
                        "Notifications enabled"
                    } else {
                        "Notifications disabled"
                    },
                    Toast.LENGTH_SHORT
                ).show()
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }


    // =========================================================
    // HELP & SUPPORT
    // =========================================================

    private fun showHelpDialog() {

        AlertDialog.Builder(this)

            .setTitle(
                "Help & Support"
            )

            .setMessage(
                """
                Need help using Khula?

                Account:
                View your registered information from Account Information.

                Services:
                Customers can browse available services and send service requests.

                Bookings:
                Customers can view their service requests under My Bookings.

                Providers:
                Providers can create services and view customer requests.

                If you experience a technical problem, close and reopen the application and check your internet connection.
                """.trimIndent()
            )

            .setPositiveButton(
                "OK",
                null
            )

            .show()
    }


    // =========================================================
    // PRIVACY POLICY
    // =========================================================

    private fun showPrivacyPolicy() {

        AlertDialog.Builder(this)

            .setTitle(
                "Privacy Policy"
            )

            .setMessage(
                """
                Khula Privacy Notice

                Khula uses account information you provide when registering, such as your name, email address, phone number and location information, to provide application functionality.

                Customers can use Khula to browse services and submit service requests.

                Service providers can use Khula to publish services and receive customer requests.

                Your login session is stored on your device so that you can remain signed in.

                You can end your current session at any time by selecting Log Out.

                Location permission is only requested when location functionality is required by the application.

                Khula should only request permissions necessary for its application features.
                """.trimIndent()
            )

            .setPositiveButton(
                "Close",
                null
            )

            .show()
    }


    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    private fun showChangePasswordInformation() {

        AlertDialog.Builder(this)

            .setTitle(
                "Change Password"
            )

            .setMessage(
                "Password changes are not currently supported by the Khula server."
            )

            .setPositiveButton(
                "OK",
                null
            )

            .show()
    }


    // =========================================================
    // LOGOUT CONFIRMATION
    // =========================================================

    private fun showLogoutDialog() {

        AlertDialog.Builder(this)

            .setTitle(
                "Log Out"
            )

            .setMessage(
                "Are you sure you want to log out?"
            )

            .setPositiveButton(
                "Log Out"
            ) { _, _ ->

                logoutUser()
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }


    // =========================================================
    // LOGOUT USER
    // =========================================================

    private fun logoutUser() {

        // Clear authentication session.
        SessionManager(
            this
        ).clearSession()


        // Open Login screen.
        val intent =
            Intent(
                this,
                Login::class.java
            )


        // Remove authenticated screens from back stack.
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK


        startActivity(
            intent
        )


        finish()
    }
}