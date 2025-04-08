package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.User
import com.huntersascension.data.model.Rank
import com.huntersascension.data.model.Stat

/**
 * Data Access Object for User entity
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): LiveData<User?>
    
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsernameSync(username: String): User?
    
    @Insert
    suspend fun insertUser(user: User)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("SELECT * FROM users ORDER BY rank DESC, level DESC, exp DESC LIMIT :limit")
    fun getTopUsers(limit: Int = 10): LiveData<List<User>>
    
    @Query("SELECT * FROM users WHERE rank = :rank ORDER BY level DESC, exp DESC LIMIT :limit")
    fun getUsersByRank(rank: Rank, limit: Int = 10): LiveData<List<User>>
    
    @Query("UPDATE users SET exp = exp + :amount, remainingDailyExp = remainingDailyExp - :amount WHERE username = :username AND remainingDailyExp >= :amount")
    suspend fun addExp(username: String, amount: Int): Int
    
    @Query("UPDATE users SET strength = strength + :amount WHERE username = :username")
    suspend fun addStrength(username: String, amount: Int): Int
    
    @Query("UPDATE users SET endurance = endurance + :amount WHERE username = :username")
    suspend fun addEndurance(username: String, amount: Int): Int
    
    @Query("UPDATE users SET agility = agility + :amount WHERE username = :username")
    suspend fun addAgility(username: String, amount: Int): Int
    
    @Query("UPDATE users SET vitality = vitality + :amount WHERE username = :username")
    suspend fun addVitality(username: String, amount: Int): Int
    
    @Query("UPDATE users SET intelligence = intelligence + :amount WHERE username = :username")
    suspend fun addIntelligence(username: String, amount: Int): Int
    
    @Query("UPDATE users SET luck = luck + :amount WHERE username = :username")
    suspend fun addLuck(username: String, amount: Int): Int
    
    @Query("UPDATE users SET trophyPoints = trophyPoints + :amount WHERE username = :username")
    suspend fun addTrophyPoints(username: String, amount: Int): Int
    
    @Query("UPDATE users SET currentStreak = currentStreak + 1, bestStreak = CASE WHEN currentStreak + 1 > bestStreak THEN currentStreak + 1 ELSE bestStreak END WHERE username = :username")
    suspend fun incrementStreak(username: String): Int
    
    @Query("UPDATE users SET currentStreak = 0 WHERE username = :username")
    suspend fun resetStreak(username: String): Int
    
    @Query("UPDATE users SET totalWorkouts = totalWorkouts + 1 WHERE username = :username")
    suspend fun incrementWorkoutCount(username: String): Int
    
    @Query("UPDATE users SET totalWorkoutMinutes = totalWorkoutMinutes + :minutes WHERE username = :username")
    suspend fun addWorkoutMinutes(username: String, minutes: Int): Int
    
    @Query("UPDATE users SET totalCaloriesBurned = totalCaloriesBurned + :calories WHERE username = :username")
    suspend fun addCaloriesBurned(username: String, calories: Int): Int
    
    @Query("UPDATE users SET level = :level, exp = :remainingExp, expToNextLevel = :expToNextLevel WHERE username = :username")
    suspend fun updateLevelAndExp(username: String, level: Int, remainingExp: Int, expToNextLevel: Int): Int
    
    @Query("UPDATE users SET rank = :rank, canRankUp = 0, rankUpQuestCompleted = 0 WHERE username = :username")
    suspend fun updateRank(username: String, rank: Rank): Int
    
    @Query("UPDATE users SET canRankUp = 1 WHERE username = :username")
    suspend fun markReadyForRankUp(username: String): Int
    
    @Query("UPDATE users SET rankUpQuestCompleted = 1 WHERE username = :username")
    suspend fun markRankUpQuestCompleted(username: String): Int
    
    @Query("UPDATE users SET remainingDailyExp = getDailyExpCap() WHERE username = :username")
    suspend fun resetDailyExp(username: String): Int
    
    @Query("UPDATE users SET hasWorkedOutToday = 1 WHERE username = :username")
    suspend fun markWorkoutCompleted(username: String): Int
    
    @Query("UPDATE users SET hasWorkedOutToday = 0 WHERE 1=1")
    suspend fun resetDailyWorkoutFlags(): Int
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
    
    @Query("UPDATE users SET passwordHash = :newPasswordHash WHERE username = :username")
    suspend fun updatePassword(username: String, newPasswordHash: String): Int
    
    @Query("SELECT * FROM users WHERE rank = :rank ORDER BY CASE " +
           "WHEN :statType = 'STRENGTH' THEN strength " +
           "WHEN :statType = 'ENDURANCE' THEN endurance " +
           "WHEN :statType = 'AGILITY' THEN agility " +
           "WHEN :statType = 'VITALITY' THEN vitality " +
           "WHEN :statType = 'INTELLIGENCE' THEN intelligence " +
           "WHEN :statType = 'LUCK' THEN luck " +
           "ELSE totalWorkouts END DESC LIMIT :limit")
    fun getUsersByRankAndStat(rank: Rank, statType: String, limit: Int = 10): LiveData<List<User>>
}
