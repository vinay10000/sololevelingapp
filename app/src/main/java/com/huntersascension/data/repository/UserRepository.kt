package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.UserDao
import com.huntersascension.data.model.User
import com.huntersascension.data.model.Rank
import java.security.MessageDigest
import java.util.*

/**
 * Repository for interacting with user data
 */
class UserRepository(private val userDao: UserDao) {
    
    /**
     * Gets a user by username
     */
    fun getUserByUsername(username: String): LiveData<User?> {
        return userDao.getUserByUsername(username)
    }
    
    /**
     * Attempts to log in a user
     */
    suspend fun login(username: String, password: String): Boolean {
        val passwordHash = hashPassword(password)
        val user = userDao.getUserByUsernameSync(username)
        return user != null && user.passwordHash == passwordHash
    }
    
    /**
     * Registers a new user
     */
    suspend fun register(username: String, hunterName: String, password: String): Boolean {
        val existingUser = userDao.getUserByUsernameSync(username)
        if (existingUser != null) {
            return false
        }
        
        val passwordHash = hashPassword(password)
        val newUser = User(
            username = username,
            hunterName = hunterName,
            passwordHash = passwordHash
        )
        
        userDao.insertUser(newUser)
        return true
    }
    
    /**
     * Updates a user
     */
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
    
    /**
     * Deletes a user
     */
    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }
    
    /**
     * Gets top users by rank
     */
    fun getTopUsers(limit: Int = 10): LiveData<List<User>> {
        return userDao.getTopUsers(limit)
    }
    
    /**
     * Gets users of a specific rank
     */
    fun getUsersByRank(rank: Rank, limit: Int = 10): LiveData<List<User>> {
        return userDao.getUsersByRank(rank, limit)
    }
    
    /**
     * Adds experience points to a user
     */
    suspend fun addExp(username: String, amount: Int): Int {
        return userDao.addExp(username, amount)
    }
    
    /**
     * Adds strength points to a user
     */
    suspend fun addStrength(username: String, amount: Int): Int {
        return userDao.addStrength(username, amount)
    }
    
    /**
     * Adds endurance points to a user
     */
    suspend fun addEndurance(username: String, amount: Int): Int {
        return userDao.addEndurance(username, amount)
    }
    
    /**
     * Adds agility points to a user
     */
    suspend fun addAgility(username: String, amount: Int): Int {
        return userDao.addAgility(username, amount)
    }
    
    /**
     * Adds vitality points to a user
     */
    suspend fun addVitality(username: String, amount: Int): Int {
        return userDao.addVitality(username, amount)
    }
    
    /**
     * Adds intelligence points to a user
     */
    suspend fun addIntelligence(username: String, amount: Int): Int {
        return userDao.addIntelligence(username, amount)
    }
    
    /**
     * Adds luck points to a user
     */
    suspend fun addLuck(username: String, amount: Int): Int {
        return userDao.addLuck(username, amount)
    }
    
    /**
     * Adds trophy points to a user
     */
    suspend fun addTrophyPoints(username: String, amount: Int): Int {
        return userDao.addTrophyPoints(username, amount)
    }
    
    /**
     * Increments a user's streak
     */
    suspend fun incrementStreak(username: String): Int {
        return userDao.incrementStreak(username)
    }
    
    /**
     * Resets a user's streak
     */
    suspend fun resetStreak(username: String): Int {
        return userDao.resetStreak(username)
    }
    
    /**
     * Increments a user's workout count
     */
    suspend fun incrementWorkoutCount(username: String): Int {
        return userDao.incrementWorkoutCount(username)
    }
    
    /**
     * Adds workout minutes to a user's total
     */
    suspend fun addWorkoutMinutes(username: String, minutes: Int): Int {
        return userDao.addWorkoutMinutes(username, minutes)
    }
    
    /**
     * Adds calories burned to a user's total
     */
    suspend fun addCaloriesBurned(username: String, calories: Int): Int {
        return userDao.addCaloriesBurned(username, calories)
    }
    
    /**
     * Updates a user's level and experience
     */
    suspend fun updateLevelAndExp(username: String, level: Int, remainingExp: Int, expToNextLevel: Int): Int {
        return userDao.updateLevelAndExp(username, level, remainingExp, expToNextLevel)
    }
    
    /**
     * Updates a user's rank
     */
    suspend fun updateRank(username: String, rank: Rank): Int {
        return userDao.updateRank(username, rank)
    }
    
    /**
     * Marks a user as ready for rank up
     */
    suspend fun markReadyForRankUp(username: String): Int {
        return userDao.markReadyForRankUp(username)
    }
    
    /**
     * Marks a user's rank up quest as completed
     */
    suspend fun markRankUpQuestCompleted(username: String): Int {
        return userDao.markRankUpQuestCompleted(username)
    }
    
    /**
     * Resets a user's daily experience
     */
    suspend fun resetDailyExp(username: String): Int {
        return userDao.resetDailyExp(username)
    }
    
    /**
     * Marks a user as having worked out today
     */
    suspend fun markWorkoutCompleted(username: String): Int {
        return userDao.markWorkoutCompleted(username)
    }
    
    /**
     * Resets all users' daily workout flags
     */
    suspend fun resetDailyWorkoutFlags(): Int {
        return userDao.resetDailyWorkoutFlags()
    }
    
    /**
     * Gets the count of all users
     */
    suspend fun getUserCount(): Int {
        return userDao.getUserCount()
    }
    
    /**
     * Updates a user's password
     */
    suspend fun updatePassword(username: String, newPassword: String): Int {
        val passwordHash = hashPassword(newPassword)
        return userDao.updatePassword(username, passwordHash)
    }
    
    /**
     * Hashes a password for secure storage
     */
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
