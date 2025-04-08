package com.huntersascension.data.dao.social

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Rank
import com.huntersascension.data.model.Stat
import com.huntersascension.data.model.social.Leaderboard
import com.huntersascension.data.model.social.LeaderboardEntry
import com.huntersascension.data.model.social.LeaderboardScope
import com.huntersascension.data.model.social.LeaderboardType

/**
 * Data Access Object for Leaderboard entities
 */
@Dao
interface LeaderboardDao {
    @Transaction
    @Query("SELECT * FROM leaderboards WHERE id = :leaderboardId")
    fun getLeaderboardById(leaderboardId: Long): LiveData<Leaderboard?>
    
    @Transaction
    @Query("SELECT * FROM leaderboards WHERE type = :type AND scope = :scope")
    fun getLeaderboard(type: LeaderboardType, scope: LeaderboardScope = LeaderboardScope.GLOBAL): LiveData<Leaderboard?>
    
    @Transaction
    @Query("SELECT * FROM leaderboards WHERE type = :type AND specificRank = :rank")
    fun getRankSpecificLeaderboard(type: LeaderboardType, rank: Rank): LiveData<Leaderboard?>
    
    @Transaction
    @Query("SELECT * FROM leaderboard_entries WHERE leaderboardId = :leaderboardId ORDER BY rank ASC LIMIT :limit")
    fun getLeaderboardEntries(leaderboardId: Long, limit: Int = 100): LiveData<List<LeaderboardEntry>>
    
    @Transaction
    @Query("SELECT * FROM leaderboard_entries WHERE leaderboardId = :leaderboardId AND username = :username")
    fun getUserEntry(leaderboardId: Long, username: String): LiveData<LeaderboardEntry?>
    
    @Transaction
    @Query("SELECT * FROM leaderboard_entries WHERE leaderboardId = :leaderboardId AND rank <= :rankThreshold ORDER BY rank ASC")
    fun getTopLeaderboardEntries(leaderboardId: Long, rankThreshold: Int = 10): LiveData<List<LeaderboardEntry>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboard(leaderboard: Leaderboard): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: LeaderboardEntry)
    
    @Update
    suspend fun updateLeaderboard(leaderboard: Leaderboard)
    
    @Update
    suspend fun updateEntry(entry: LeaderboardEntry)
    
    @Delete
    suspend fun deleteLeaderboard(leaderboard: Leaderboard)
    
    @Delete
    suspend fun deleteEntry(entry: LeaderboardEntry)
    
    @Transaction
    @Query("DELETE FROM leaderboard_entries WHERE leaderboardId = :leaderboardId")
    suspend fun clearLeaderboard(leaderboardId: Long)
    
    @Transaction
    suspend fun createOrUpdateTotalExpLeaderboard(): Long {
        val existingLeaderboard = getLeaderboardByTypeSync(LeaderboardType.TOTAL_EXPERIENCE)
        return if (existingLeaderboard != null) {
            updateLeaderboard(existingLeaderboard.copy(lastUpdated = java.util.Date()))
            existingLeaderboard.id
        } else {
            insertLeaderboard(
                Leaderboard(
                    type = LeaderboardType.TOTAL_EXPERIENCE,
                    scope = LeaderboardScope.GLOBAL,
                    name = "Total Experience",
                    description = "Leaderboard ranked by total accumulated experience points"
                )
            )
        }
    }
    
    @Transaction
    suspend fun createOrUpdateWeeklyExpLeaderboard(): Long {
        val existingLeaderboard = getLeaderboardByTypeSync(LeaderboardType.WEEKLY_EXPERIENCE)
        return if (existingLeaderboard != null) {
            updateLeaderboard(existingLeaderboard.copy(lastUpdated = java.util.Date()))
            existingLeaderboard.id
        } else {
            insertLeaderboard(
                Leaderboard(
                    type = LeaderboardType.WEEKLY_EXPERIENCE,
                    scope = LeaderboardScope.GLOBAL,
                    name = "Weekly Experience",
                    description = "Leaderboard ranked by experience points earned this week",
                    resetFrequency = 7
                )
            )
        }
    }
    
    @Transaction
    suspend fun createOrUpdateStreakLeaderboard(): Long {
        val existingLeaderboard = getLeaderboardByTypeSync(LeaderboardType.STREAK)
        return if (existingLeaderboard != null) {
            updateLeaderboard(existingLeaderboard.copy(lastUpdated = java.util.Date()))
            existingLeaderboard.id
        } else {
            insertLeaderboard(
                Leaderboard(
                    type = LeaderboardType.STREAK,
                    scope = LeaderboardScope.GLOBAL,
                    name = "Workout Streak",
                    description = "Leaderboard ranked by consecutive workout days"
                )
            )
        }
    }
    
    @Transaction
    suspend fun createOrUpdateStatLeaderboard(statType: LeaderboardType): Long {
        val existingLeaderboard = getLeaderboardByTypeSync(statType)
        return if (existingLeaderboard != null) {
            updateLeaderboard(existingLeaderboard.copy(lastUpdated = java.util.Date()))
            existingLeaderboard.id
        } else {
            val statName = when (statType) {
                LeaderboardType.STRENGTH -> "Strength"
                LeaderboardType.ENDURANCE -> "Endurance"
                LeaderboardType.AGILITY -> "Agility"
                LeaderboardType.VITALITY -> "Vitality"
                LeaderboardType.INTELLIGENCE -> "Intelligence"
                LeaderboardType.LUCK -> "Luck"
                else -> throw IllegalArgumentException("Not a stat-based leaderboard type")
            }
            
            insertLeaderboard(
                Leaderboard(
                    type = statType,
                    scope = LeaderboardScope.GLOBAL,
                    name = "$statName Masters",
                    description = "Leaderboard ranked by $statName stat points",
                    statName = statName
                )
            )
        }
    }
    
    @Transaction
    suspend fun createOrUpdateRankSpecificLeaderboard(rank: Rank): Long {
        val existingLeaderboard = getRankSpecificLeaderboardSync(LeaderboardType.RANK_SPECIFIC, rank)
        return if (existingLeaderboard != null) {
            updateLeaderboard(existingLeaderboard.copy(lastUpdated = java.util.Date()))
            existingLeaderboard.id
        } else {
            insertLeaderboard(
                Leaderboard(
                    type = LeaderboardType.RANK_SPECIFIC,
                    scope = LeaderboardScope.GLOBAL,
                    name = "${rank.name}-Rank Hunters",
                    description = "Leaderboard of all ${rank.name}-Rank hunters ranked by experience",
                    specificRank = rank
                )
            )
        }
    }
    
    @Query("SELECT * FROM leaderboards WHERE type = :type AND scope = :scope")
    suspend fun getLeaderboardByTypeSync(
        type: LeaderboardType, 
        scope: LeaderboardScope = LeaderboardScope.GLOBAL
    ): Leaderboard?
    
    @Query("SELECT * FROM leaderboards WHERE type = :type AND specificRank = :rank")
    suspend fun getRankSpecificLeaderboardSync(type: LeaderboardType, rank: Rank): Leaderboard?
}
