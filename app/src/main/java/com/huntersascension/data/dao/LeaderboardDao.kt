package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.LeaderboardEntry
import java.util.Date

@Dao
interface LeaderboardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: LeaderboardEntry)
    
    @Update
    suspend fun update(entry: LeaderboardEntry)
    
    @Query("DELETE FROM leaderboard_entries WHERE userEmail = :userEmail AND leaderboardCategory = :category")
    suspend fun deleteUserEntryForCategory(userEmail: String, category: String)
    
    @Query("SELECT * FROM leaderboard_entries WHERE leaderboardCategory = :category AND region = :region ORDER BY experience DESC LIMIT :limit")
    fun getLeaderboardByExperience(category: String, region: String = "global", limit: Int = 100): LiveData<List<LeaderboardEntry>>
    
    @Query("SELECT * FROM leaderboard_entries WHERE leaderboardCategory = :category AND region = :region ORDER BY level DESC, experience DESC LIMIT :limit")
    fun getLeaderboardByLevel(category: String, region: String = "global", limit: Int = 100): LiveData<List<LeaderboardEntry>>
    
    @Query("SELECT * FROM leaderboard_entries WHERE leaderboardCategory = :category AND region = :region ORDER BY streak DESC LIMIT :limit")
    fun getLeaderboardByStreak(category: String, region: String = "global", limit: Int = 100): LiveData<List<LeaderboardEntry>>
    
    @Query("SELECT * FROM leaderboard_entries WHERE leaderboardCategory = :category AND region = :region ORDER BY strength DESC LIMIT :limit")
    fun getLeaderboardByStrength(category: String, region: String = "global", limit: Int = 100): LiveData<List<LeaderboardEntry>>
    
    @Query("SELECT * FROM leaderboard_entries WHERE leaderboardCategory = :category AND region = :region ORDER BY agility DESC LIMIT :limit")
    fun getLeaderboardByAgility(category: String, region: String = "global", limit: Int = 100): LiveData<List<LeaderboardEntry>>
    
    @Query("SELECT * FROM leaderboard_entries WHERE leaderboardCategory = :category AND region = :region ORDER BY vitality DESC LIMIT :limit")
    fun getLeaderboardByVitality(category: String, region: String = "global", limit: Int = 100): LiveData<List<LeaderboardEntry>>
    
    @Query("SELECT * FROM leaderboard_entries WHERE leaderboardCategory = :category AND region = :region ORDER BY totalWorkouts DESC LIMIT :limit")
    fun getLeaderboardByTotalWorkouts(category: String, region: String = "global", limit: Int = 100): LiveData<List<LeaderboardEntry>>
    
    @Query("UPDATE leaderboard_entries SET lastUpdated = :updateTime WHERE userEmail = :userEmail")
    suspend fun updateLastUpdated(userEmail: String, updateTime: Date)
    
    @Query("SELECT * FROM leaderboard_entries WHERE userEmail = :userEmail AND leaderboardCategory = :category")
    fun getUserLeaderboardEntry(userEmail: String, category: String): LiveData<LeaderboardEntry>
    
    @Query("SELECT COUNT(*) + 1 FROM leaderboard_entries WHERE experience > (SELECT experience FROM leaderboard_entries WHERE userEmail = :userEmail AND leaderboardCategory = :category) AND leaderboardCategory = :category AND region = :region")
    suspend fun getUserRank(userEmail: String, category: String, region: String = "global"): Int
}
