package com.huntersascension.data.model.social

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.huntersascension.data.model.User
import java.util.Date

/**
 * Entity representing a social post/share
 */
@Entity(
    tableName = "social_shares",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("username")
    ]
)
data class SocialShare(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val contentType: SharedContentType,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val referenceId: Long? = null, // ID of referenced content (e.g., workout ID, achievement ID)
    val createdDate: Date = Date(),
    val isEdited: Boolean = false,
    val lastEditDate: Date? = null,
    val isPublic: Boolean = true,
    val likes: Int = 0,
    val comments: Int = 0
)
