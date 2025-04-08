package com.huntersascension.ui.util

import com.huntersascension.data.model.Rank
import com.huntersascension.data.model.WorkoutDifficulty
import com.huntersascension.data.model.WorkoutIntensity
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Utility class for various game mechanic calculations
 */
object CalculationUtils {
    
    /**
     * Calculates the experience needed for a given level
     * Uses a standard RPG progression curve
     */
    fun calculateExpForLevel(level: Int): Int {
        return (100 * (1.5f.pow(level - 1))).roundToInt()
    }
    
    /**
     * Calculates calories burned based on workout parameters
     */
    fun calculateCaloriesBurned(
        durationMinutes: Int,
        intensity: WorkoutIntensity,
        difficulty: WorkoutDifficulty,
        weightKg: Float = 70f // default weight assumption
    ): Int {
        // Base metabolic rate (calories per minute)
        val baseBurnRate = when (difficulty) {
            WorkoutDifficulty.EASY -> 4f
            WorkoutDifficulty.MEDIUM -> 6f
            WorkoutDifficulty.HARD -> 8f
            WorkoutDifficulty.EXTREME -> 10f
        }
        
        // Apply intensity modifier
        val intensityMultiplier = when (intensity) {
            WorkoutIntensity.LIGHT -> 0.8f
            WorkoutIntensity.NORMAL -> 1.0f
            WorkoutIntensity.INTENSE -> 1.2f
            WorkoutIntensity.MAXIMUM -> 1.5f
        }
        
        // Weight modifier (calories burn faster for heavier individuals)
        val weightMultiplier = weightKg / 70f
        
        // Calculate and round to nearest integer
        return (baseBurnRate * intensityMultiplier * weightMultiplier * durationMinutes).roundToInt()
    }
    
    /**
     * Calculates the streak bonus multiplier
     */
    fun calculateStreakMultiplier(streakDays: Int): Float {
        return when {
            streakDays < 3 -> 1.0f
            streakDays < 7 -> 1.05f
            streakDays < 14 -> 1.1f
            streakDays < 30 -> 1.15f
            streakDays < 60 -> 1.2f
            streakDays < 100 -> 1.25f
            else -> 1.3f
        }
    }
    
    /**
     * Calculates the rank up requirements for a given rank
     */
    fun calculateRankUpRequirements(currentRank: Rank): RankUpRequirements? {
        val nextRank = currentRank.next() ?: return null
        
        return when (nextRank) {
            Rank.D -> RankUpRequirements(
                expRequired = 1000,
                strengthRequired = 15,
                enduranceRequired = 12,
                agilityRequired = 10,
                vitalityRequired = 10
            )
            Rank.C -> RankUpRequirements(
                expRequired = 5000,
                strengthRequired = 25,
                enduranceRequired = 20,
                agilityRequired = 18,
                vitalityRequired = 20
            )
            Rank.B -> RankUpRequirements(
                expRequired = 15000,
                strengthRequired = 40,
                enduranceRequired = 35,
                agilityRequired = 32,
                vitalityRequired = 38
            )
            Rank.A -> RankUpRequirements(
                expRequired = 30000,
                strengthRequired = 60,
                enduranceRequired = 55,
                agilityRequired = 50,
                vitalityRequired = 58
            )
            Rank.S -> RankUpRequirements(
                expRequired = 50000,
                strengthRequired = 85,
                enduranceRequired = 80,
                agilityRequired = 75,
                vitalityRequired = 85
            )
            else -> null
        }
    }
    
    /**
     * Checks if the user has maintained a streak today
     */
    fun hasWorkoutStreakToday(lastWorkoutDate: Date?): Boolean {
        if (lastWorkoutDate == null) return false
        
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()
        calendar.time = lastWorkoutDate
        
        // Check if the last workout was today
        return (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
    }
    
    /**
     * Checks if the streak is still valid or broken
     */
    fun isStreakValid(lastWorkoutDate: Date?): Boolean {
        if (lastWorkoutDate == null) return false
        
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()
        calendar.time = lastWorkoutDate
        
        // Check if the last workout was yesterday or today
        return hasWorkoutStreakToday(lastWorkoutDate) || 
               (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1)
    }
    
    /**
     * Data class to hold rank up requirements
     */
    data class RankUpRequirements(
        val expRequired: Int,
        val strengthRequired: Int,
        val enduranceRequired: Int,
        val agilityRequired: Int,
        val vitalityRequired: Int,
        val intelligenceRequired: Int = 0,
        val luckRequired: Int = 0
    )
}
