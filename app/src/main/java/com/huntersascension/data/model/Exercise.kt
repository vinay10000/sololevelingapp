package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Enum representing exercise categories
 */
enum class ExerciseCategory {
    STRENGTH, CARDIO, FLEXIBILITY, BALANCE, MOBILITY, PLYOMETRIC, HIIT;
    
    companion object {
        fun getPrimaryStat(category: ExerciseCategory): Stat {
            return when (category) {
                STRENGTH -> Stat.STRENGTH
                CARDIO -> Stat.ENDURANCE
                FLEXIBILITY -> Stat.AGILITY
                BALANCE -> Stat.AGILITY
                MOBILITY -> Stat.VITALITY
                PLYOMETRIC -> Stat.STRENGTH
                HIIT -> Stat.VITALITY
            }
        }
    }
}

/**
 * Enum representing exercise types
 */
enum class ExerciseType {
    REPETITION_BASED, // Counted in reps
    TIME_BASED, // Counted in seconds
    DISTANCE_BASED, // Counted in meters/kilometers
    WEIGHT_BASED, // Focus on weight lifted
    COMBINED // Multiple metrics
}

/**
 * Enum representing muscle groups
 */
enum class MuscleGroup {
    CHEST, BACK, SHOULDERS, BICEPS, TRICEPS, FOREARMS, 
    QUADRICEPS, HAMSTRINGS, CALVES, GLUTES, 
    ABS, OBLIQUES, LOWER_BACK,
    FULL_BODY, UPPER_BODY, LOWER_BODY, CORE
}

/**
 * Exercise entity representing a single exercise
 */
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey
    val exerciseId: String = UUID.randomUUID().toString(),
    
    // Basic info
    val name: String,
    val description: String,
    val instructions: String, // How to perform the exercise
    val category: ExerciseCategory,
    val type: ExerciseType,
    
    // Muscle groups worked
    val primaryMuscle: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    
    // Stats affected
    val primaryStat: Stat,
    val secondaryStat: Stat? = null,
    
    // Difficulty
    val difficulty: Int, // 1-5 scale
    
    // Media
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    
    // Requirements
    val equipment: List<String> = emptyList(),
    val requiredRank: Rank = Rank.E,
    
    // Metrics
    val defaultSets: Int = 3,
    val defaultReps: Int? = null,
    val defaultWeight: Float? = null, // kg
    val defaultDuration: Int? = null, // seconds
    val defaultDistance: Float? = null, // meters
    val defaultRestTime: Int = 60, // seconds
    
    // Flags
    val isLocked: Boolean = false,
    val isCustom: Boolean = false,
    val createdBy: String? = null,
    val createdDate: Date = Date()
) {
    /**
     * Gets a display string for the default values
     */
    fun getDefaultsDisplayString(): String {
        return when (type) {
            ExerciseType.REPETITION_BASED -> "$defaultSets sets of $defaultReps reps"
            ExerciseType.TIME_BASED -> "$defaultSets sets of ${defaultDuration ?: 30} seconds"
            ExerciseType.DISTANCE_BASED -> "$defaultDistance meters"
            ExerciseType.WEIGHT_BASED -> "$defaultSets sets of $defaultReps reps at $defaultWeight kg"
            ExerciseType.COMBINED -> {
                val parts = mutableListOf<String>()
                defaultReps?.let { parts.add("$it reps") }
                defaultDuration?.let { parts.add("$it seconds") }
                defaultWeight?.let { parts.add("$it kg") }
                "$defaultSets sets of ${parts.joinToString(", ")}"
            }
        }
    }
    
    /**
     * Checks if the exercise requires equipment
     */
    fun requiresEquipment(): Boolean {
        return equipment.isNotEmpty()
    }
    
    /**
     * Gets the recommended rest time based on type and difficulty
     */
    fun getRecommendedRestTime(): Int {
        // Higher difficulty and strength exercises typically need more rest
        return when {
            category == ExerciseCategory.STRENGTH && difficulty >= 4 -> 90
            category == ExerciseCategory.STRENGTH -> 60
            type == ExerciseType.TIME_BASED && difficulty >= 4 -> 45
            type == ExerciseType.TIME_BASED -> 30
            else -> defaultRestTime
        }
    }
}
