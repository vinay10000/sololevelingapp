package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Entity representing an exercise within a workout
 */
@Entity(
    tableName = "workout_exercises",
    primaryKeys = ["workoutId", "exerciseId", "order"],
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
        Index("exerciseId")
    ]
)
data class WorkoutExercise(
    val workoutId: Long,
    val exerciseId: Long,
    val order: Int, // order of the exercise in the workout
    val sets: Int? = null,
    val reps: Int? = null,
    val duration: Int? = null, // in seconds
    val distance: Float? = null, // in meters
    val weight: Float? = null, // in kg
    val restTime: Int = 60, // in seconds
    val notes: String? = null
)
