package com.huntersascension.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.huntersascension.utils.DateConverter
import java.util.Date

/**
 * User entity representing a Hunter in the app
 */
@Entity(tableName = "users")
@TypeConverters(DateConverter::class)
data class User(
    @PrimaryKey(autoGenerate = false)
    val username: String,
    
    val passwordHash: String,
    
    // Rank (E, D, C, B, A, S)
    val rank: String = "E",
    
    // Experience points
    val exp: Int = 0,
    
    // Stats
    val strStat: Int = 10,
    val agiStat: Int = 10,
    val vitStat: Int = 10,
    val intStat: Int = 10,
    val lukStat: Int = 10,
    
    // Streak
    val streak: Int = 0,
    val lastWorkoutDate: Date? = null,
    
    // Unlocked abilities (comma-separated list)
    val unlockedAbilities: String = "",
    
    // Equipped cosmetics (comma-separated list)
    val equippedCosmetics: String = "",
    
    // Creation date
    val createdAt: Date = Date()
) {
    /**
     * Get the display name for the current rank
     */
    fun getRankDisplayName(): String {
        return when (rank) {
            "E" -> "E-Rank Hunter"
            "D" -> "D-Rank Hunter"
            "C" -> "C-Rank Hunter"
            "B" -> "B-Rank Hunter"
            "A" -> "A-Rank Hunter"
            "S" -> "S-Rank Hunter"
            else -> "E-Rank Hunter"
        }
    }
    
    /**
     * Get the next rank
     */
    fun getNextRank(): String? {
        return when (rank) {
            "E" -> "D"
            "D" -> "C"
            "C" -> "B"
            "B" -> "A"
            "A" -> "S"
            else -> null
        }
    }
    
    /**
     * Get the next rank display name
     */
    fun getNextRankDisplayName(): String? {
        return when (rank) {
            "E" -> "D-Rank Hunter"
            "D" -> "C-Rank Hunter"
            "C" -> "B-Rank Hunter"
            "B" -> "A-Rank Hunter"
            "A" -> "S-Rank Hunter"
            else -> null
        }
    }
    
    /**
     * Get all the user stats as a map
     */
    fun getStats(): Map<String, Int> {
        return mapOf(
            "STR" to strStat,
            "AGI" to agiStat,
            "VIT" to vitStat,
            "INT" to intStat,
            "LUK" to lukStat
        )
    }
}
