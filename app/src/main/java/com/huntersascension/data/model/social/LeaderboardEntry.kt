package com.huntersascension.data.model.social

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.huntersascension.data.model.User
import java.util.Date

/**
 * Entity representing an entry in a leaderboard
 */
@Entity(
    tableName = "leaderboard_entries",
    primaryKeys = ["leaderboardId", "username"],
    foreignKeys = [
        ForeignKey(
            entity = Leaderboard::class,
            parentColumns = ["id"],
            childColumns = ["leaderboardId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("leaderboardId"),
        Index("username")
    ]
)
data class LeaderboardEntry(
    val leaderboardId: Long,
    val username: String,
    val rank: Int = 0, // Current rank/position on the leaderboard
    val previousRank: Int? = null, // Previous rank for trend tracking
    val score: Int, // The score/value for this entry
    val updatedDate: Date = Date()
)
