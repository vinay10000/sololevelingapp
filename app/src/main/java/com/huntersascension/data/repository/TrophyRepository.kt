package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.TrophyDao
import com.huntersascension.data.model.Trophy

class TrophyRepository(private val trophyDao: TrophyDao) {
    
    fun getAllTrophiesForUser(username: String): LiveData<List<Trophy>> {
        return trophyDao.getAllTrophiesForUser(username)
    }
    
    suspend fun insert(trophy: Trophy) {
        trophyDao.insert(trophy)
    }
    
    suspend fun getTrophyCount(username: String): Int {
        return trophyDao.getTrophyCount(username)
    }
    
    suspend fun getTrophyCountByRarity(username: String, rarity: String): Int {
        return trophyDao.getTrophyCountByRarity(username, rarity)
    }
    
    suspend fun hasTrophy(username: String, trophyId: String): Boolean {
        return trophyDao.hasTrophy(username, trophyId)
    }
}
