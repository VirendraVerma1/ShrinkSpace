package com.kreasaar.shrinkspace.ui.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _settings = MutableLiveData<Settings>()
    val settings: LiveData<Settings> = _settings

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadSettings()
    }

    private fun loadSettings() {
        // Load settings from SharedPreferences
        val autoCompression = prefs.getBoolean("auto_compression", false)
        val compressionQuality = prefs.getInt("compression_quality", 80)
        val deleteAfterCompression = prefs.getBoolean("delete_after_compression", false)
        val scanOnStartup = prefs.getBoolean("scan_on_startup", true)
        val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
        val darkMode = prefs.getBoolean("dark_mode", false)
        
        _settings.value = Settings(
            autoCompression = autoCompression,
            compressionQuality = compressionQuality,
            deleteAfterCompression = deleteAfterCompression,
            scanOnStartup = scanOnStartup,
            notificationsEnabled = notificationsEnabled,
            darkMode = darkMode
        )
    }

    fun updateAutoCompression(enabled: Boolean) {
        prefs.edit().putBoolean("auto_compression", enabled).apply()
        _settings.value = _settings.value?.copy(autoCompression = enabled)
    }

    fun updateCompressionQuality(quality: Int) {
        prefs.edit().putInt("compression_quality", quality).apply()
        _settings.value = _settings.value?.copy(compressionQuality = quality)
    }

    fun updateDeleteAfterCompression(enabled: Boolean) {
        prefs.edit().putBoolean("delete_after_compression", enabled).apply()
        _settings.value = _settings.value?.copy(deleteAfterCompression = enabled)
    }

    fun updateScanOnStartup(enabled: Boolean) {
        prefs.edit().putBoolean("scan_on_startup", enabled).apply()
        _settings.value = _settings.value?.copy(scanOnStartup = enabled)
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
        _settings.value = _settings.value?.copy(notificationsEnabled = enabled)
    }

    fun updateDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean("dark_mode", enabled).apply()
        _settings.value = _settings.value?.copy(darkMode = enabled)
    }

    fun saveSettings(settings: Settings) {
        // Save settings to SharedPreferences
        prefs.edit()
            .putBoolean("auto_compression", settings.autoCompression)
            .putInt("compression_quality", settings.compressionQuality)
            .putBoolean("delete_after_compression", settings.deleteAfterCompression)
            .putBoolean("scan_on_startup", settings.scanOnStartup)
            .putBoolean("notifications_enabled", settings.notificationsEnabled)
            .putBoolean("dark_mode", settings.darkMode)
            .apply()
        
        _settings.value = settings
    }

    fun resetToDefaults() {
        val defaultSettings = Settings()
        saveSettings(defaultSettings)
    }

    data class Settings(
        val autoCompression: Boolean = false,
        val compressionQuality: Int = 80,
        val deleteAfterCompression: Boolean = false,
        val scanOnStartup: Boolean = true,
        val notificationsEnabled: Boolean = true,
        val darkMode: Boolean = false
    )
} 