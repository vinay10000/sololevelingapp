package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Enum representing ability types
 */
enum class AbilityType {
    PASSIVE, // Always active once unlocked
    ACTIVE, // Must be activated
    BUFF, // Temporary stat boost
    SKILL, // Special skill
    UTILITY // Non-combat utility
}

/**
 * Ability entity representing special powers unlocked at different ranks
 */
@Entity(tableName = "abilities")
data class Ability(
    @PrimaryKey
    val abilityId: String = UUID.randomUUID().toString(),
    
    // Basic info
    val name: String,
    val description: String,
    val type: AbilityType,
    
    // Requirements
    val requiredRank: Rank,
    
    // Effects
    val expMultiplier: Float? = null,
    val strengthMultiplier: Float? = null,
    val enduranceMultiplier: Float? = null,
    val agilityMultiplier: Float? = null,
    val vitalityMultiplier: Float? = null,
    val intelligenceMultiplier: Float? = null,
    val luckMultiplier: Float? = null,
    
    // Active ability properties
    val cooldownMinutes: Int? = null,
    val durationMinutes: Int? = null,
    
    // Display
    val iconUrl: String? = null,
    val effectDescription: String = ""
) {
    /**
     * Checks if this is a stat multiplier ability
     */
    fun isStatMultiplier(): Boolean {
        return strengthMultiplier != null || enduranceMultiplier != null || 
               agilityMultiplier != null || vitalityMultiplier != null || 
               intelligenceMultiplier != null || luckMultiplier != null
    }
    
    /**
     * Gets the multiplier for a specific stat
     */
    fun getMultiplierForStat(stat: Stat): Float {
        return when (stat) {
            Stat.STRENGTH -> strengthMultiplier ?: 1.0f
            Stat.ENDURANCE -> enduranceMultiplier ?: 1.0f
            Stat.AGILITY -> agilityMultiplier ?: 1.0f
            Stat.VITALITY -> vitalityMultiplier ?: 1.0f
            Stat.INTELLIGENCE -> intelligenceMultiplier ?: 1.0f
            Stat.LUCK -> luckMultiplier ?: 1.0f
        }
    }
    
    /**
     * Gets a display string for the cooldown/duration
     */
    fun getTimingDisplayString(): String {
        val parts = mutableListOf<String>()
        
        durationMinutes?.let {
            val unit = if (it == 1) "minute" else "minutes"
            parts.add("Duration: $it $unit")
        }
        
        cooldownMinutes?.let {
            val unit = if (it == 1) "minute" else "minutes"
            parts.add("Cooldown: $it $unit")
        }
        
        return parts.joinToString(" | ")
    }
}

/**
 * Junction entity to connect Users and Abilities
 */
@Entity(
    tableName = "user_abilities",
    primaryKeys = ["username", "abilityId"]
)
data class UserAbility(
    val username: String,
    val abilityId: String,
    val isActive: Boolean = false,
    val usageCount: Int = 0,
    val lastUsedTimestamp: Long? = null
)

/**
 * Data class providing sample abilities for each rank
 */
object AbilityData {
    
    /**
     * Gets new abilities unlocked at a specific rank
     */
    fun getNewAbilitiesForRank(rank: Rank): List<Ability> {
        return when (rank) {
            Rank.E -> getERankAbilities()
            Rank.D -> getDRankAbilities()
            Rank.C -> getCRankAbilities()
            Rank.B -> getBRankAbilities()
            Rank.A -> getARankAbilities()
            Rank.S -> getSRankAbilities()
        }
    }
    
    /**
     * Gets all E-Rank abilities
     */
    private fun getERankAbilities(): List<Ability> {
        return listOf(
            Ability(
                name = "Basic Tracking",
                description = "Unlocks basic workout tracking features",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.E,
                effectDescription = "Allows tracking of workouts, exercises, and basic stats"
            )
        )
    }
    
