package com.example.newplaylistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val APP_CONFIG = "app_config"
const val DARK_THEME = "dark_theme"
const val SEARCH_HISTORY = "search_history"

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        val sharedPrefs = getSharedPreferences(APP_CONFIG, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME, darkTheme)
        switchTheme(darkTheme)
        super.onCreate()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        val sharedPrefs = getSharedPreferences(APP_CONFIG, MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(DARK_THEME, darkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
