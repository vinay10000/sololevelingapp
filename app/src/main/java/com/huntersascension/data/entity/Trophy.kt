package com.huntersascension.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.huntersascension.utils.DateConverter
import java.util.Date

/**
 * Trophy entity representing achievements earned by users
 */
@Entity(
    tableName = "trophies",
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
data class Trophy(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Foreign key to the user
    val username: String,
    
    // Trophy name
    val name: String,
    
    // Trophy description
    val description: String,
    
    // Bonus provided by the trophy (if any)
    val bonus: String,
    
    // Date when the trophy was earned
    val unlockedAt: Date = Date()
) {
    companion object {
        // Trophy types
        const val TROPHY_STREAK_7 = "7-Day Streak"
        const val TROPHY_STREAK_30 = "30-Day Streak"
        const val TROPHY_STREAK_60 = "60-Day Streak"
        const val TROPHY_STREAK_100 = "100-Day Streak"
        
        const val TROPHY_RANK_D = "D-Rank Achieved"
        const val TROPHY_RANK_C = "C-Rank Achieved"
        const val TROPHY_RANK_B = "B-Rank Achieved"
        const val TROPHY_RANK_A = "A-Rank Achieved"
        const val TROPHY_RANK_S = "S-Rank Achieved"
        
        const val TROPHY_WORKOUT_10 = "10 Workouts Completed"
        const val TROPHY_WORKOUT_50 = "50 Workouts Completed"
        const val TROPHY_WORKOUT_100 = "100 Workouts Completed"
    }
}
