package com.huntersascension.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.TrophyDao
import com.huntersascension.data.dao.UserDao
import com.huntersascension.data.dao.WorkoutDao
import com.huntersascension.data.entity.Trophy
import com.huntersascension.data.entity.User
import com.huntersascension.utils.ExpCalculator
import com.huntersascension.utils.RankManager
import com.huntersascension.utils.StatCalculator
import java.security.MessageDigest
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Repository for managing User data
 */
class UserRepository(
    private val userDao: UserDao,
    private val workoutDao: WorkoutDao,
    private val trophyDao: TrophyDao,
    private val rankManager: RankManager,
    private val statCalculator: StatCalculator,
    private val expCalculator: ExpCalculator
) {
    /**
     * Register a new user
     */
    suspend fun registerUser(username: String, password: String): Result<User> {
        return try {
            // Check if username already exists
            val existingUser = userDao.getUserByUsername(username)
            if (existingUser != null) {
                return Result.failure(Exception("Username already exists"))
            }
            
            // Create new user with hashed password
            val passwordHash = hashPassword(password)
            val newUser = User(
                username = username,
                passwordHash = passwordHash
            )
            
            userDao.insert(newUser)
            Result.success(newUser)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error registering user", e)
            Result.failure(e)
        }
    }
    
    /**
     * Login a user
     */
    suspend fun loginUser(username: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByUsername(username)
                ?: return Result.failure(Exception("User not found"))
            
            val passwordHash = hashPassword(password)
            if (user.passwordHash != passwordHash) {
                return Result.failure(Exception("Invalid password"))
            }
            
            // Check if streak needs to be updated
            updateStreak(user)
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error logging in user", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get user by username
     */
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
    
    /**
     * Get user by username as LiveData
     */
    fun getUserByUsernameLive(username: String): LiveData<User?> {
        return userDao.getUserByUsernameLive(username)
    }
    
    /**
     * Get all trophies for a user
     */
    fun getTrophiesByUser(username: String): LiveData<List<Trophy>> {
        return trophyDao.getTrophiesByUser(username)
    }
    
    /**
     * Update user stats
     */
    suspend fun updateStats(username: String, strGain: Int, agiGain: Int, vitGain: Int, intGain: Int, lukGain: Int) {
        val user = userDao.getUserByUsername(username) ?: return
        
        userDao.updateStats(
            username,
            user.strStat + strGain,
            user.agiStat + agiGain,
            user.vitStat + vitGain,
            user.intStat + intGain,
            user.lukStat + lukGain
        )
    }
    
    /**
     * Add experience to a user
     */
    suspend fun addExp(username: String, expAmount: Int) {
        userDao.addExp(username, expAmount)
    }
    
    /**
     * Check if user is eligible for rank up
     */
    suspend fun checkRankUpEligibility(username: String): Boolean {
        val user = userDao.getUserByUsername(username) ?: return false
        
        // Check if there's a next rank
        val nextRank = user.getNextRank() ?: return false
        
        return rankManager.isEligibleForRankUp(user, nextRank)
    }
    
    /**
     * Attempt to rank up a user
     */
    suspend fun rankUp(username: String): Result<User> {
        val user = userDao.getUserByUsername(username) ?: return Result.failure(Exception("User not found"))
        
        // Check if there's a next rank
        val nextRank = user.getNextRank() ?: return Result.failure(Exception("Already at maximum rank"))
        
        // Check if eligible for rank up
        if (!rankManager.isEligibleForRankUp(user, nextRank)) {
            return Result.failure(Exception("Not eligible for rank up"))
        }
        
        // Perform rank up
        userDao.updateRank(username, nextRank)
        
        // Add rank up trophy
        val trophyName = when (nextRank) {
            "D" -> Trophy.TROPHY_RANK_D
            "C" -> Trophy.TROPHY_RANK_C
            "B" -> Trophy.TROPHY_RANK_B
            "A" -> Trophy.TROPHY_RANK_A
            "S" -> Trophy.TROPHY_RANK_S
            else -> null
        }
        
        trophyName?.let {
            val trophyDescription = "Achieved ${user.getNextRankDisplayName()}"
            val trophyBonus = "+"
            
            val trophy = Trophy(
                username = username,
                name = it,
                description = trophyDescription,
                bonus = trophyBonus
            )
            trophyDao.insert(trophy)
        }
        
        return Result.success(userDao.getUserByUsername(username)!!)
    }
    
    /**
     * Add a trophy to a user
     */
    suspend fun addTrophy(username: String, name: String, description: String, bonus: String): Long {
        val trophy = Trophy(
            username = username,
            name = name,
            description = description,
            bonus = bonus
        )
        return trophyDao.insert(trophy)
    }
    
    /**
     * Update streak for a user
     */
    private suspend fun updateStreak(user: User) {
        val now = Date()
        val lastWorkout = user.lastWorkoutDate
        
        if (lastWorkout == null) {
            return
        }
        
        val daysSinceLastWorkout = TimeUnit.MILLISECONDS.toDays(now.time - lastWorkout.time)
        
        when {
            // If more than 1 day has passed, reset streak
            daysSinceLastWorkout > 1 -> {
                userDao.updateStreak(user.username, 0, now)
            }
            // If exactly 1 day has passed, update the date but keep streak
            daysSinceLastWorkout == 1L -> {
                userDao.updateStreak(user.username, user.streak, now)
            }
            // If less than 1 day, do nothing
        }
    }
    
    /**
     * Hash a password
     */
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
    
    /**
     * Check and award streak trophies
     */
    suspend fun checkAndAwardStreakTrophies(username: String, streak: Int) {
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
                
                addTrophy(username, trophyName, description, bonus)
            }
        }
    }
}
