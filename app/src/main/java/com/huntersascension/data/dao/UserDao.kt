package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Rank
import com.huntersascension.data.model.User
import java.util.*

/**
 * Data Access Object for User entity
 */
@Dao
interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): LiveData<User?>
    
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsernameSync(username: String): User?
    
    @Query("SELECT * FROM users WHERE username = :username AND passwordHash = :passwordHash")
    suspend fun loginUser(username: String, passwordHash: String): User?
    
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    suspend fun usernameExists(username: String): Boolean
    
    @Query("UPDATE users SET level = :level, exp = :exp, expToNextLevel = :expToNextLevel WHERE username = :username")
    suspend fun updateLevel(username: String, level: Int, exp: Int, expToNextLevel: Int)
    
    @Query("UPDATE users SET rank = :rank WHERE username = :username")
    suspend fun updateRank(username: String, rank: Rank)
    
    @Query("UPDATE users SET currentStreak = currentStreak + 1, bestStreak = CASE WHEN currentStreak + 1 > bestStreak THEN currentStreak + 1 ELSE bestStreak END, lastWorkoutDate = :date, hasWorkedOutToday = 1 WHERE username = :username")
    suspend fun incrementStreak(username: String, date: Date = Date())
    
    @Query("UPDATE users SET currentStreak = 0, lastWorkoutDate = NULL, hasWorkedOutToday = 0 WHERE username = :username")
    suspend fun resetStreak(username: String)
    
    @Query("UPDATE users SET strength = strength + :amount WHERE username = :username")
    suspend fun addStrength(username: String, amount: Int)
    
    @Query("UPDATE users SET endurance = endurance + :amount WHERE username = :username")
    suspend fun addEndurance(username: String, amount: Int)
    
    @Query("UPDATE users SET agility = agility + :amount WHERE username = :username")
    suspend fun addAgility(username: String, amount: Int)
    
    @Query("UPDATE users SET vitality = vitality + :amount WHERE username = :username")
    suspend fun addVitality(username: String, amount: Int)
    
    @Query("UPDATE users SET intelligence = intelligence + :amount WHERE username = :username")
    suspend fun addIntelligence(username: String, amount: Int)
    
    @Query("UPDATE users SET luck = luck + :amount WHERE username = :username")
    suspend fun addLuck(username: String, amount: Int)
    
    @Query("UPDATE users SET totalWorkouts = totalWorkouts + 1, totalWorkoutMinutes = totalWorkoutMinutes + :duration, totalCaloriesBurned = totalCaloriesBurned + :calories WHERE username = :username")
    suspend fun updateWorkoutStats(username: String, duration: Int, calories: Int)
    
    @Query("UPDATE users SET canRankUp = :canRankUp WHERE username = :username")
    suspend fun setCanRankUp(username: String, canRankUp: Boolean)
    
    @Query("UPDATE users SET rankUpQuestCompleted = :completed WHERE username = :username")
    suspend fun setRankUpQuestCompleted(username: String, completed: Boolean)
    
    @Query("UPDATE users SET trophyPoints = trophyPoints + :points WHERE username = :username")
    suspend fun addTrophyPoints(username: String, points: Int)
    
    @Query("UPDATE users SET achievementCount = achievementCount + 1 WHERE username = :username")
    suspend fun incrementAchievementCount(username: String)
    
    @Query("UPDATE users SET remainingDailyExp = :amount WHERE username = :username")
    suspend fun updateRemainingDailyExp(username: String, amount: Int)
    
    @Query("UPDATE users SET remainingDailyExp = CASE WHEN rank = 'E' THEN 500 WHEN rank = 'D' THEN 750 WHEN rank = 'C' THEN 1000 WHEN rank = 'B' THEN 1500 WHEN rank = 'A' THEN 2000 ELSE 3000 END, hasWorkedOutToday = 0 WHERE username = :username")
    suspend fun resetDailyExp(username: String)
    
    @Query("SELECT * FROM users ORDER BY level DESC, exp DESC LIMIT :limit")
    fun getTopUsers(limit: Int): LiveData<List<User>>
    
    @Query("SELECT * FROM users ORDER BY currentStreak DESC LIMIT :limit")
    fun getTopStreakUsers(limit: Int): LiveData<List<User>>
    
    @Query("SELECT * FROM users WHERE rank = :rank ORDER BY level DESC, exp DESC LIMIT :limit")
    fun getTopUsersByRank(rank: Rank, limit: Int): LiveData<List<User>>
}
