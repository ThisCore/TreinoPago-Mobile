package com.example.treinopago.ViewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(
        sharedPreferences.getBoolean("is_dark_theme", false)
    )
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        val newTheme = !_isDarkTheme.value
        _isDarkTheme.value = newTheme
        sharedPreferences.edit()
            .putBoolean("is_dark_theme", newTheme)
            .apply()
    }

    fun setTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        sharedPreferences.edit()
            .putBoolean("is_dark_theme", isDark)
            .apply()
    }
}
