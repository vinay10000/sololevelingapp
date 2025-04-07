package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.StreakMilestoneDao
import com.huntersascension.data.dao.UserDao
import com.huntersascension.data.model.StreakMilestone
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StreakRepository(
    private val streakMilestoneDao: StreakMilestoneDao,
    private val userDao: UserDao
) {
    
    fun getAllUserMilestones(userEmail: String): LiveData<List<StreakMilestone>> {
        return streakMilestoneDao.getAllUserMilestones(userEmail)
    }
    
    fun getUnclaimedMilestones(userEmail: String): LiveData<List<StreakMilestone>> {
        return streakMilestoneDao.getUnclaimedMilestones(userEmail)
    }
    
    suspend fun hasMilestone(userEmail: String, milestoneName: String): Boolean {
        return withContext(Dispatchers.IO) {
            streakMilestoneDao.hasMilestone(userEmail, milestoneName) > 0
        }
    }
    
    suspend fun claimMilestone(milestoneId: Long) {
        withContext(Dispatchers.IO) {
            streakMilestoneDao.markMilestoneClaimed(milestoneId)
        }
    }
    
    suspend fun checkAndCreateMilestones(userEmail: String) {
        withContext(Dispatchers.IO) {
            val currentStreak = userDao.getCurrentStreak(userEmail)
            
            // Define milestone thresholds and rewards
            val milestones = mapOf(
                "7-Day Streak" to MilestoneInfo(7, "trophy", "streak_warrior"),
                "30-Day Streak" to MilestoneInfo(30, "stat_bonus", "{\"vitality\": 5, \"strength\": 3}"),
                "60-Day Streak" to MilestoneInfo(60, "trophy", "streak_master"),
                "100-Day Streak" to MilestoneInfo(100, "cosmetic", "golden_aura"),
                "365-Day Streak" to MilestoneInfo(365, "trophy", "yearly_dedication")
            )
            
            // Check each milestone
            for ((name, info) in milestones) {
                if (currentStreak >= info.threshold && !hasMilestone(userEmail, name)) {
                    // Create new milestone
                    val milestone = StreakMilestone(
                        userEmail = userEmail,
                        milestoneName = name,
                        streakCount = info.threshold,
                        achievedDate = Date(),
                        rewardType = info.rewardType,
                        rewardValue = info.rewardValue,
                        claimed = false
                    )
                    streakMilestoneDao.insert(milestone)
                }
            }
        }
    }
    
    suspend fun getHighestAchievedStreak(userEmail: String): Int {
        return withContext(Dispatchers.IO) {
            streakMilestoneDao.getHighestAchievedStreak(userEmail) ?: 0
        }
    }
    
    // Data class to hold milestone information
    private data class MilestoneInfo(
        val threshold: Int,
        val rewardType: String,
        val rewardValue: String
    )
}
