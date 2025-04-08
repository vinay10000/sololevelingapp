package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Enum representing types of quests
 */
enum class QuestType {
    DAILY,
    WEEKLY,
    RANK_UP,
    SPECIAL
}

/**
 * Entity representing a quest/mission
 */
@Entity(tableName = "quests")
data class Quest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val type: QuestType,
    val requiredRank: Rank? = null, // For rank-specific quests
    val targetWorkoutType: WorkoutType? = null,
    val targetExerciseCategory: ExerciseCategory? = null,
    val targetCount: Int = 1,
    val timeLimit: Int? = null, // in minutes
    val expiryDate: Date? = null,
    val iconPath: String? = null,
    val expReward: Int = 0,
    val strengthReward: Int = 0,
    val enduranceReward: Int = 0,
    val agilityReward: Int = 0,
    val vitalityReward: Int = 0,
    val intelligenceReward: Int = 0,
    val luckReward: Int = 0,
    val isRepeatable: Boolean = false,
    val cooldownPeriod: Int? = null // in hours, for repeatable quests
)
