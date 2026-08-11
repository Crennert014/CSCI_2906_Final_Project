package com.warburton.wfreunion.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.warburton.wfreunion.R

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_IS_DARK_MODE = "is_dark_mode"
    private const val KEY_DARK_SCHEME = "dark_scheme"

    fun applyTheme(context: Context) {
        val isDarkMode = isDarkMode(context)
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun getThemeResId(context: Context): Int {
        if (!isDarkMode(context)) return R.style.Theme_WFReunion
        
        return when (getDarkScheme(context)) {
            1 -> R.style.Theme_WFReunion_Dark_Ocean
            2 -> R.style.Theme_WFReunion_Dark_Midnight
            else -> R.style.Theme_WFReunion_Dark_Forest
        }
    }

    fun toggleTheme(context: Context) {
        val newDarkMode = !isDarkMode(context)
        setDarkMode(context, newDarkMode)
        applyTheme(context)
    }

    fun setDarkScheme(context: Context, index: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putInt(KEY_DARK_SCHEME, index) }
    }

    fun getDarkScheme(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_DARK_SCHEME, 0) // 0: Forest, 1: Ocean, 2: Midnight
    }

    fun isDarkMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_DARK_MODE, false)
    }

    private fun setDarkMode(context: Context, isDark: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_IS_DARK_MODE, isDark) }
    }
}
