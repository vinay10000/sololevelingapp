package com.huntersascension.data

import com.huntersascension.data.model.Ability
import com.huntersascension.data.model.AbilityType
import com.huntersascension.data.model.Rank

/**
 * Helper class that provides predefined abilities for each rank
 */
object AbilityData {
    
    /**
     * Gets a list of new abilities unlocked at a specific rank
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
     * E-Rank abilities (basic) 
     */
    private fun getERankAbilities(): List<Ability> {
        return listOf(
            Ability(
                name = "Basic Training",
                description = "Unlocks basic workout tracking functionality",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.E
            ),
            Ability(
                name = "Beginner's Luck",
                description = "10% chance to gain double EXP from a workout",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.E,
                luckBoost = 1
            )
        )
    }
    
    /**
     * D-Rank abilities 
     */
    private fun getDRankAbilities(): List<Ability> {
        return listOf(
            Ability(
                name = "EXP Boost",
                description = "5% boost to all EXP gained",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.D,
                expBoostPercentage = 5
            ),
            Ability(
                name = "Endurance Focus",
                description = "Gain 1 extra endurance point when completing cardio workouts",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.D,
                enduranceBoost = 1
            ),
            Ability(
                name = "Strength Focus",
                description = "Gain 1 extra strength point when completing strength workouts",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.D,
                strengthBoost = 1
            )
        )
    }
    
    /**
     * C-Rank abilities 
     */
    private fun getCRankAbilities(): List<Ability> {
        return listOf(
            Ability(
                name = "Workout Templates",
                description = "Ability to create and save custom workout templates",
                type = AbilityType.SKILL,
                requiredRank = Rank.C,
                intelligenceBoost = 1
            ),
            Ability(
                name = "Recovery Boost",
                description = "Reduce rest time between sets by 10%",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.C,
                vitalityBoost = 1
            ),
            Ability(
                name = "Stat Multiplier",
                description = "Choose one stat to receive a 10% boost during workouts",
                type = AbilityType.ACTIVE,
                requiredRank = Rank.C,
                cooldownTime = 1440 // 24 hours
            )
        )
    }
    
    /**
     * B-Rank abilities 
     */
    private fun getBRankAbilities(): List<Ability> {
        return listOf(
            Ability(
                name = "Custom Workouts",
                description = "Create fully customized workouts with any exercise combination",
                type = AbilityType.SKILL,
                requiredRank = Rank.B
            ),
            Ability(
                name = "EXP Surge",
                description = "Double EXP for the next workout completed",
                type = AbilityType.ACTIVE,
                requiredRank = Rank.B,
                cooldownTime = 2880, // 48 hours
                expBoostPercentage = 100
            ),
            Ability(
                name = "Streak Shield",
                description = "Protect your streak once per week if you miss a day",
                type = AbilityType.ACTIVE,
                requiredRank = Rank.B,
                cooldownTime = 10080 // 1 week
            )
        )
    }
    
    /**
     * A-Rank abilities 
     */
    private fun getARankAbilities(): List<Ability> {
        return listOf(
            Ability(
                name = "Ability Specialization",
                description = "Choose from a variety of special abilities based on your playstyle",
                type = AbilityType.SKILL,
                requiredRank = Rank.A
            ),
            Ability(
                name = "Stat Reallocation",
                description = "Reallocate up to 10 stat points between different stats once per month",
                type = AbilityType.ACTIVE,
                requiredRank = Rank.A,
                cooldownTime = 43200 // 30 days
            ),
            Ability(
                name = "Expert Analysis",
                description = "Enhanced workout analytics and performance tracking",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.A,
                intelligenceBoost = 2
            ),
            Ability(
                name = "Supercharged",
                description = "10% boost to all stats for 24 hours",
                type = AbilityType.ACTIVE,
                requiredRank = Rank.A,
                cooldownTime = 4320, // 3 days
                durationTime = 1440, // 24 hours
                strengthBoost = 2,
                enduranceBoost = 2,
                agilityBoost = 2,
                vitalityBoost = 2
            )
        )
    }
    
    /**
     * S-Rank abilities 
     */
    private fun getSRankAbilities(): List<Ability> {
        return listOf(
            Ability(
                name = "Shadow Monarch Mode",
                description = "Double all EXP and stat gains during weekends",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.S,
                expBoostPercentage = 100,
                specialEffect = "Weekend double gains"
            ),
            Ability(
                name = "Legendary Status",
                description = "Unlocks unique cosmetic items and effects",
                type = AbilityType.PASSIVE,
                requiredRank = Rank.S
            ),
            Ability(
                name = "Army of Shadows",
                description = "Recruit friends to your team for bonus group rewards",
                type = AbilityType.SKILL,
                requiredRank = Rank.S
            ),
            Ability(
                name = "Arise",
                description = "Instantly complete your daily workout goals once per month",
                type = AbilityType.ACTIVE,
                requiredRank = Rank.S,
                cooldownTime = 43200 // 30 days
            ),
            Ability(
                name = "Transcendence",
                description = "Triple all stats for 1 hour",
                type = AbilityType.ACTIVE,
                requiredRank = Rank.S,
                cooldownTime = 10080, // 7 days
                durationTime = 60, // 1 hour
                strengthBoost = 5,
                enduranceBoost = 5,
                agilityBoost = 5,
                vitalityBoost = 5,
                intelligenceBoost = 5,
                luckBoost = 5
            )
        )
    }
}
