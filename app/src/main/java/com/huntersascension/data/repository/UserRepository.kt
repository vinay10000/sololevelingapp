package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.UserDao
import com.huntersascension.data.model.User

class UserRepository(private val userDao: UserDao) {
    
    fun getUserByUsername(username: String): LiveData<User> {
        return userDao.getUserByUsername(username)
    }
    
    suspend fun getUserByUsernameSync(username: String): User? {
        return userDao.getUserByUsernameSync(username)
    }
    
    fun getAllUsersByExpDesc(): LiveData<List<User>> {
        return userDao.getAllUsersByExpDesc()
    }
    
    suspend fun insert(user: User) {
        userDao.insert(user)
    }
    
    suspend fun update(user: User) {
        userDao.update(user)
    }
    
    suspend fun updateRankAndExp(username: String, rank: String, exp: Int) {
        userDao.updateRankAndExp(username, rank, exp)
    }
    
    suspend fun updateStats(
        username: String,
        strength: Int,
        agility: Int,
        vitality: Int,
        intelligence: Int,
        luck: Int
    ) {
        userDao.updateStats(username, strength, agility, vitality, intelligence, luck)
    }
}
