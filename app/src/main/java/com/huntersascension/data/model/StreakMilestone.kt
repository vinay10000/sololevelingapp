package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "streak_milestones",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["email"],
            childColumns = ["userEmail"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StreakMilestone(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userEmail: String,
    val milestoneName: String, // "7-Day Streak", "30-Day Streak", etc.
    val streakCount: Int,
    val achievedDate: Date,
    val rewardType: String, // "trophy", "stat_bonus", "cosmetic"
    val rewardValue: String, // JSON string describing the reward
    val claimed: Boolean = false
)
