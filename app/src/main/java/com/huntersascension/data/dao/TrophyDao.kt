package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Trophy

@Dao
interface TrophyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trophy: Trophy)

    @Query("SELECT * FROM trophies WHERE username = :username ORDER BY acquiredDate DESC")
    fun getAllTrophiesForUser(username: String): LiveData<List<Trophy>>

    @Query("SELECT COUNT(*) FROM trophies WHERE username = :username")
    suspend fun getTrophyCount(username: String): Int

    @Query("SELECT COUNT(*) FROM trophies WHERE username = :username AND rarity = :rarity")
    suspend fun getTrophyCountByRarity(username: String, rarity: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM trophies WHERE username = :username AND id = :trophyId)")
    suspend fun hasTrophy(username: String, trophyId: String): Boolean
}
