package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Enum representing achievement categories
 */
enum class AchievementCategory {
    WORKOUT_COUNT, STREAK, STRENGTH, ENDURANCE, AGILITY, VITALITY,
    RANK_UP, CONSISTENCY, CALORIES, EXERCISE_MASTERY, SPECIAL
}

/**
 * Enum representing achievement tiers
 */
enum class AchievementTier {
    BRONZE, SILVER, GOLD, PLATINUM, LEGENDARY;
    
    fun getPointValue(): Int {
        return when (this) {
            BRONZE -> 1
            SILVER -> 3
            GOLD -> 5
            PLATINUM -> 10
            LEGENDARY -> 20
        }
    }
    
    fun getExpReward(): Int {
        return when (this) {
            BRONZE -> 50
            SILVER -> 100
            GOLD -> 200
            PLATINUM -> 500
            LEGENDARY -> 1000
        }
    }
}

/**
 * Achievement entity representing unlockable achievements
 */
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey
    val achievementId: String = UUID.randomUUID().toString(),
    
    // Basic info
    val title: String,
    val description: String,
    val category: AchievementCategory,
    val tier: AchievementTier,
    
    // Criteria
    val requirement: String, // Human-readable requirement
    val targetValue: Int, // Value needed to unlock
    
    // Rewards
    val expReward: Int = 0,
    val strengthReward: Int = 0,
    val enduranceReward: Int = 0,
    val agilityReward: Int = 0,
    val vitalityReward: Int = 0,
    val intelligenceReward: Int = 0,
    val luckReward: Int = 0,
    val trophyPoints: Int = 0,
    
    // Status
    val isUnlocked: Boolean = false,
    val username: String? = null, // Set when unlocked
    val unlockedDate: Date? = null,
    
    // Display
    val imageUrl: String? = null,
    val badgeColor: String? = null
) {
    /**
     * Gets the total stat points rewarded
     */
    fun getTotalStatPoints(): Int {
        return strengthReward + enduranceReward + agilityReward + 
               vitalityReward + intelligenceReward + luckReward
    }
    
    /**
     * Gets the text for the reward
     */
    fun getRewardText(): String {
        val rewards = mutableListOf<String>()
        
        if (expReward > 0) rewards.add("$expReward EXP")
        if (strengthReward > 0) rewards.add("+$strengthReward STR")
        if (enduranceReward > 0) rewards.add("+$enduranceReward END")
        if (agilityReward > 0) rewards.add("+$agilityReward AGI")
        if (vitalityReward > 0) rewards.add("+$vitalityReward VIT")
        if (intelligenceReward > 0) rewards.add("+$intelligenceReward INT")
        if (luckReward > 0) rewards.add("+$luckReward LUK")
        if (trophyPoints > 0) rewards.add("$trophyPoints Trophy Points")
        
        return if (rewards.isEmpty()) "Achievement Unlocked!" else rewards.joinToString(", ")
    }
}
