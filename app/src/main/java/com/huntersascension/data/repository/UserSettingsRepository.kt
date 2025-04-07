package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.UserSettingsDao
import com.huntersascension.data.model.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserSettingsRepository(private val userSettingsDao: UserSettingsDao) {
    
    fun getUserSettings(userEmail: String): LiveData<UserSettings> {
        return userSettingsDao.getUserSettings(userEmail)
    }
    
    suspend fun insertUserSettings(settings: UserSettings) {
        withContext(Dispatchers.IO) {
            userSettingsDao.insert(settings)
        }
    }
    
    suspend fun updateUserSettings(settings: UserSettings) {
        withContext(Dispatchers.IO) {
            userSettingsDao.update(settings)
        }
    }
    
    suspend fun updateNotifications(userEmail: String, enabled: Boolean) {
        withContext(Dispatchers.IO) {
            userSettingsDao.updateNotificationSettings(userEmail, enabled)
        }
    }
    
    suspend fun updateDailyReminder(userEmail: String, enabled: Boolean) {
        withContext(Dispatchers.IO) {
            userSettingsDao.updateDailyReminder(userEmail, enabled)
        }
    }
    
    suspend fun updateReminderTime(userEmail: String, time: String) {
        withContext(Dispatchers.IO) {
            userSettingsDao.updateReminderTime(userEmail, time)
        }
    }
    
    suspend fun updateDarkMode(userEmail: String, enabled: Boolean) {
        withContext(Dispatchers.IO) {
            userSettingsDao.updateDarkMode(userEmail, enabled)
        }
    }
    
    suspend fun updateSoundEffects(userEmail: String, enabled: Boolean) {
        withContext(Dispatchers.IO) {
            userSettingsDao.updateSoundEffects(userEmail, enabled)
        }
    }
    
    suspend fun updateLeaderboardVisibility(userEmail: String, visible: Boolean) {
        withContext(Dispatchers.IO) {
            userSettingsDao.updateLeaderboardVisibility(userEmail, visible)
        }
    }
    
    suspend fun updateMeasurementUnit(userEmail: String, unit: String) {
        withContext(Dispatchers.IO) {
            userSettingsDao.updateMeasurementUnit(userEmail, unit)
        }
    }
}
