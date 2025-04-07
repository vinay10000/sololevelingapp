package com.huntersascension.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.huntersascension.utils.DateConverter
import java.util.Date

/**
 * Workout entity representing a completed workout/exercise
 */
@Entity(
    tableName = "workouts",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(DateConverter::class)
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Foreign key to the user
    val username: String,
    
    // Exercise type (push-ups, squats, running, etc)
    val exerciseType: String,
    
    // Difficulty level
    val difficulty: String,
    
    // Number of reps
    val reps: Int,
    
    // Duration in seconds (if applicable)
    val duration: Int = 0,
    
    // Experience points earned
    val expGained: Int,
    
    // Stat gains
    val strGained: Int = 0,
    val agiGained: Int = 0,
    val vitGained: Int = 0,
    val intGained: Int = 0,
    val lukGained: Int = 0,
    
    // Date and time
    val completedAt: Date = Date()
) {
    companion object {
        // Exercise types
        const val EXERCISE_PUSHUPS = "push-ups"
        const val EXERCISE_SQUATS = "squats"
        const val EXERCISE_RUNNING = "running"
        
        // Difficulty levels
        const val DIFFICULTY_EASY = "easy"
        const val DIFFICULTY_MEDIUM = "medium"
        const val DIFFICULTY_HARD = "hard"
        const val DIFFICULTY_BOSS = "boss"
    }
    
    /**
     * Get the difficulty multiplier
     */
    fun getDifficultyMultiplier(): Float {
        return when (difficulty) {
            DIFFICULTY_EASY -> 1.0f
            DIFFICULTY_MEDIUM -> 1.5f
            DIFFICULTY_HARD -> 2.0f
            DIFFICULTY_BOSS -> 3.0f
            else -> 1.0f
        }
    }
}
