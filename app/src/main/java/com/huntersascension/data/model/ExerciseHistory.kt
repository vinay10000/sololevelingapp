package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a completed exercise within a workout
 */
@Entity(
    tableName = "exercise_history",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutHistory::class,
            parentColumns = ["id"],
            childColumns = ["workoutHistoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("workoutHistoryId"),
        Index("exerciseId")
    ]
)
data class ExerciseHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * ID of the workout history this exercise belongs to
     */
    val workoutHistoryId: Long,
    
    /**
     * ID of the exercise (can be null if exercise was deleted)
     */
    val exerciseId: Long,
    
    /**
     * Name of the exercise (stored separately in case exercise is deleted)
     */
    val exerciseName: String,
    
    /**
     * Type of the exercise
     */
    val exerciseType: String,
    
    /**
     * Number of sets completed
     */
    val setsCompleted: Int,
    
    /**
     * Number of repetitions per set (for strength exercises)
     */
    val repsPerSet: Int? = null,
    
    /**
     * Weight used in kg (for strength exercises)
     */
    val weightUsed: Float? = null,
    
    /**
     * Whether this was a bodyweight exercise
     */
    val wasBodyweight: Boolean = false,
    
    /**
     * Duration in minutes (for cardio exercises)
     */
    val durationMinutes: Int? = null,
    
    /**
     * Distance in kilometers (for cardio exercises)
     */
    val distanceKm: Float? = null,
    
    /**
     * Hold time in seconds (for flexibility exercises)
     */
    val holdTimeSeconds: Int? = null,
    
    /**
     * Estimated calories burned from this exercise
     */
    val caloriesBurned: Int,
    
    /**
     * XP earned from this exercise
     */
    val xpEarned: Int,
    
    /**
     * User notes for this exercise
     */
    val notes: String? = null
)
