package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.TrophyDao
import com.huntersascension.data.model.Trophy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class TrophyRepository(private val trophyDao: TrophyDao) {
    
    fun getTrophyById(id: Long): LiveData<Trophy> {
        return trophyDao.getTrophyById(id)
    }
    
    fun getTrophiesByUser(userEmail: String): LiveData<List<Trophy>> {
        return trophyDao.getTrophiesByUser(userEmail)
    }
    
    suspend fun awardTrophy(
        userEmail: String,
        name: String,
        description: String,
        experienceRewarded: Int,
        imagePath: String? = null
    ): Long? {
        return withContext(Dispatchers.IO) {
            // Check if trophy already exists
            val existingTrophy = trophyDao.checkIfTrophyExists(userEmail, name)
            if (existingTrophy != null) {
                return@withContext null
            }
            
            val trophy = Trophy(
                userEmail = userEmail,
                name = name,
                description = description,
                dateEarned = Date(),
                experienceRewarded = experienceRewarded,
                imagePath = imagePath
            )
            trophyDao.insert(trophy)
        }
    }
    
    suspend fun getTrophyCount(userEmail: String): Int {
        return withContext(Dispatchers.IO) {
            trophyDao.getTrophyCount(userEmail)
        }
    }
    
    suspend fun getTotalExperienceRewarded(userEmail: String): Int {
        return withContext(Dispatchers.IO) {
            trophyDao.getTotalExperienceRewarded(userEmail)
        }
    }
}
