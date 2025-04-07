package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): LiveData<User>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsernameSync(username: String): User?

    @Query("SELECT * FROM users ORDER BY exp DESC")
    fun getAllUsersByExpDesc(): LiveData<List<User>>

    @Query("UPDATE users SET rank = :rank, exp = :exp WHERE username = :username")
    suspend fun updateRankAndExp(username: String, rank: String, exp: Int)

    @Query("UPDATE users SET strength = :strength, agility = :agility, vitality = :vitality, " +
            "intelligence = :intelligence, luck = :luck WHERE username = :username")
    suspend fun updateStats(
        username: String, 
        strength: Int, 
        agility: Int, 
        vitality: Int, 
        intelligence: Int, 
        luck: Int
    )
}
