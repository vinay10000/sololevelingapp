package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userEmail: String,
    val workoutName: String,
    val workoutType: String, // "strength", "cardio", "flexibility", "hybrid"
    val difficulty: Int, // 1-5 scale
    val mainStat: String, // "strength", "agility", "vitality", "intelligence", "luck"
    val secondaryStat: String, // secondary stat that gets minor improvements
    val startTime: Date,
    val endTime: Date? = null,
    val duration: Long = 0, // in milliseconds
    val completed: Boolean = false,
    val caloriesBurned: Int = 0,
    val experienceGained: Int = 0,
    val strengthGained: Int = 0,
    val agilityGained: Int = 0,
    val vitalityGained: Int = 0,
    val intelligenceGained: Int = 0,
    val luckGained: Int = 0,
    val notes: String = ""
)
