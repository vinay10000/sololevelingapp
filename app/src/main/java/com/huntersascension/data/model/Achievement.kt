package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Enum representing categories of achievements
 */
enum class AchievementCategory {
    WORKOUT_COUNT,
    EXERCISE_COUNT,
    STREAK,
    CALORIES,
    DISTANCE,
    LEVEL,
    RANK,
    STRENGTH,
    ENDURANCE,
    AGILITY,
    VITALITY,
    INTELLIGENCE,
    LUCK,
    SPECIAL
}

/**
 * Enum representing tiers of achievements
 */
enum class AchievementTier {
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM,
    DIAMOND
}

/**
 * Entity representing an achievement that can be earned
 */
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: AchievementCategory,
    val tier: AchievementTier,
    val requirement: String,
    val targetValue: Int,
    val iconPath: String? = null,
    val expReward: Int = 0,
    val strengthReward: Int = 0,
    val enduranceReward: Int = 0,
    val agilityReward: Int = 0,
    val vitalityReward: Int = 0,
    val intelligenceReward: Int = 0,
    val luckReward: Int = 0,
    val trophyPoints: Int = 0,
    val specialReward: String? = null
)