    /**
     * Gets all D-Rank abilities
     */
    private fun getDRankAbilities(): List<Ability> {
        return listOf(
            Ability(
                name = "Experience Boost",
                description = "Gain 5% more experience from all workouts",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.D,
                expMultiplier = 1.05f,
                effectDescription = "Your workouts generate 5% more experience"
            ),
            Ability(
                name = "Steady Pace",
                description = "Gain a small bonus to endurance training",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.D,
                enduranceMultiplier = 1.1f,
                effectDescription = "Endurance stat gains are increased by 10%"
            )
        )
    }
    
    /**
     * Gets all C-Rank abilities
     */
    private fun getCRankAbilities(): List<Ability> {
        return listOf(
            Ability(
                name = "Strength Focus",
                description = "Gain a significant bonus to strength training",
                type = AbilityType.ACTIVE,
                requiredRank = Rank.C,
                strengthMultiplier = 1.2f,
                cooldownMinutes = 1440, // 24 hours
                durationMinutes = 60,
                effectDescription = "Strength stat gains are increased by 20% for 1 hour"
            ),
            Ability(
                name = "Agility Focus",
                description = "Gain a significant bonus to agility training",
                type = AbilityType.ACTIVE,
                requiredRank = Rank.C,
                agilityMultiplier = 1.2f,
                cooldownMinutes = 1440, // 24 hours
                durationMinutes = 60,
                effectDescription = "Agility stat gains are increased by 20% for 1 hour"
            ),
            Ability(
                name = "Vitality Focus",
                description = "Gain a significant bonus to vitality training",
                type = AbilityType.ACTIVE,
                requiredRank = Rank.C,
                vitalityMultiplier = 1.2f,
                cooldownMinutes = 1440, // 24 hours
                durationMinutes = 60,
                effectDescription = "Vitality stat gains are increased by 20% for 1 hour"
            )
        )
    }
    
    /**
     * Gets all B-Rank abilities
     */
    private fun getBRankAbilities(): List<Ability> {
        return listOf(
            Ability(
                name = "Workout Template Master",
                description = "Create and save custom workout templates",
                type = AbilityType.UTILITY,
                requiredRank = Rank.B,
                effectDescription = "Unlocks the ability to create, save, and share custom workout templates"
            ),
            Ability(
                name = "Second Wind",
                description = "Once per day, restore 50% of your daily EXP cap",
                type = AbilityType.ACTIVE,
                requiredRank = Rank.B,
                cooldownMinutes = 1440, // 24 hours
                effectDescription = "Instantly restores 50% of your remaining daily EXP cap"
            )
        )
    }
    
    /**
     * Gets all A-Rank abilities
     */
    private fun getARankAbilities(): List<Ability> {
        return listOf(
            Ability(
                name = "Stat Specialization",
                description = "Choose one stat to receive a permanent 15% bonus",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.A,
                effectDescription = "Choose one stat to receive a permanent 15% bonus to all gains"
            ),
            Ability(
                name = "Streak Protection",
                description = "Once per week, prevent a streak from breaking",
                type = AbilityType.ACTIVE,
                requiredRank = Rank.A,
                cooldownMinutes = 10080, // 7 days
                effectDescription = "Maintains your current streak even if you miss a day"
            )
        )
    }
    
    /**
     * Gets all S-Rank abilities
     */
    private fun getSRankAbilities(): List<Ability> {
        return listOf(
            Ability(
                name = "Shadow Monarch",
                description = "Double EXP on weekends",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.S,
                expMultiplier = 2.0f,
                effectDescription = "All workouts completed on Saturday and Sunday earn double EXP"
            ),
            Ability(
                name = "Arise",
                description = "Once per month, gain a massive stat boost for one workout",
                type = AbilityType.ACTIVE,
                requiredRank = Rank.S,
                strengthMultiplier = 2.0f,
                enduranceMultiplier = 2.0f,
                agilityMultiplier = 2.0f,
                vitalityMultiplier = 2.0f,
                intelligenceMultiplier = 2.0f,
                luckMultiplier = 2.0f,
                cooldownMinutes = 43200, // 30 days
                durationMinutes = 60,
                effectDescription = "For one hour, all stat gains are doubled"
            )
        )
    }
}
