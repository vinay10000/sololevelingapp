package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Entity representing a user
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Username for login
     */
    val username: String,
    
    /**
     * Email address
     */
    val email: String,
    
    /**
     * Hashed password
     */
    val password: String,
    
    /**
     * Display name
     */
    val displayName: String,
    
    /**
     * Profile picture URL
     */
    val profilePictureUrl: String? = null,
    
    /**
     * User's current level
     */
    val level: Int = 1,
    
    /**
     * Current XP points
     */
    val xp: Int = 0,
    
    /**
     * XP needed for next level
     */
    val xpToNextLevel: Int = 100,
    
    /**
     * User's current rank (F, E, D, C, B, A, S, SS, SSS)
     */
    val rank: String = "F",
    
    /**
     * Strength stat value
     */
    val strengthStat: Int = 1,
    
    /**
     * Endurance stat value
     */
    val enduranceStat: Int = 1,
    
    /**
     * Agility stat value
     */
    val agilityStat: Int = 1,
    
    /**
     * Vitality stat value
     */
    val vitalityStat: Int = 1,
    
    /**
     * Total workouts completed
     */
    val totalWorkouts: Int = 0,
    
    /**
     * Total minutes spent working out
     */
    val totalWorkoutMinutes: Int = 0,
    
    /**
     * Total calories burned
     */
    val totalCaloriesBurned: Int = 0,
    
    /**
     * Current workout streak (days)
     */
    val currentStreak: Int = 0,
    
    /**
     * Best workout streak (days)
     */
    val bestStreak: Int = 0,
    
    /**
     * Date of the last completed workout
     */
    val lastWorkoutDate: Date? = null,
    
    /**
     * Last login date
     */
    val lastLoginDate: Date? = null,
    
    /**
     * Registration date
     */
    val registrationDate: Date,
    
    /**
     * User goals (serialized JSON)
     */
    val goals: String? = null,
    
    /**
     * User trophies (serialized JSON)
     */
    val trophies: String? = null,
    
    /**
     * User achievements (serialized JSON)
     */
    val achievements: String? = null
)
