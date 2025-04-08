package com.huntersascension.data.model.social

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.huntersascension.data.model.User
import java.util.*

/**
 * Enum representing friend relation status
 */
enum class FriendRequestStatus {
    PENDING, ACCEPTED, REJECTED, BLOCKED
}

/**
 * Entity representing a friend relation between two users
 */
@Entity(
    tableName = "friend_relations",
    primaryKeys = ["username", "friendUsername"],
    indices = [
        Index("friendUsername")
    ],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["friendUsername"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FriendRelation(
    val username: String,
    val friendUsername: String,
    val status: FriendRequestStatus = FriendRequestStatus.PENDING,
    val requestDate: Date = Date(),
    val responseDate: Date? = null,
    val lastInteractionDate: Date? = null,
    val notes: String? = null
)
