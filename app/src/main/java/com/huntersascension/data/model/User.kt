package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val username: String,
    val passwordHash: String,
    val level: Int = 1,
    val rank: String = "E",
    val experience: Int = 0,
    val strength: Int = 10,
    val agility: Int = 10,
    val vitality: Int = 10,
    val intelligence: Int = 10,
    val luck: Int = 10,
    val totalWorkouts: Int = 0,
    val consecutiveStreak: Int = 0,
    val lastWorkoutDate: Date? = null,
    val maxStreak: Int = 0
)
