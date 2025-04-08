package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Enum representing user ranks from E (lowest) to S (highest)
 */
enum class Rank {
    E, D, C, B, A, S;
    
    /**
     * Returns the next rank or null if already at max rank
     */
    fun next(): Rank? {
        return when (this) {
            E -> D
            D -> C
            C -> B
            B -> A
            A -> S
            S -> null // Already at max rank
        }
    }
    
    /**
     * Returns the previous rank or null if already at min rank
     */
    fun previous(): Rank? {
        return when (this) {
            E -> null // Already at min rank
            D -> E
            C -> D
            B -> C
            A -> B
            S -> A
        }
    }
}

/**
 * Enum representing the user's stats
 */
enum class Stat {
    STRENGTH, ENDURANCE, AGILITY, VITALITY, INTELLIGENCE, LUCK;
    
    override fun toString(): String {
        return when (this) {
            STRENGTH -> "STR"
            ENDURANCE -> "END"
            AGILITY -> "AGI"
            VITALITY -> "VIT"
            INTELLIGENCE -> "INT"
            LUCK -> "LUK"
        }
    }
}

/**
 * User entity representing a player in the game
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val username: String,
    
    // Personal info
    val hunterName: String,
    val passwordHash: String,
    val email: String? = null,
    val avatarPath: String? = null,
    val createdDate: Date = Date(),
    
    // Rank and level
    val rank: Rank = Rank.E,
    val level: Int = 1,
    val exp: Int = 0,
    val expToNextLevel: Int = 100,
    val remainingDailyExp: Int = 500,
    
    // Stats
    val strength: Int = 10,
    val endurance: Int = 10,
    val agility: Int = 10,
    val vitality: Int = 10,
    val intelligence: Int = 10,
    val luck: Int = 5,
    
    // Streak tracking
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastWorkoutDate: Date? = null,
    val hasWorkedOutToday: Boolean = false,
    
    // Progression flags
    val canRankUp: Boolean = false,
    val rankUpQuestCompleted: Boolean = false,
    
    // Workout stats
    val totalWorkouts: Int = 0,
    val totalWorkoutMinutes: Int = 0,
    val totalCaloriesBurned: Int = 0,
    
    // Achievement/trophy tracking
    val trophyPoints: Int = 0,
    val achievementCount: Int = 0
) {
    /**
     * Gets the stat value for a specific stat
     */
    fun getStatValue(stat: Stat): Int {
        return when (stat) {
            Stat.STRENGTH -> strength
            Stat.ENDURANCE -> endurance
            Stat.AGILITY -> agility
            Stat.VITALITY -> vitality
            Stat.INTELLIGENCE -> intelligence
            Stat.LUCK -> luck
        }
    }
    
    /**
     * Gets the display name for the user (hunter name or username)
     */
    fun getDisplayName(): String {
        return hunterName.ifEmpty { username }
    }
    
    /**
     * Gets the daily exp cap based on rank
     */
    fun getDailyExpCap(): Int {
        return when (rank) {
            Rank.E -> 500
            Rank.D -> 750
            Rank.C -> 1000
            Rank.B -> 1500  // Fixed from D to B
            Rank.A -> 2000
            Rank.S -> 3000
        }
    }
    
    /**
     * Calculates the percentage of exp progress to next level
     */
    fun getLevelProgress(): Float {
        return if (expToNextLevel > 0) {
            exp.toFloat() / expToNextLevel.toFloat() * 100f
        } else {
            0f
        }
    }
    
    /**
     * Calculates the total exp gained
     */
    fun getTotalExp(): Int {
        var totalExp = exp
        
        // Add in exp from previous levels
        for (lvl in 1 until level) {
            totalExp += calculateExpForLevel(lvl)
        }
        
        return totalExp
    }
    
    /**
     * Helper function to calculate exp for a given level
     */
    private fun calculateExpForLevel(level: Int): Int {
        return (100 * Math.pow(1.5, (level - 1).toDouble())).toInt()
    }
}
