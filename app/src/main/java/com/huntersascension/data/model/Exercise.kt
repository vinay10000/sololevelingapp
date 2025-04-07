package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutId: Long,
    val name: String,
    val type: String, // "weight_training", "bodyweight", "cardio", "stretching", "other"
    val sets: Int = 0,
    val reps: Int = 0,
    val weight: Float = 0f,
    val time: Long = 0, // Duration in seconds
    val distance: Float = 0f, // Distance in meters
    val caloriesBurned: Int = 0,
    val experienceValue: Int = 0, // XP gained from completing this exercise
    val completed: Boolean = false,
    val notes: String = "",
    val primaryStat: String = "strength" // The stat most affected by this exercise
)
