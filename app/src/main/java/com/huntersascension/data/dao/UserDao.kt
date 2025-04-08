package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.User
import java.util.*

/**
 * Data Access Object for User entities
 */
@Dao
interface UserDao {
    /**
     * Get all users
     * @return LiveData list of all users
     */
    @Query("SELECT * FROM users ORDER BY username ASC")
    fun getAllUsers(): LiveData<List<User>>
    
    /**
     * Get a user by ID
     * @param userId The ID of the user
     * @return The user with the specified ID, or null if not found
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?
    
    /**
     * Get a user by username
     * @param username The username of the user
     * @return The user with the specified username, or null if not found
     */
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?
    
    /**
     * Get a user by email
     * @param email The email of the user
     * @return The user with the specified email, or null if not found
     */
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?
    
    /**
     * Insert a new user
     * @param user The user to insert
     * @return The ID of the inserted user
     */
    @Insert
    suspend fun insertUser(user: User): Long
    
    /**
     * Update an existing user
     * @param user The user to update
     */
    @Update
    suspend fun updateUser(user: User)
    
    /**
     * Delete a user
     * @param user The user to delete
     */
    @Delete
    suspend fun deleteUser(user: User)
    
    /**
     * Add XP to a user
     * @param userId The ID of the user
     * @param xpToAdd The amount of XP to add
     * @return True if the user leveled up, false otherwise
     */
    @Transaction
    suspend fun addXpToUser(userId: Long, xpToAdd: Int): Boolean {
        val user = getUserById(userId) ?: return false
        
        var newXp = user.xp + xpToAdd
        var newLevel = user.level
        var newXpToNextLevel = user.xpToNextLevel
        var leveledUp = false
        
        // Check if user leveled up
        while (newXp >= newXpToNextLevel) {
            newXp -= newXpToNextLevel
            newLevel++
            // Increase XP needed for next level by 20%
            newXpToNextLevel = (newXpToNextLevel * 1.2).toInt()
            leveledUp = true
        }
        
        // Update user
        val updatedUser = user.copy(
            xp = newXp,
            level = newLevel,
            xpToNextLevel = newXpToNextLevel
        )
        
        updateUser(updatedUser)
        
        return leveledUp
    }
    
    /**
     * Increment a user's stat
     * @param userId The ID of the user
     * @param statName The name of the stat to increment (Strength, Endurance, Agility, Vitality)
     * @param amount The amount to increment the stat by
     */
    @Transaction
    suspend fun incrementStat(userId: Long, statName: String, amount: Int) {
        val user = getUserById(userId) ?: return
        
        val updatedUser = when (statName.toLowerCase(Locale.ROOT)) {
            "strength" -> {
                user.copy(strengthStat = user.strengthStat + amount)
            }
            "endurance" -> {
                user.copy(enduranceStat = user.enduranceStat + amount)
            }
            "agility" -> {
                user.copy(agilityStat = user.agilityStat + amount)
            }
            "vitality" -> {
                user.copy(vitalityStat = user.vitalityStat + amount)
            }
            else -> user
        }
        
        updateUser(updatedUser)
    }
    
    /**
     * Update user's workout stats
     * @param userId The ID of the user
     * @param workoutsToAdd Number of workouts to add
     * @param caloriesToAdd Number of calories to add
     * @param minutesToAdd Number of minutes to add
     */
    @Transaction
    suspend fun updateWorkoutStats(userId: Long, workoutsToAdd: Int, caloriesToAdd: Int, minutesToAdd: Int) {
        val user = getUserById(userId) ?: return
        
        val updatedUser = user.copy(
            totalWorkouts = user.totalWorkouts + workoutsToAdd,
            totalCaloriesBurned = user.totalCaloriesBurned + caloriesToAdd,
            totalWorkoutMinutes = user.totalWorkoutMinutes + minutesToAdd
        )
        
        updateUser(updatedUser)
    }
    
    /**
     * Update user's last workout date
     * @param userId The ID of the user
     */
    @Query("UPDATE users SET lastWorkoutDate = :date WHERE id = :userId")
    suspend fun updateLastWorkoutDate(userId: Long, date: Date = Date())
    
    /**
     * Update user's streak
     * @param userId The ID of the user
     * @param currentStreak The current streak
     */
    @Transaction
    suspend fun updateStreak(userId: Long, currentStreak: Int) {
        val user = getUserById(userId) ?: return
        
        // Update best streak if current streak is better
        val bestStreak = if (currentStreak > user.bestStreak) currentStreak else user.bestStreak
        
        val updatedUser = user.copy(
            currentStreak = currentStreak,
            bestStreak = bestStreak
        )
        
        updateUser(updatedUser)
    }
    
    /**
     * Update user's rank
     * @param userId The ID of the user
     * @param newRank The new rank
     */
    @Query("UPDATE users SET rank = :newRank WHERE id = :userId")
    suspend fun updateRank(userId: Long, newRank: String)
}
