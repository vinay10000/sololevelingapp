package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Enum representing types of abilities
 */
enum class AbilityType {
    PASSIVE,  // Always active
    ACTIVE,   // Must be manually activated
    BOOST,    // Temporary stat boost
    SKILL,    // Unlocks new functionality
    PERK      // Special benefit
}

/**
 * Entity representing an ability that can be unlocked
 */
@Entity(tableName = "abilities")
data class Ability(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val type: AbilityType,
    val requiredRank: Rank,
    val iconPath: String? = null,
    val cooldownTime: Int = 0, // in minutes, 0 = no cooldown
    val durationTime: Int = 0, // in minutes, 0 = permanent/passive
    val expBoostPercentage: Int = 0,
    val strengthBoost: Int = 0,
    val enduranceBoost: Int = 0,
    val agilityBoost: Int = 0,
    val vitalityBoost: Int = 0,
    val intelligenceBoost: Int = 0,
    val luckBoost: Int = 0,
    val specialEffect: String? = null
)
