package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.huntersascension.data.converters.DateConverter
import java.util.Date
import java.util.UUID

/**
 * WorkoutHistory entity that represents a completed workout session
 */
@Entity(tableName = "workout_history")
@TypeConverters(DateConverter::class)
data class WorkoutHistory(
    @PrimaryKey
    val historyId: String = UUID.randomUUID().toString(),
    
    // Reference to workout and user
    val workoutId: String,
    val username: String,
    
    // Session details
    val startTime: Date,
    val endTime: Date? = null,
    val totalDuration: Int = 0, // in seconds
    val caloriesBurned: Int = 0,
    val expEarned: Int = 0,
    
    // Performance metrics
    val intensity: WorkoutIntensity = WorkoutIntensity.NORMAL,
    val completionPercentage: Float = 100f,
    val userRating: Int? = null, // 1-5 stars
    val userNotes: String? = null,
    
    // Stat gains
    val strengthGain: Int = 0,
    val enduranceGain: Int = 0,
    val agilityGain: Int = 0,
    val vitalityGain: Int = 0,
    val intelligenceGain: Int = 0,
    val luckGain: Int = 0,
    
    // Flags
    val isCompleted: Boolean = false,
    val wasStreakDay: Boolean = false
) {
    /**
     * Returns true if the workout is currently in progress
     */
    val isInProgress: Boolean
        get() = startTime != null && endTime == null
    
    /**
     * Returns the total time in minutes
     */
    val durationMinutes: Int
        get() = totalDuration / 60
    
    /**
     * Returns the formatted duration string (e.g., "45m 30s")
     */
    fun getFormattedDuration(): String {
        val minutes = totalDuration / 60
        val seconds = totalDuration % 60
        return "${minutes}m ${seconds}s"
    }
    
    /**
     * Returns the total stat gain from this workout
     */
    val totalStatGain: Int
        get() = strengthGain + enduranceGain + agilityGain + vitalityGain + intelligenceGain + luckGain
}

/**
 * ExerciseHistory entity that represents a completed exercise within a workout
 */
@Entity(tableName = "exercise_history")
data class ExerciseHistory(
    @PrimaryKey
    val exerciseHistoryId: String = UUID.randomUUID().toString(),
    
    // Reference to workout history and exercise
    val historyId: String,
    val exerciseId: String,
    
    // Performance details
    val setsCompleted: Int = 0,
    val totalReps: Int = 0,
    val maxWeight: Float? = null,
    val totalWeight: Float? = null, // Volume (reps * weight)
    val duration: Int? = null, // in seconds
    val distance: Float? = null, // in meters
    
    // User feedback
    val difficultyRating: Int? = null, // 1-5 scale
    val notes: String? = null
)

/**
 * Defines workout intensity levels
 */
enum class WorkoutIntensity {
    LIGHT,
    NORMAL,
    INTENSE,
    MAXIMUM;
    
    /**
     * Returns the experience multiplier for this intensity
     */
    fun getExpMultiplier(): Float {
        return when (this) {
            LIGHT -> 0.8f
            NORMAL -> 1.0f
            INTENSE -> 1.25f
            MAXIMUM -> 1.5f
        }
    }
}
