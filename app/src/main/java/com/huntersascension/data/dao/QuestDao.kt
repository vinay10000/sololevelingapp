package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Quest
import com.huntersascension.data.model.QuestType
import java.util.*

/**
 * Data Access Object for Quest entity
 */
@Dao
interface QuestDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: Quest): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<Quest>)
    
    @Update
    suspend fun updateQuest(quest: Quest)
    
    @Delete
    suspend fun deleteQuest(quest: Quest)
    
    @Query("SELECT * FROM quests WHERE questId = :questId")
    fun getQuestById(questId: String): LiveData<Quest?>
    
    @Query("SELECT * FROM quests ORDER BY isCompleted, expiryDate")
    fun getAllQuests(): LiveData<List<Quest>>
    
    @Query("SELECT * FROM quests WHERE isDaily = 1 ORDER BY isCompleted, title")
    fun getDailyQuests(): LiveData<List<Quest>>
    
    @Query("SELECT * FROM quests WHERE isDaily = 0 ORDER BY isCompleted, expiryDate")
    fun getNonDailyQuests(): LiveData<List<Quest>>
    
    @Query("SELECT * FROM quests WHERE isCompleted = 0 ORDER BY expiryDate, title")
    fun getIncompleteQuests(): LiveData<List<Quest>>
    
    @Query("SELECT * FROM quests WHERE isCompleted = 1 ORDER BY completedDate DESC")
    fun getCompletedQuests(): LiveData<List<Quest>>
    
    @Query("SELECT * FROM quests WHERE type = :type ORDER BY isCompleted, title")
    fun getQuestsByType(type: QuestType): LiveData<List<Quest>>
    
    @Query("UPDATE quests SET currentValue = MIN(currentValue + :increment, targetValue), isCompleted = (currentValue + :increment >= targetValue), isInProgress = (currentValue + :increment > 0 AND currentValue + :increment < targetValue), completedDate = CASE WHEN currentValue + :increment >= targetValue AND completedDate IS NULL THEN :now ELSE completedDate END WHERE type = :type AND isCompleted = 0")
    suspend fun updateAllQuestsOfType(type: QuestType, increment: Int, now: Date = Date())
    
    @Query("UPDATE quests SET currentValue = MIN(currentValue + :increment, targetValue), isCompleted = (currentValue + :increment >= targetValue), isInProgress = (currentValue + :increment > 0 AND currentValue + :increment < targetValue), completedDate = CASE WHEN currentValue + :increment >= targetValue AND completedDate IS NULL THEN :now ELSE completedDate END WHERE questId = :questId AND isCompleted = 0")
    suspend fun updateQuestProgress(questId: String, increment: Int, now: Date = Date())
    
    @Query("DELETE FROM quests WHERE isDaily = 1")
    suspend fun clearDailyQuests()
    
    @Query("DELETE FROM quests WHERE isExpired = 1")
    suspend fun clearExpiredQuests()
    
    @Query("SELECT EXISTS(SELECT 1 FROM quests WHERE type = :type AND isCompleted = 1)")
    suspend fun hasCompletedQuestOfType(type: QuestType): Boolean
    
    @Query("SELECT * FROM quests WHERE isCompleted = 1 AND completedDate > :since ORDER BY completedDate DESC")
    fun getRecentlyCompletedQuests(since: Date): LiveData<List<Quest>>
}
