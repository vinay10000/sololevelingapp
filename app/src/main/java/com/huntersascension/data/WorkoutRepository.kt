package com.huntersascension.data

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.TrophyDao
import com.huntersascension.data.dao.UserDao
import com.huntersascension.data.dao.WorkoutDao
import com.huntersascension.data.entity.Trophy
import com.huntersascension.data.entity.User
import com.huntersascension.data.entity.Workout
import com.huntersascension.utils.ExpCalculator
import com.huntersascension.utils.StatCalculator
import java.util.Date

/**
 * Repository for managing Workout data
 */
class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val userDao: UserDao,
    private val trophyDao: TrophyDao,
    private val expCalculator: ExpCalculator,
    private val statCalculator: StatCalculator
) {
    /**
     * Record a new workout
     */
    suspend fun recordWorkout(
        username: String,
        exerciseType: String,
        difficulty: String,
        reps: Int,
        duration: Int = 0
    ): Result<Workout> {
        try {
            val user = userDao.getUserByUsername(username) ?: return Result.failure(Exception("User not found"))
            
            // Calculate EXP gain
            val expGained = expCalculator.calculateExp(exerciseType, difficulty, reps, duration, user.streak)
            
            // Calculate stat gains
            val statGains = statCalculator.calculateStatGains(exerciseType, reps, difficulty)
            
            // Create workout record
            val workout = Workout(
                username = username,
                exerciseType = exerciseType,
                difficulty = difficulty,
                reps = reps,
                duration = duration,
                expGained = expGained,
                strGained = statGains["STR"] ?: 0,
                agiGained = statGains["AGI"] ?: 0,
                vitGained = statGains["VIT"] ?: 0,
                intGained = statGains["INT"] ?: 0,
                lukGained = statGains["LUK"] ?: 0
            )
            
            // Insert workout
            val workoutId = workoutDao.insert(workout)
            
            // Update user stats
            userDao.updateStats(
                username,
                user.strStat + (statGains["STR"] ?: 0),
                user.agiStat + (statGains["AGI"] ?: 0),
                user.vitStat + (statGains["VIT"] ?: 0),
                user.intStat + (statGains["INT"] ?: 0),
                user.lukStat + (statGains["LUK"] ?: 0)
            )
            
            // Update EXP
            userDao.addExp(username, expGained)
            
            // Update streak
            updateStreak(user)
            
            // Check for workout count trophies
            checkAndAwardWorkoutCountTrophies(username)
            
            return Result.success(workout)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
    
    /**
     * Get all workouts for a user
     */
    fun getWorkoutsByUser(username: String): LiveData<List<Workout>> {
        return workoutDao.getWorkoutsByUser(username)
    }
    
    /**
     * Get total workout count for a user
     */
    suspend fun getWorkoutCount(username: String): Int {
        return workoutDao.getWorkoutCount(username)
    }
    
    /**
     * Update streak after workout
     */
    private suspend fun updateStreak(user: User) {
        val username = user.username
        val now = Date()
        val lastWorkout = user.lastWorkoutDate
        
        if (lastWorkout == null) {
            // First workout, set streak to 1
            userDao.updateStreak(username, 1, now)
            return
        }
        
        // Calculate days between workouts
        val lastWorkoutDay = lastWorkout.time / (24 * 60 * 60 * 1000)
        val todayDay = now.time / (24 * 60 * 60 * 1000)
        val dayDifference = todayDay - lastWorkoutDay
        
        when {
            // Same day, no streak change
            dayDifference == 0L -> {
                // Update last workout date but don't change streak
                userDao.updateStreak(username, user.streak, now)
            }
            // Consecutive day, increase streak
            dayDifference == 1L -> {
                userDao.updateStreak(username, user.streak + 1, now)
                
                // Check for streak trophies
                val updatedUser = userDao.getUserByUsername(username)
                updatedUser?.let {
                    checkAndAwardStreakTrophies(username, it.streak)
                }
            }
            // Gap in days, reset streak
            else -> {
                userDao.updateStreak(username, 1, now)
            }
        }
    }
    
    /**
     * Check and award workout count trophies
     */
    private suspend fun checkAndAwardWorkoutCountTrophies(username: String) {
        val workoutCount = workoutDao.getWorkoutCount(username)
        
        // Define trophy thresholds
        val workoutTrophies = mapOf(
            10 to Trophy.TROPHY_WORKOUT_10,
            50 to Trophy.TROPHY_WORKOUT_50,
            100 to Trophy.TROPHY_WORKOUT_100
        )
        
        // Check if user has reached any trophy thresholds
        for ((threshold, trophyName) in workoutTrophies) {
            if (workoutCount >= threshold && !trophyDao.hasTrophy(username, trophyName)) {
                // Award the trophy
                val description = "Completed $threshold workouts"
                val bonus = when (threshold) {
                    10 -> "+1 INT"
                    50 -> "+3 to all stats"
                    100 -> "+10% EXP gain"
                    else -> "No bonus"
                }
                
                val trophy = Trophy(
                    username = username,
                    name = trophyName,
                    description = description,
                    bonus = bonus
                )
                trophyDao.insert(trophy)
            }
        }
    }
    
    /**
     * Check and award streak trophies
     */
    private suspend fun checkAndAwardStreakTrophies(username: String, streak: Int) {
        // Define trophy thresholds
        val streakTrophies = mapOf(
            7 to Trophy.TROPHY_STREAK_7,
            30 to Trophy.TROPHY_STREAK_30,
            60 to Trophy.TROPHY_STREAK_60,
            100 to Trophy.TROPHY_STREAK_100
        )
        
        // Check if user has reached any trophy thresholds
        for ((threshold, trophyName) in streakTrophies) {
            if (streak >= threshold && !trophyDao.hasTrophy(username, trophyName)) {
                // Award the trophy
                val description: String
                val bonus: String
                
                when (threshold) {
                    7 -> {
                        description = "Maintained a streak of 7 days"
                        bonus = "+1% STR"
                    }
                    30 -> {
                        description = "Maintained a streak of 30 days"
                        bonus = "+2% all stats"
                    }
                    60 -> {
                        description = "Maintained a streak of 60 days"
                        bonus = "Special Avatar Frame"
                    }
                    100 -> {
                        description = "Maintained a streak of 100 days"
                        bonus = "Unique Cosmetic"
                    }
                    else -> {
                        description = "Trophy for $threshold day streak"
                        bonus = "No bonus"
                    }
                }
                
                val trophy = Trophy(
                    username = username,
                    name = trophyName,
                    description = description,
                    bonus = bonus
                )
                trophyDao.insert(trophy)
            }
        }
    }
}
