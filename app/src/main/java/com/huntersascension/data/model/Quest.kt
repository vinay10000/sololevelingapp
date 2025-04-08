package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import kotlin.math.min

/**
 * Enum representing quest types
 */
enum class QuestType {
    WORKOUT_COUNT, // Complete X workouts
    WORKOUT_DURATION, // Exercise for X minutes
    EXERCISE_COUNT, // Complete X exercises
    CALORIES_BURNED, // Burn X calories
    STREAK, // Maintain X day streak
    RANK_UP, // Complete rank up requirements
    STRENGTH_GAIN, // Gain X STR points
    ENDURANCE_GAIN, // Gain X END points
    AGILITY_GAIN, // Gain X AGI points
    VITALITY_GAIN, // Gain X VIT points
    CUSTOM // Special quest with custom criteria
}

/**
 * Quest entity representing challenges that reward users
 */
@Entity(tableName = "quests")
data class Quest(
    @PrimaryKey
    val questId: String = UUID.randomUUID().toString(),
    
    // Basic info
    val title: String,
    val description: String,
    val type: QuestType,
    
    // Progression
    val currentValue: Int = 0,
    val targetValue: Int,
    
    // Rewards
    val expReward: Int = 0,
    val strengthReward: Int = 0,
    val enduranceReward: Int = 0,
    val agilityReward: Int = 0,
    val vitalityReward: Int = 0,
    val intelligenceReward: Int = 0,
    val luckReward: Int = 0,
    
    // Status
    val isCompleted: Boolean = false,
    val isInProgress: Boolean = false,
    val isDaily: Boolean = false,
    val completedDate: Date? = null,
    
    // Timing
    val expiryDate: Date? = null,
    val createdDate: Date = Date(),
    
    // Requirements
    val requiredRank: Rank = Rank.E,
    
    // Display
    val iconUrl: String? = null
) {
    /**
     * Gets the progress percentage
     */
    val progressPercentage: Float
        get() = (currentValue.toFloat() / targetValue.toFloat()) * 100f
    
    /**
     * Checks if the quest is available to a user
     */
    fun isAvailableForUser(userRank: Rank): Boolean {
        return userRank.ordinal >= requiredRank.ordinal
    }
    
    /**
     * Updates the quest progress
     */
    fun updateProgress(increment: Int): Quest {
        // Calculate new value, capped at target
        val newValue = min(currentValue + increment, targetValue)
        
        // Check if quest is now completed
        val isNowCompleted = newValue >= targetValue
        
        return copy(
            currentValue = newValue,
            isCompleted = isNowCompleted,
            isInProgress = !isNowCompleted && newValue > 0,
            completedDate = if (isNowCompleted && completedDate == null) Date() else completedDate
        )
    }
    
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
        
        return rewards.joinToString(", ")
    }
    
    /**
     * Checks if the quest has expired
     */
    fun isExpired(): Boolean {
        return if (expiryDate != null) {
            Date().after(expiryDate)
        } else {
            false
        }
    }
}
