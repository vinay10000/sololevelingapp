package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "leaderboard_entries",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["email"],
            childColumns = ["userEmail"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LeaderboardEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userEmail: String,
    val username: String,
    val rank: String,
    val level: Int,
    val experience: Int,
    val strength: Int,
    val agility: Int,
    val vitality: Int,
    val intelligence: Int,
    val luck: Int,
    val totalWorkouts: Int,
    val streak: Int,
    val lastUpdated: Date,
    val region: String = "global",
    val leaderboardCategory: String // "exp", "streak", "strength", etc.
)
