package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.LeaderboardDao
import com.huntersascension.data.model.LeaderboardEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class LeaderboardRepository(private val leaderboardDao: LeaderboardDao) {
    
    suspend fun insertLeaderboardEntry(entry: LeaderboardEntry) {
        withContext(Dispatchers.IO) {
            leaderboardDao.insert(entry)
        }
    }
    
    suspend fun updateLeaderboardEntry(entry: LeaderboardEntry) {
        withContext(Dispatchers.IO) {
            leaderboardDao.update(entry)
        }
    }
    
    suspend fun deleteUserEntryForCategory(userEmail: String, category: String) {
        withContext(Dispatchers.IO) {
            leaderboardDao.deleteUserEntryForCategory(userEmail, category)
        }
    }
    
    fun getLeaderboardByExperience(category: String, region: String = "global", limit: Int = 100): LiveData<List<LeaderboardEntry>> {
        return leaderboardDao.getLeaderboardByExperience(category, region, limit)
    }
    
    fun getLeaderboardByLevel(category: String, region: String = "global", limit: Int = 100): LiveData<List<LeaderboardEntry>> {
        return leaderboardDao.getLeaderboardByLevel(category, region, limit)
    }
    
    fun getLeaderboardByStreak(category: String, region: String = "global", limit: Int = 100): LiveData<List<LeaderboardEntry>> {
        return leaderboardDao.getLeaderboardByStreak(category, region, limit)
    }
    
    fun getLeaderboardByAttribute(attribute: String, category: String, region: String = "global", limit: Int = 100): LiveData<List<LeaderboardEntry>> {
        return when (attribute.lowercase()) {
            "strength" -> leaderboardDao.getLeaderboardByStrength(category, region, limit)
            "agility" -> leaderboardDao.getLeaderboardByAgility(category, region, limit)
            "vitality" -> leaderboardDao.getLeaderboardByVitality(category, region, limit)
            "totalworkouts" -> leaderboardDao.getLeaderboardByTotalWorkouts(category, region, limit)
            "streak" -> leaderboardDao.getLeaderboardByStreak(category, region, limit)
            "level" -> leaderboardDao.getLeaderboardByLevel(category, region, limit)
            else -> leaderboardDao.getLeaderboardByExperience(category, region, limit)
        }
    }
    
    suspend fun updateLastUpdated(userEmail: String) {
        withContext(Dispatchers.IO) {
            leaderboardDao.updateLastUpdated(userEmail, Date())
        }
    }
    
    fun getUserLeaderboardEntry(userEmail: String, category: String): LiveData<LeaderboardEntry> {
        return leaderboardDao.getUserLeaderboardEntry(userEmail, category)
    }
    
    suspend fun getUserRank(userEmail: String, category: String, region: String = "global"): Int {
        return withContext(Dispatchers.IO) {
            leaderboardDao.getUserRank(userEmail, category, region)
        }
    }
}
