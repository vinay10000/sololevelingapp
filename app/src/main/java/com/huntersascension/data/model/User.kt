package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.huntersascension.data.converter.Converters

@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class User(
    @PrimaryKey val username: String,
    val passwordHash: String,
    val email: String? = null,
    val rank: String = "E", // E, D, C, B, A, S
    val exp: Int = 0,
    val strength: Int = 10,
    val agility: Int = 10,
    val vitality: Int = 10,
    val intelligence: Int = 10,
    val luck: Int = 10,
    val streak: Int = 0,
    val unlockedAbilities: List<String> = listOf(),
    val equippedCosmetics: List<String> = listOf()
)
