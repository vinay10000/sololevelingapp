package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Enum representing workout types
 */
enum class WorkoutType {
    STRENGTH, CARDIO, HIIT, FLEXIBILITY, ENDURANCE, BALANCE, CUSTOM;
    
    companion object {
        fun getMainStat(type: WorkoutType): Stat {
            return when (type) {
                STRENGTH -> Stat.STRENGTH
                CARDIO, ENDURANCE -> Stat.ENDURANCE
                HIIT -> Stat.VITALITY
                FLEXIBILITY, BALANCE -> Stat.AGILITY
                CUSTOM -> Stat.STRENGTH // Default for custom
            }
        }
        
        fun getSecondaryStat(type: WorkoutType): Stat? {
            return when (type) {
                STRENGTH -> Stat.VITALITY
                CARDIO -> Stat.AGILITY
                HIIT -> Stat.STRENGTH
                FLEXIBILITY -> Stat.VITALITY
                ENDURANCE -> Stat.VITALITY
                BALANCE -> Stat.INTELLIGENCE
                CUSTOM -> null // None for custom
            }
        }
    }
}

/**
 * Enum representing workout difficulty
 */
enum class WorkoutDifficulty {
    EASY, MEDIUM, HARD, EXTREME;
    
    fun getExpMultiplier(): Float {
        return when (this) {
            EASY -> 0.8f
            MEDIUM -> 1.0f
            HARD -> 1.3f
            EXTREME -> 1.6f
        }
    }
    
    fun getCaloriesPerMinute(): Int {
        return when (this) {
            EASY -> 5
            MEDIUM -> 7
            HARD -> 10
            EXTREME -> 12
        }
    }
}

/**
 * Enum for workout intensity when performing
 */
enum class WorkoutIntensity {
    LIGHT, NORMAL, INTENSE, MAXIMUM;
    
    fun getExpMultiplier(): Float {
        return when (this) {
            LIGHT -> 0.8f
            NORMAL -> 1.0f
            INTENSE -> 1.2f
            MAXIMUM -> 1.5f
        }
    }
}

/**
 * Workout entity representing a single workout template
 */
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey
    val workoutId: String = UUID.randomUUID().toString(),
    
    // Basic info
    val name: String,
    val description: String,
    val createdBy: String, // Username of creator
    val createdDate: Date = Date(),
    val lastModifiedDate: Date = Date(),
    
    // Type and difficulty
    val type: WorkoutType,
    val difficulty: WorkoutDifficulty,
    
    // Stats affected
    val primaryStat: Stat,
    val secondaryStat: Stat? = null,
    
    // For client UI
    val estimatedDuration: Int = 30, // In minutes
    val baseExpReward: Int = 50, // Base XP reward
    
    // Flags
    val isPublic: Boolean = false,
    val isFavorite: Boolean = false,
    val isRecommended: Boolean = false,
    val requiredRank: Rank = Rank.E
) {
    /**
     * Calculates the experience reward for completing this workout
     */
    fun calculateExpReward(intensityMultiplier: Float = 1.0f): Int {
        // Base reward is modified by difficulty and intensity
        val difficultyMultiplier = difficulty.getExpMultiplier()
        return (baseExpReward * difficultyMultiplier * intensityMultiplier).roundToInt()
    }
    
    /**
     * Calculates calories burned for this workout at a given duration
     */
    fun calculateCaloriesBurned(durationMinutes: Int): Int {
        // Base calculation using calories per minute from difficulty
        return difficulty.getCaloriesPerMinute() * durationMinutes
    }
    
    /**
     * Gets the UI color for this workout based on primary stat
     */
    fun getStatColor(): Int {
        // This would return a color resource ID based on the primary stat
        // For use in UI to color-code workouts
        return when (primaryStat) {
            Stat.STRENGTH -> 0 // Resource IDs would go here
            Stat.ENDURANCE -> 1
            Stat.AGILITY -> 2
            Stat.VITALITY -> 3
            Stat.INTELLIGENCE -> 4
            Stat.LUCK -> 5
        }
    }
}

/**
 * Junction entity to connect Workouts and Exercises
 */
@Entity(
    tableName = "workout_exercises",
    primaryKeys = ["workoutId", "exerciseId", "orderIndex"]
)
data class WorkoutExercise(
    val workoutId: String,
    val exerciseId: String,
    val orderIndex: Int, // For ordering exercises in a workout
    
    // Exercise parameters
    val sets: Int,
    val reps: Int? = null, // Null for time-based exercises
    val weight: Float? = null, // Null for bodyweight exercises
    val duration: Int? = null, // Seconds, null for rep-based exercises
    val distance: Float? = null, // For distance-based exercises (meters)
    val restTime: Int = 60 // Rest time between sets (seconds)
)

/**
 * Entity to track workout history
 */
@Entity(tableName = "workout_history")
data class WorkoutHistory(
    @PrimaryKey
    val historyId: String = UUID.randomUUID().toString(),
    val workoutId: String,
    val username: String,
    val startTime: Date,
    val endTime: Date? = null,
    val duration: Int? = null, // Seconds
    val calories: Int? = null,
    val exp: Int? = null,
    
    // Stat gains
    val strengthGain: Int? = null,
    val enduranceGain: Int? = null,
    val agilityGain: Int? = null,
    val vitalityGain: Int? = null,
    val intelligenceGain: Int? = null,
    val luckGain: Int? = null,
    
    // User feedback
    val rating: Int? = null, // 1-5 stars
    val notes: String? = null,
    
    // Completed or not
    val isCompleted: Boolean = false
)

/**
 * Entity to track individual exercise history within a workout
 */
@Entity(
    tableName = "exercise_history",
    primaryKeys = ["historyId", "exerciseId", "setNumber"]
)
data class ExerciseHistory(
    val historyId: String,
    val exerciseId: String,
    val setNumber: Int = 1,
    
    // Performance metrics
    val reps: Int? = null,
    val weight: Float? = null,
    val duration: Int? = null, // Seconds
    val distance: Float? = null,
    
    // Completed or not
    val isCompleted: Boolean = false
)
