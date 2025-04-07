package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.StreakMilestoneDao
import com.huntersascension.data.model.StreakMilestone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StreakMilestoneRepository(private val streakMilestoneDao: StreakMilestoneDao) {
    
    fun getAllUserMilestones(userEmail: String): LiveData<List<StreakMilestone>> {
        return streakMilestoneDao.getAllUserMilestones(userEmail)
    }
    
    fun getUnclaimedMilestones(userEmail: String): LiveData<List<StreakMilestone>> {
        return streakMilestoneDao.getUnclaimedMilestones(userEmail)
    }
    
    suspend fun insertMilestone(milestone: StreakMilestone) {
        withContext(Dispatchers.IO) {
            streakMilestoneDao.insert(milestone)
        }
    }
    
    suspend fun updateMilestone(milestone: StreakMilestone) {
        withContext(Dispatchers.IO) {
            streakMilestoneDao.update(milestone)
        }
    }
    
    suspend fun hasMilestone(userEmail: String, milestoneName: String): Boolean {
        return withContext(Dispatchers.IO) {
            streakMilestoneDao.hasMilestone(userEmail, milestoneName) > 0
        }
    }
    
    suspend fun markMilestoneClaimed(milestoneId: Long) {
        withContext(Dispatchers.IO) {
            streakMilestoneDao.markMilestoneClaimed(milestoneId)
        }
    }
    
    suspend fun getNewlyAchievedMilestones(userEmail: String, currentStreak: Int): List<StreakMilestone> {
        return withContext(Dispatchers.IO) {
            streakMilestoneDao.getNewlyAchievedMilestones(userEmail, currentStreak)
        }
    }
    
    suspend fun getHighestAchievedStreak(userEmail: String): Int {
        return withContext(Dispatchers.IO) {
            streakMilestoneDao.getHighestAchievedStreak(userEmail) ?: 0
        }
    }
    
    suspend fun checkAndCreateMilestone(userEmail: String, currentStreak: Int) {
        withContext(Dispatchers.IO) {
            when (currentStreak) {
                3 -> createMilestoneIfNotExists(userEmail, "3-Day Streak", currentStreak, "stat_bonus", "{'stat': 'vitality', 'value': 1}")
                7 -> createMilestoneIfNotExists(userEmail, "7-Day Streak", currentStreak, "trophy", "{'name': 'Consistent Hunter'}")
                14 -> createMilestoneIfNotExists(userEmail, "14-Day Streak", currentStreak, "stat_bonus", "{'stat': 'strength', 'value': 2}")
                30 -> createMilestoneIfNotExists(userEmail, "30-Day Streak", currentStreak, "trophy", "{'name': 'Dedicated Hunter'}")
                60 -> createMilestoneIfNotExists(userEmail, "60-Day Streak", currentStreak, "stat_bonus", "{'stat': 'all', 'value': 1}")
                90 -> createMilestoneIfNotExists(userEmail, "90-Day Streak", currentStreak, "trophy", "{'name': 'Elite Hunter'}")
                180 -> createMilestoneIfNotExists(userEmail, "180-Day Streak", currentStreak, "trophy", "{'name': 'Shadow Monarch'}")
                365 -> createMilestoneIfNotExists(userEmail, "365-Day Streak", currentStreak, "trophy", "{'name': 'Sovereign'}")
            }
        }
    }
    
    private suspend fun createMilestoneIfNotExists(userEmail: String, milestoneName: String, streakCount: Int, rewardType: String, rewardValue: String) {
        if (streakMilestoneDao.hasMilestone(userEmail, milestoneName) == 0) {
            val milestone = StreakMilestone(
                userEmail = userEmail,
                milestoneName = milestoneName,
                streakCount = streakCount,
                achievedDate = java.util.Date(),
                rewardType = rewardType,
                rewardValue = rewardValue,
                claimed = false
            )
            streakMilestoneDao.insert(milestone)
        }
    }
}
