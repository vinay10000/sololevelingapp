package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.huntersascension.data.converter.Converters
import java.util.Date

@Entity(tableName = "trophies")
@TypeConverters(Converters::class)
data class Trophy(
    @PrimaryKey val id: String,
    val username: String,
    val name: String,
    val description: String,
    val iconResource: String,
    val acquiredDate: Date,
    val rarity: String // COMMON, RARE, EPIC, LEGENDARY
)
