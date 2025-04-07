package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Trophy

@Dao
interface TrophyDao {
    @Insert
    suspend fun insert(trophy: Trophy): Long
    
    @Update
    suspend fun update(trophy: Trophy)
    
    @Delete
    suspend fun delete(trophy: Trophy)
    
    @Query("SELECT * FROM trophies WHERE id = :id")
    fun getTrophyById(id: Long): LiveData<Trophy>
    
    @Query("SELECT * FROM trophies WHERE userEmail = :userEmail ORDER BY dateEarned DESC")
    fun getTrophiesByUser(userEmail: String): LiveData<List<Trophy>>
    
    @Query("SELECT COUNT(*) FROM trophies WHERE userEmail = :userEmail")
    suspend fun getTrophyCount(userEmail: String): Int
    
    @Query("SELECT SUM(experienceRewarded) FROM trophies WHERE userEmail = :userEmail")
    suspend fun getTotalExperienceRewarded(userEmail: String): Int
    
    @Query("SELECT * FROM trophies WHERE userEmail = :userEmail AND name = :trophyName")
    suspend fun checkIfTrophyExists(userEmail: String, trophyName: String): Trophy?
}
