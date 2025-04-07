package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.huntersascension.data.converter.Converters
import java.util.Date

@Entity(tableName = "workouts")
@TypeConverters(Converters::class)
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val type: String, // "STRENGTH", "AGILITY", "VITALITY"
    val exercises: List<String>,
    val completedExercises: List<String> = listOf(),
    val startTime: Date,
    val endTime: Date? = null,
    val expGained: Int = 0,
    val isCompleted: Boolean = false
)
