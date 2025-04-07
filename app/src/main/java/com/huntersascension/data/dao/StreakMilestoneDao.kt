package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.StreakMilestone
import java.util.Date

@Dao
interface StreakMilestoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(milestone: StreakMilestone)
    
    @Update
    suspend fun update(milestone: StreakMilestone)
    
    @Query("SELECT * FROM streak_milestones WHERE userEmail = :userEmail ORDER BY achievedDate DESC")
    fun getAllUserMilestones(userEmail: String): LiveData<List<StreakMilestone>>
    
    @Query("SELECT * FROM streak_milestones WHERE userEmail = :userEmail AND claimed = 0 ORDER BY achievedDate DESC")
    fun getUnclaimedMilestones(userEmail: String): LiveData<List<StreakMilestone>>
    
    @Query("SELECT COUNT(*) FROM streak_milestones WHERE userEmail = :userEmail AND milestoneName = :milestoneName")
    suspend fun hasMilestone(userEmail: String, milestoneName: String): Int
    
    @Query("UPDATE streak_milestones SET claimed = 1 WHERE id = :milestoneId")
    suspend fun markMilestoneClaimed(milestoneId: Long)
    
    @Query("SELECT * FROM streak_milestones WHERE userEmail = :userEmail AND streakCount <= :currentStreak AND milestoneName NOT IN (SELECT milestoneName FROM streak_milestones WHERE userEmail = :userEmail)")
    suspend fun getNewlyAchievedMilestones(userEmail: String, currentStreak: Int): List<StreakMilestone>
    
    @Query("SELECT MAX(streakCount) FROM streak_milestones WHERE userEmail = :userEmail")
    suspend fun getHighestAchievedStreak(userEmail: String): Int?
}
