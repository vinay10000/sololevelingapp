package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a completed exercise
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
    val workoutHistoryId: Long,
    val exerciseId: Long?,
    val exerciseName: String,
    val sets: Int? = null,
    val reps: Int? = null,
    val duration: Int? = null, // in seconds
    val distance: Float? = null, // in meters
    val weight: Float? = null, // in kg
    val caloriesBurned: Int = 0,
    val completionTime: Date = Date(),
    val notes: String? = null,
    val difficulty: Int? = null // 1-10 perceived difficulty
)
