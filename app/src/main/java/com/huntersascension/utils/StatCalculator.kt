package com.huntersascension.utils

import com.huntersascension.data.entity.Workout
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Calculates stat gains from workouts
 */
class StatCalculator {
    
    /**
     * Calculate stat gains based on exercise type, reps, and difficulty
     */
    fun calculateStatGains(exerciseType: String, reps: Int, difficulty: String): Map<String, Int> {
        val difficultyMultiplier = when (difficulty) {
            Workout.DIFFICULTY_EASY -> 1.0f
            Workout.DIFFICULTY_MEDIUM -> 1.5f
            Workout.DIFFICULTY_HARD -> 2.0f
            Workout.DIFFICULTY_BOSS -> 3.0f
            else -> 1.0f
        }
        
        // Base stat gains based on exercise type
        val baseGains = when (exerciseType) {
            Workout.EXERCISE_PUSHUPS -> mapOf(
                "STR" to 3,
                "AGI" to 1,
                "VIT" to 2,
                "INT" to 1,
                "LUK" to 0
            )
            
            Workout.EXERCISE_SQUATS -> mapOf(
                "STR" to 2,
                "AGI" to 2,
                "VIT" to 3,
                "INT" to 1,
                "LUK" to 0
            )
            
            Workout.EXERCISE_RUNNING -> mapOf(
                "STR" to 1,
                "AGI" to 3,
                "VIT" to 3,
                "INT" to 1,
                "LUK" to 0
            )
            
            else -> mapOf(
                "STR" to 1,
                "AGI" to 1,
                "VIT" to 1,
                "INT" to 1,
                "LUK" to 1
            )
        }
        
        // Calculate rep factor (diminishing returns for very high reps)
        val repFactor = sqrt(reps.toFloat()) / 5f
        
        // Apply multipliers and round to integer
        return baseGains.mapValues {
            max(1, (it.value * difficultyMultiplier * repFactor).toInt())
        }
    }
}
