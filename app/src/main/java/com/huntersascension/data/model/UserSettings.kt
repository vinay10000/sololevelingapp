package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_settings",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["email"],
            childColumns = ["userEmail"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserSettings(
    @PrimaryKey
    val userEmail: String,
    val notificationsEnabled: Boolean = true,
    val dailyWorkoutReminder: Boolean = true,
    val reminderTime: String = "18:00", // 24-hour format
    val darkModeEnabled: Boolean = false,
    val soundEffectsEnabled: Boolean = true,
    val shareWorkoutsAutomatically: Boolean = false,
    val measurementUnit: String = "metric", // metric or imperial
    val showOnLeaderboard: Boolean = true
)
