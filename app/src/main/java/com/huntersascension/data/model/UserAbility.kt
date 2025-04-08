package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

/**
 * Entity representing an ability that a user has unlocked
 */
@Entity(
    tableName = "user_abilities",
    primaryKeys = ["username", "abilityId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ability::class,
            parentColumns = ["id"],
            childColumns = ["abilityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("username"),
        Index("abilityId")
    ]
)
data class UserAbility(
    val username: String,
    val abilityId: Long,
    val isEquipped: Boolean = false,
    val isActive: Boolean = false,
    val unlockDate: Date = Date(),
    val lastActivated: Date? = null,
    val cooldownEndTime: Date? = null,
    val activeUntil: Date? = null,
    val usageCount: Int = 0
)
