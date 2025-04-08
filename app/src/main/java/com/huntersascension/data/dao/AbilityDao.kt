package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Ability
import com.huntersascension.data.model.AbilityType
import com.huntersascension.data.model.Rank
import com.huntersascension.data.model.UserAbility

/**
 * Data Access Object for Ability and UserAbility entities
 */
@Dao
interface AbilityDao {
    
    // Ability methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAbility(ability: Ability): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAbilities(abilities: List<Ability>)
    
    @Update
    suspend fun updateAbility(ability: Ability)
    
    @Delete
    suspend fun deleteAbility(ability: Ability)
    
    @Query("SELECT * FROM abilities WHERE abilityId = :abilityId")
    fun getAbilityById(abilityId: String): LiveData<Ability?>
    
    @Query("SELECT * FROM abilities WHERE requiredRank = :rank")
    fun getAbilitiesForRank(rank: Rank): LiveData<List<Ability>>
    
    @Query("SELECT * FROM abilities WHERE requiredRank <= :rank")
    fun getAllAvailableAbilitiesForRank(rank: Rank): LiveData<List<Ability>>
    
    @Query("SELECT * FROM abilities WHERE type = :type")
    fun getAbilitiesByType(type: AbilityType): LiveData<List<Ability>>
    
    // UserAbility methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAbility(userAbility: UserAbility)
    
    @Update
    suspend fun updateUserAbility(userAbility: UserAbility)
    
    @Delete
    suspend fun deleteUserAbility(userAbility: UserAbility)
    
    @Query("SELECT * FROM user_abilities WHERE username = :username AND abilityId = :abilityId")
    fun getUserAbility(username: String, abilityId: String): LiveData<UserAbility?>
    
    @Query("SELECT * FROM user_abilities WHERE username = :username")
    fun getUserAbilities(username: String): LiveData<List<UserAbility>>
    
    @Query("SELECT a.* FROM abilities a JOIN user_abilities ua ON a.abilityId = ua.abilityId WHERE ua.username = :username")
    fun getUnlockedAbilitiesForUser(username: String): LiveData<List<Ability>>
    
    @Query("SELECT a.* FROM abilities a JOIN user_abilities ua ON a.abilityId = ua.abilityId WHERE ua.username = :username AND ua.isActive = 1")
    fun getActiveAbilitiesForUser(username: String): LiveData<List<Ability>>
    
    @Query("UPDATE user_abilities SET isActive = :isActive WHERE username = :username AND abilityId = :abilityId")
    suspend fun setAbilityActive(username: String, abilityId: String, isActive: Boolean)
    
    @Query("UPDATE user_abilities SET usageCount = usageCount + 1, lastUsedTimestamp = :timestamp WHERE username = :username AND abilityId = :abilityId")
    suspend fun incrementAbilityUsage(username: String, abilityId: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT EXISTS(SELECT 1 FROM user_abilities WHERE username = :username AND abilityId = :abilityId)")
    suspend fun hasAbility(username: String, abilityId: String): Boolean
    
    @Query("SELECT EXISTS(SELECT 1 FROM user_abilities WHERE username = :username AND abilityId = :abilityId AND isActive = 1)")
    suspend fun isAbilityActive(username: String, abilityId: String): Boolean
    
    @Query("SELECT COUNT(*) FROM user_abilities WHERE username = :username")
    fun getUnlockedAbilityCount(username: String): LiveData<Int>
    
    @Transaction
    suspend fun unlockAbilityForUser(username: String, abilityId: String, isActive: Boolean = false) {
        if (!hasAbility(username, abilityId)) {
            insertUserAbility(UserAbility(username, abilityId, isActive))
        }
    }
    
    @Transaction
    suspend fun unlockAbilitiesForRank(username: String, rank: Rank) {
        val abilities = getAbilitiesForRankSync(rank)
        for (ability in abilities) {
            unlockAbilityForUser(username, ability.abilityId, isActive = true)
        }
    }
    
    @Query("SELECT * FROM abilities WHERE requiredRank = :rank")
    suspend fun getAbilitiesForRankSync(rank: Rank): List<Ability>
}
