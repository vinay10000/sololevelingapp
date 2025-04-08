package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Achievement
import com.huntersascension.data.model.AchievementCategory
import com.huntersascension.data.model.AchievementTier
import java.util.Date

/**
 * Data Access Object for Achievement entity
 */
@Dao
interface AchievementDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)
    
    @Update
    suspend fun updateAchievement(achievement: Achievement)
    
    @Delete
    suspend fun deleteAchievement(achievement: Achievement)
    
    @Query("SELECT * FROM achievements WHERE achievementId = :achievementId")
    fun getAchievementById(achievementId: String): LiveData<Achievement?>
    
    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 AND username = :username ORDER BY unlockedDate DESC")
    fun getUnlockedAchievements(username: String): LiveData<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE isUnlocked = 0")
    fun getLockedAchievements(): LiveData<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE category = :category ORDER BY tier, isUnlocked DESC")
    fun getAchievementsByCategory(category: AchievementCategory): LiveData<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE tier = :tier ORDER BY category, isUnlocked DESC")
    fun getAchievementsByTier(tier: AchievementTier): LiveData<List<Achievement>>
    
    @Query("UPDATE achievements SET isUnlocked = 1, unlockedDate = :unlockDate, username = :username WHERE achievementId = :achievementId")
    suspend fun unlockAchievement(achievementId: String, username: String, unlockDate: Date = Date())
    
    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1 AND username = :username")
    fun getUnlockedAchievementCount(username: String): LiveData<Int>
    
    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1 AND username = :username AND tier = :tier")
    fun getUnlockedAchievementCountByTier(username: String, tier: AchievementTier): LiveData<Int>
    
    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 AND username = :username ORDER BY unlockedDate DESC LIMIT :limit")
    fun getRecentUnlockedAchievements(username: String, limit: Int): LiveData<List<Achievement>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM achievements WHERE achievementId = :achievementId AND isUnlocked = 1)")
    suspend fun isAchievementUnlocked(achievementId: String): Boolean
}
