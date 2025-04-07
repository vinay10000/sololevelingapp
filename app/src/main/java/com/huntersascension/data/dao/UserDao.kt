package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.User
import java.util.Date

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)
    
    @Update
    suspend fun update(user: User)
    
    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmail(email: String): LiveData<User>
    
    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :passwordHash")
    suspend fun login(email: String, passwordHash: String): User?
    
    @Query("SELECT * FROM users WHERE username LIKE :searchQuery")
    fun searchUsersByUsername(searchQuery: String): LiveData<List<User>>
    
    @Query("UPDATE users SET level = :level, experience = :experience WHERE email = :email")
    suspend fun updateLevelAndExp(email: String, level: Int, experience: Int)
    
    @Query("UPDATE users SET rank = :rank WHERE email = :email")
    suspend fun updateRank(email: String, rank: String)
    
    @Query("UPDATE users SET strength = :value WHERE email = :email")
    suspend fun updateStrength(email: String, value: Int)
    
    @Query("UPDATE users SET agility = :value WHERE email = :email")
    suspend fun updateAgility(email: String, value: Int)
    
    @Query("UPDATE users SET vitality = :value WHERE email = :email")
    suspend fun updateVitality(email: String, value: Int)
    
    @Query("UPDATE users SET intelligence = :value WHERE email = :email")
    suspend fun updateIntelligence(email: String, value: Int)
    
    @Query("UPDATE users SET luck = :value WHERE email = :email")
    suspend fun updateLuck(email: String, value: Int)
    
    @Query("UPDATE users SET totalWorkouts = totalWorkouts + 1 WHERE email = :email")
    suspend fun incrementWorkouts(email: String)
    
    @Query("UPDATE users SET lastWorkoutDate = :date WHERE email = :email")
    suspend fun updateLastWorkoutDate(email: String, date: Date)
    
    @Query("UPDATE users SET consecutiveStreak = :streak WHERE email = :email")
    suspend fun updateConsecutiveStreak(email: String, streak: Int)
    
    @Query("UPDATE users SET maxStreak = :maxStreak WHERE email = :email")
    suspend fun updateMaxStreak(email: String, maxStreak: Int)
    
    @Query("UPDATE users SET consecutiveStreak = consecutiveStreak + 1 WHERE email = :email")
    suspend fun incrementStreak(email: String)
    
    @Query("UPDATE users SET maxStreak = CASE WHEN consecutiveStreak > maxStreak THEN consecutiveStreak ELSE maxStreak END WHERE email = :email")
    suspend fun updateMaxStreakIfNeeded(email: String)
    
    @Query("SELECT consecutiveStreak FROM users WHERE email = :email")
    suspend fun getCurrentStreak(email: String): Int
    
    @Query("SELECT lastWorkoutDate FROM users WHERE email = :email")
    suspend fun getLastWorkoutDate(email: String): Date?
}
