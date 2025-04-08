package com.huntersascension.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing an exercise
 */
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Name of the exercise
     */
    val name: String,
    
    /**
     * Description of the exercise
     */
    val description: String? = null,
    
    /**
     * Type of exercise (Strength, Cardio, Flexibility, etc.)
     */
    val type: String,
    
    /**
     * Primary stat this exercise improves (Strength, Endurance, Agility, Vitality)
     */
    val primaryStat: String,
    
    /**
     * Secondary stat this exercise improves (can be null)
     */
    val secondaryStat: String? = null,
    
    /**
     * Muscle group targeted (Chest, Back, Legs, etc.)
     */
    val muscleGroup: String? = null,
    
    /**
     * Equipment needed for the exercise (can be null for bodyweight exercises)
     */
    val equipment: String? = null,
    
    /**
     * Difficulty level (1-5)
     */
    val difficulty: Int = 3,
    
    /**
     * Whether it's a compound exercise (works multiple muscle groups)
     */
    val isCompound: Boolean = false,
    
    /**
     * Whether this is a bodyweight exercise
     */
    val isBodyweight: Boolean = false,
    
    /**
     * Estimated calories burned per unit
     * For strength: per rep
     * For cardio: per minute
     * For flexibility: per minute of stretch
     */
    val caloriesPerUnit: Int? = null,
    
    /**
     * XP earned per set of this exercise
     */
    val xpPerSet: Int? = null,
    
    /**
     * URL to video demonstration
     */
    val videoUrl: String? = null,
    
    /**
     * URL to image demonstration
     */
    val imageUrl: String? = null,
    
    /**
     * Instructions for performing the exercise
     */
    val instructions: String? = null
)
