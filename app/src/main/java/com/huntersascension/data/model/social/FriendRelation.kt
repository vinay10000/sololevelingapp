package com.huntersascension.data.model.social

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.huntersascension.data.model.User
import java.util.Date

/**
 * Entity representing a friendship or relationship between two users
 */
@Entity(
    tableName = "friend_relations",
    primaryKeys = ["userUsername", "friendUsername"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["userUsername"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["friendUsername"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userUsername"),
        Index("friendUsername")
    ]
)
data class FriendRelation(
    val userUsername: String,
    val friendUsername: String,
    val status: FriendRequestStatus,
    val createdDate: Date = Date(),
    val updatedDate: Date = Date()
)
