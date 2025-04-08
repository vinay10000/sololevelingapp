package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

/**
 * Entity representing a workout
 */
@Entity(
    tableName = "workouts",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * ID of the user who created the workout
     */
    val userId: Long,
    
    /**
     * Name of the workout
     */
    val name: String,
    
    /**
     * Optional description of the workout
     */
    val description: String? = null,
    
    /**
     * Type of workout (Strength, Cardio, Flexibility, Hybrid)
     */
    val type: String,
    
    /**
     * Primary stat this workout improves (Strength, Endurance, Agility, Vitality)
     */
    val primaryStat: String,
    
    /**
     * Secondary stat this workout improves (can be null)
     */
    val secondaryStat: String? = null,
    
    /**
     * Difficulty level of the workout (1-5)
     */
    val difficulty: Int,
    
    /**
     * Estimated duration of the workout in minutes
     */
    val estimatedDuration: Int = 0,
    
    /**
     * Whether this workout is marked as a favorite
     */
    val isFavorite: Boolean = false,
    
    /**
     * Date when the workout was created
     */
    val createdAt: Date,
    
    /**
     * Date when the workout was last updated
     */
    val updatedAt: Date
)
