package com.huntersascension.utils

import com.huntersascension.data.entity.User

/**
 * Manages user rank progression and requirements
 */
class RankManager {
    // EXP thresholds for each rank
    private val rankExpThresholds = mapOf(
        "E" to 0,
        "D" to 500,
        "C" to 2000,
        "B" to 5000,
        "A" to 10000,
        "S" to 20000
    )
    
    // Stat requirements for each rank
    private val rankStatRequirements = mapOf(
        "E" to mapOf("STR" to 0, "AGI" to 0, "VIT" to 0, "INT" to 0, "LUK" to 0),
        "D" to mapOf("STR" to 20, "AGI" to 15, "VIT" to 15, "INT" to 10, "LUK" to 5),
        "C" to mapOf("STR" to 35, "AGI" to 30, "VIT" to 30, "INT" to 20, "LUK" to 10),
        "B" to mapOf("STR" to 50, "AGI" to 45, "VIT" to 45, "INT" to 30, "LUK" to 15),
        "A" to mapOf("STR" to 70, "AGI" to 65, "VIT" to 65, "INT" to 45, "LUK" to 25),
        "S" to mapOf("STR" to 90, "AGI" to 85, "VIT" to 85, "INT" to 60, "LUK" to 40)
    )
    
    /**
     * Gets the EXP required for a specific rank
     */
    fun getExpForRank(rank: String): Int {
        return rankExpThresholds[rank] ?: 0
    }
    
    /**
     * Gets the stat requirements for a specific rank
     */
    fun getStatRequirementsForRank(rank: String): Map<String, Int> {
        return rankStatRequirements[rank] ?: emptyMap()
    }
    
    /**
     * Checks if a user is eligible for rank up
     */
    fun isEligibleForRankUp(user: User, targetRank: String): Boolean {
        // Check if user has enough EXP
        if (user.exp < getExpForRank(targetRank)) {
            return false
        }
        
        // Check if user meets stat requirements
        val statRequirements = getStatRequirementsForRank(targetRank)
        
        if (user.strStat < (statRequirements["STR"] ?: 0)) return false
        if (user.agiStat < (statRequirements["AGI"] ?: 0)) return false
        if (user.vitStat < (statRequirements["VIT"] ?: 0)) return false
        if (user.intStat < (statRequirements["INT"] ?: 0)) return false
        if (user.lukStat < (statRequirements["LUK"] ?: 0)) return false
        
        return true
    }
    
    /**
     * Gets the rank up challenge description for a specific rank
     */
    fun getRankUpChallengeDescription(targetRank: String): String {
        return when (targetRank) {
            "D" -> "Complete 50 push-ups in one session."
            "C" -> "Complete 100 squats in one session."
            "B" -> "Complete a combined 200 reps of push-ups and squats."
            "A" -> "Complete a Boss-level workout of any exercise type."
            "S" -> "Complete 3 consecutive days of Hard or Boss difficulty workouts."
            else -> "No challenge available."
        }
    }
    
    /**
     * Gets the next EXP target for rank up
     */
    fun getNextExpTarget(currentRank: String): Int? {
        val nextRank = when (currentRank) {
            "E" -> "D"
            "D" -> "C"
            "C" -> "B"
            "B" -> "A"
            "A" -> "S"
            else -> null
        }
        
        return nextRank?.let { getExpForRank(it) }
    }
}
