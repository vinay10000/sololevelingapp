package com.huntersascension.data.repository

import com.huntersascension.data.dao.UserDao
import com.huntersascension.data.model.User
import java.util.*

/**
 * Repository for user data
 */
class UserRepository(private val userDao: UserDao) {
    
    /**
     * Get a user by ID
     * @param userId The ID of the user
     * @return The user with the specified ID, or null if not found
     */
    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }
    
    /**
     * Get a user by username
     * @param username The username of the user
     * @return The user with the specified username, or null if not found
     */
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
    
    /**
     * Get a user by email
     * @param email The email of the user
     * @return The user with the specified email, or null if not found
     */
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
    
    /**
     * Insert a new user
     * @param user The user to insert
     * @return The ID of the inserted user
     */
    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }
    
    /**
     * Update an existing user
     * @param user The user to update
     */
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
    
    /**
     * Add XP to a user
     * @param userId The ID of the user
     * @param xpToAdd The amount of XP to add
     * @return True if the user leveled up, false otherwise
     */
    suspend fun addXp(userId: Long, xpToAdd: Int): Boolean {
        return userDao.addXpToUser(userId, xpToAdd)
    }
    
    /**
     * Increment a user's stat
     * @param userId The ID of the user
     * @param statName The name of the stat to increment (Strength, Endurance, Agility, Vitality)
     * @param amount The amount to increment the stat by
     */
    suspend fun incrementStat(userId: Long, statName: String, amount: Int) {
        userDao.incrementStat(userId, statName, amount)
    }
    
    /**
     * Update user's workout stats
     * @param userId The ID of the user
     * @param workoutsToAdd Number of workouts to add
     * @param caloriesToAdd Number of calories to add
     * @param minutesToAdd Number of minutes to add
     */
    suspend fun updateWorkoutStats(userId: Long, workoutsToAdd: Int, caloriesToAdd: Int, minutesToAdd: Int) {
        userDao.updateWorkoutStats(userId, workoutsToAdd, caloriesToAdd, minutesToAdd)
    }
    
    /**
     * Update user's last workout date
     * @param userId The ID of the user
     */
    suspend fun updateLastWorkoutDate(userId: Long) {
        userDao.updateLastWorkoutDate(userId)
    }
    
    /**
     * Update user's streak
     * @param userId The ID of the user
     * @param currentStreak The current streak
     */
    suspend fun updateStreak(userId: Long, currentStreak: Int) {
        userDao.updateStreak(userId, currentStreak)
    }
    
    /**
     * Update user's rank
     * @param userId The ID of the user
     * @param newRank The new rank
     */
    suspend fun updateRank(userId: Long, newRank: String) {
        userDao.updateRank(userId, newRank)
    }
}
