package com.huntersascension.data.model.social

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.huntersascension.data.model.Rank
import java.util.Date

/**
 * Entity representing a leaderboard
 */
@Entity(tableName = "leaderboards")
data class Leaderboard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: LeaderboardType,
    val scope: LeaderboardScope = LeaderboardScope.GLOBAL,
    val name: String,
    val description: String,
    val specificRank: Rank? = null, // For rank-specific leaderboards
    val statName: String? = null, // For stat-specific leaderboards
    val createdDate: Date = Date(),
    val lastUpdated: Date = Date(),
    val resetFrequency: Int = 0, // 0 = never, 1 = daily, 7 = weekly, 30 = monthly
    val lastReset: Date? = null
)
