package com.huntersascension.data.model.social

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.huntersascension.data.model.User
import java.util.Date

/**
 * Entity representing a like on a social share
 */
@Entity(
    tableName = "social_likes",
    primaryKeys = ["shareId", "username"],
    foreignKeys = [
        ForeignKey(
            entity = SocialShare::class,
            parentColumns = ["id"],
            childColumns = ["shareId"],
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
        Index("shareId"),
        Index("username")
    ]
)
data class SocialLike(
    val shareId: Long,
    val username: String,
    val createdDate: Date = Date()
)
