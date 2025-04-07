package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.UserSettings

@Dao
interface UserSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: UserSettings)
    
    @Update
    suspend fun update(settings: UserSettings)
    
    @Query("SELECT * FROM user_settings WHERE userEmail = :userEmail")
    fun getUserSettings(userEmail: String): LiveData<UserSettings>
    
    @Query("UPDATE user_settings SET notificationsEnabled = :enabled WHERE userEmail = :userEmail")
    suspend fun updateNotificationSettings(userEmail: String, enabled: Boolean)
    
    @Query("UPDATE user_settings SET dailyWorkoutReminder = :enabled WHERE userEmail = :userEmail")
    suspend fun updateDailyReminder(userEmail: String, enabled: Boolean)
    
    @Query("UPDATE user_settings SET reminderTime = :time WHERE userEmail = :userEmail")
    suspend fun updateReminderTime(userEmail: String, time: String)
    
    @Query("UPDATE user_settings SET darkModeEnabled = :enabled WHERE userEmail = :userEmail")
    suspend fun updateDarkMode(userEmail: String, enabled: Boolean)
    
    @Query("UPDATE user_settings SET soundEffectsEnabled = :enabled WHERE userEmail = :userEmail")
    suspend fun updateSoundEffects(userEmail: String, enabled: Boolean)
    
    @Query("UPDATE user_settings SET showOnLeaderboard = :visible WHERE userEmail = :userEmail")
    suspend fun updateLeaderboardVisibility(userEmail: String, visible: Boolean)
    
    @Query("UPDATE user_settings SET measurementUnit = :unit WHERE userEmail = :userEmail")
    suspend fun updateMeasurementUnit(userEmail: String, unit: String)
}
