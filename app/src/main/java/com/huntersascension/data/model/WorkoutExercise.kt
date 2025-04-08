package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing an exercise within a workout
 */
@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("workoutId"),
        Index("exerciseId"),
        Index("workoutId", "orderIndex", unique = true)
    ]
)
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * ID of the workout this exercise belongs to
     */
    val workoutId: Long,
    
    /**
     * ID of the exercise
     */
    val exerciseId: Long,
    
    /**
     * Position of this exercise in the workout
     */
    val orderIndex: Int,
    
    /**
     * Number of sets to perform
     */
    val sets: Int,
    
    /**
     * Number of repetitions per set (for strength exercises)
     */
    val reps: Int? = null,
    
    /**
     * Weight in kg (for strength exercises)
     */
    val weight: Float? = null,
    
    /**
     * Whether this is a bodyweight exercise
     */
    val isBodyweight: Boolean = false,
    
    /**
     * Duration in minutes (for cardio exercises)
     */
    val duration: Int? = null,
    
    /**
     * Distance in kilometers (for cardio exercises)
     */
    val distance: Float? = null,
    
    /**
     * Hold time in seconds (for flexibility exercises)
     */
    val holdTime: Int? = null,
    
    /**
     * Rest time between sets in seconds
     */
    val restTime: Int = 60,
    
    /**
     * Optional notes for this exercise
     */
    val notes: String? = null
)
