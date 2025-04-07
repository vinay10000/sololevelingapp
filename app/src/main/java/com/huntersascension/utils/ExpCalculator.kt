package com.huntersascension.utils

import com.huntersascension.data.entity.Workout
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Calculates experience points from workouts
 */
class ExpCalculator {
    
    /**
     * Calculate experience points based on exercise type, difficulty, reps, duration, and streak
     */
    fun calculateExp(
        exerciseType: String,
        difficulty: String,
        reps: Int,
        duration: Int,
        streak: Int
    ): Int {
        // Base EXP per exercise type
        val baseExp = when (exerciseType) {
            Workout.EXERCISE_PUSHUPS -> 10
            Workout.EXERCISE_SQUATS -> 12
            Workout.EXERCISE_RUNNING -> 15
            else -> 10
        }
        
        // Difficulty multiplier
        val difficultyMultiplier = when (difficulty) {
            Workout.DIFFICULTY_EASY -> 1.0f
            Workout.DIFFICULTY_MEDIUM -> 1.5f
            Workout.DIFFICULTY_HARD -> 2.0f
            Workout.DIFFICULTY_BOSS -> 3.0f
            else -> 1.0f
        }
        
        // Rep scaling (square root to prevent too much EXP for high reps)
        val repFactor = sqrt(reps.toFloat()) / 2f
        
        // Streak bonus (caps at +50% for a 50-day streak)
        val streakMultiplier = 1.0f + min(streak, 50) / 100f
        
        // Calculate total EXP
        val exp = baseExp * difficultyMultiplier * repFactor * streakMultiplier
        
        // Always give at least 1 EXP
        return max(1, exp.toInt())
    }
    
    /**
     * Calculate daily EXP cap based on rank
     */
    fun calculateDailyExpCap(rank: String): Int {
        return when (rank) {
            "E" -> 200
            "D" -> 500
            "C" -> 1000
            "B" -> 2000
            "A" -> 5000
            "S" -> 10000
            else -> 200
        }
    }
}
