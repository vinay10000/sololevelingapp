package com.huntersascension.data.model.social

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.huntersascension.data.model.User
import java.util.Date

/**
 * Entity representing a comment on a social share
 */
@Entity(
    tableName = "social_comments",
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
data class SocialComment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val shareId: Long,
    val username: String,
    val commentText: String,
    val createdDate: Date = Date(),
    val isEdited: Boolean = false,
    val lastEditDate: Date? = null
)
