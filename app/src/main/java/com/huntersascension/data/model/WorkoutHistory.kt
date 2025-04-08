package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

/**
 * Entity representing a completed workout
 */
@Entity(
    tableName = "workout_history",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("userId"),
        Index("workoutId")
    ]
)
data class WorkoutHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * ID of the user who completed the workout
     */
    val userId: Long,
    
    /**
     * ID of the workout that was completed (can be null if workout was deleted)
     */
    val workoutId: Long,
    
    /**
     * Name of the workout (stored separately in case workout is deleted)
     */
    val workoutName: String,
    
    /**
     * Type of the workout
     */
    val workoutType: String,
    
    /**
     * When the workout was started
     */
    val startTime: Date,
    
    /**
     * When the workout was completed
     */
    val endTime: Date,
    
    /**
     * Duration of the workout in minutes
     */
    val durationMinutes: Int,
    
    /**
     * Number of exercises completed
     */
    val exercisesCompleted: Int,
    
    /**
     * Estimated calories burned
     */
    val caloriesBurned: Int,
    
    /**
     * XP earned from the workout
     */
    val xpEarned: Int,
    
    /**
     * Primary stat points gained
     */
    val primaryStatGained: Int,
    
    /**
     * Secondary stat points gained
     */
    val secondaryStatGained: Int,
    
    /**
     * User notes added after the workout
     */
    val notes: String? = null,
    
    /**
     * User rating of the workout (1-5)
     */
    val userRating: Int? = null
)
