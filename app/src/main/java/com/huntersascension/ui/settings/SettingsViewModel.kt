package com.huntersascension.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huntersascension.data.model.UserSettings
import com.huntersascension.data.repository.UserSettingsRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: UserSettingsRepository) : ViewModel() {
    
    private val _settingsUpdated = MutableLiveData<Boolean>()
    val settingsUpdated: LiveData<Boolean> = _settingsUpdated
    
    // Get user settings
    fun getUserSettings(userEmail: String): LiveData<UserSettings> {
        return settingsRepository.getUserSettings(userEmail)
    }
    
    // Ensure default settings exist
    fun ensureSettingsExist(userEmail: String) {
        viewModelScope.launch {
            val settings = settingsRepository.getUserSettings(userEmail).value
            if (settings == null) {
                settingsRepository.createDefaultSettings(userEmail)
            }
        }
    }
    
    // Update all settings at once
    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch {
            settingsRepository.updateSettings(settings)
            _settingsUpdated.value = true
        }
    }
    
    // Update notifications
    fun updateNotifications(userEmail: String, enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateNotificationSettings(userEmail, enabled)
            _settingsUpdated.value = true
        }
    }
    
    // Update daily reminder
    fun updateDailyReminder(userEmail: String, enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateDailyReminder(userEmail, enabled)
            _settingsUpdated.value = true
        }
    }
    
    // Update reminder time
    fun updateReminderTime(userEmail: String, time: String) {
        viewModelScope.launch {
            settingsRepository.updateReminderTime(userEmail, time)
            _settingsUpdated.value = true
        }
    }
    
    // Update dark mode
    fun updateDarkMode(userEmail: String, enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateDarkMode(userEmail, enabled)
            _settingsUpdated.value = true
        }
    }
    
    // Update sound effects
    fun updateSoundEffects(userEmail: String, enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSoundEffects(userEmail, enabled)
            _settingsUpdated.value = true
        }
    }
    
    // Update leaderboard visibility
    fun updateLeaderboardVisibility(userEmail: String, visible: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateLeaderboardVisibility(userEmail, visible)
            _settingsUpdated.value = true
        }
    }
    
    // Update measurement unit
    fun updateMeasurementUnit(userEmail: String, unit: String) {
        viewModelScope.launch {
            settingsRepository.updateMeasurementUnit(userEmail, unit)
            _settingsUpdated.value = true
        }
    }
    
    // Reset the updated flag
    fun resetUpdatedFlag() {
        _settingsUpdated.value = false
    }
}
