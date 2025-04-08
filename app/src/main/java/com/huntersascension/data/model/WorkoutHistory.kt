package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a completed workout
 */
@Entity(
    tableName = "workout_history",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["username"],
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
        Index("username"),
        Index("workoutId")
    ]
)
data class WorkoutHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val workoutId: Long?,
    val workoutName: String,
    val startTime: Date,
    val endTime: Date,
    val duration: Int, // in seconds
    val caloriesBurned: Int = 0,
    val expEarned: Int,
    val strengthGained: Int = 0,
    val enduranceGained: Int = 0,
    val agilityGained: Int = 0,
    val vitalityGained: Int = 0,
    val intelligenceGained: Int = 0,
    val luckGained: Int = 0,
    val notes: String? = null,
    val rating: Int? = null, // 1-5 rating
    val mood: String? = null, // Pre-workout mood
    val energyLevel: Int? = null, // 1-10 energy level
    val difficulty: Int? = null, // 1-10 perceived difficulty
    val completionPercentage: Float = 100f // Percentage of workout completed
)
