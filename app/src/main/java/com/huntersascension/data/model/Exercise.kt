package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Enum representing categories of exercises
 */
enum class ExerciseCategory {
    STRENGTH,
    CARDIO,
    FLEXIBILITY,
    BALANCE,
    MOBILITY
}

/**
 * Enum representing types of exercises based on how they're measured
 */
enum class ExerciseType {
    REPETITION_BASED, // Measured in reps (push-ups, pull-ups)
    TIME_BASED,       // Measured in seconds (plank, wall sit)
    DISTANCE_BASED,   // Measured in meters/kilometers (running, cycling)
    WEIGHT_BASED      // Measured in weight lifted (deadlift, bench press)
}

/**
 * Enum representing muscle groups
 */
enum class MuscleGroup {
    CHEST,
    BACK,
    SHOULDERS,
    BICEPS,
    TRICEPS,
    FOREARMS,
    ABS,
    OBLIQUES,
    LOWER_BACK,
    GLUTES,
    QUADRICEPS,
    HAMSTRINGS,
    CALVES,
    CORE,
    UPPER_BODY,
    LOWER_BODY,
    FULL_BODY
}

/**
 * Entity representing an exercise
 */
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val instructions: String,
    val category: ExerciseCategory,
    val type: ExerciseType,
    val primaryMuscle: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    val primaryStat: Stat,
    val secondaryStat: Stat? = null,
    val difficulty: Int = 2, // 1-5 scale
    val iconPath: String? = null,
    val defaultSets: Int? = null,
    val defaultReps: Int? = null,
    val defaultDuration: Int? = null, // in seconds
    val defaultDistance: Float? = null, // in meters
    val defaultWeight: Float? = null, // in kg
    val defaultRestTime: Int = 60, // in seconds
    val isCustom: Boolean = false,
    val createdBy: String? = null,
    val createdDate: Date = Date()
)
