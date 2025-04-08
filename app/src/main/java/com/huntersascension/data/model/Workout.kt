package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Enum representing types of workouts
 */
enum class WorkoutType {
    STRENGTH,
    CARDIO,
    FLEXIBILITY,
    BALANCE,
    MOBILITY,
    HIIT,
    CIRCUIT,
    CUSTOM
}

/**
 * Enum representing difficulty levels of workouts
 */
enum class WorkoutDifficulty {
    EASY,
    MEDIUM,
    HARD,
    EXPERT
}

/**
 * Entity representing a workout
 */
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val createdBy: String, // username of creator, "system" for pre-defined workouts
    val type: WorkoutType,
    val difficulty: WorkoutDifficulty,
    val primaryStat: Stat,
    val secondaryStat: Stat? = null,
    val iconPath: String? = null,
    val isPublic: Boolean = false,
    val isRecommended: Boolean = false,
    val requiredRank: Rank? = null,
    val estimatedDuration: Int, // in minutes
    val baseExpReward: Int,
    val requiredLevel: Int = 1,
    val isCustom: Boolean = false,
    val createdDate: Date = Date(),
    val updatedDate: Date = Date()
)
