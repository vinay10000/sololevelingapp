package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.WorkoutHistory
import java.util.*

/**
 * Data Access Object for WorkoutHistory entities
 */
@Dao
interface WorkoutHistoryDao {
    /**
     * Get all workout history entries
     * @return LiveData list of all workout history entries
     */
    @Query("SELECT * FROM workout_history ORDER BY startTime DESC")
    fun getAllWorkoutHistory(): LiveData<List<WorkoutHistory>>
    
    /**
     * Get workout history for a user
     * @param userId The ID of the user
     * @return LiveData list of workout history for the user
     */
    @Query("SELECT * FROM workout_history WHERE userId = :userId ORDER BY startTime DESC")
    fun getWorkoutHistoryForUser(userId: Long): LiveData<List<WorkoutHistory>>
    
    /**
     * Get recent workout history for a user
     * @param userId The ID of the user
     * @param limit The maximum number of entries to return
     * @return LiveData list of recent workout history for the user
     */
    @Query("SELECT * FROM workout_history WHERE userId = :userId ORDER BY startTime DESC LIMIT :limit")
    fun getRecentWorkoutHistory(userId: Long, limit: Int): LiveData<List<WorkoutHistory>>
    
    /**
     * Get workout history by workout
     * @param workoutId The ID of the workout
     * @return LiveData list of workout history for the workout
     */
    @Query("SELECT * FROM workout_history WHERE workoutId = :workoutId ORDER BY startTime DESC")
    fun getWorkoutHistoryByWorkout(workoutId: Long): LiveData<List<WorkoutHistory>>
    
    /**
     * Get workout history by ID
     * @param workoutHistoryId The ID of the workout history
     * @return The workout history with the specified ID, or null if not found
     */
    @Query("SELECT * FROM workout_history WHERE id = :workoutHistoryId")
    suspend fun getWorkoutHistoryById(workoutHistoryId: Long): WorkoutHistory?
    
    /**
     * Insert a new workout history entry
     * @param workoutHistory The workout history to insert
     * @return The ID of the inserted workout history
     */
    @Insert
    suspend fun insertWorkoutHistory(workoutHistory: WorkoutHistory): Long
    
    /**
     * Update an existing workout history entry
     * @param workoutHistory The workout history to update
     */
    @Update
    suspend fun updateWorkoutHistory(workoutHistory: WorkoutHistory)
    
    /**
     * Delete a workout history entry
     * @param workoutHistory The workout history to delete
     */
    @Delete
    suspend fun deleteWorkoutHistory(workoutHistory: WorkoutHistory)
    
    /**
     * Get workout history for a date range
     * @param userId The ID of the user
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return LiveData list of workout history within the date range
     */
    @Query("SELECT * FROM workout_history WHERE userId = :userId AND startTime BETWEEN :startDate AND :endDate ORDER BY startTime DESC")
    fun getWorkoutHistoryForDateRange(userId: Long, startDate: Date, endDate: Date): LiveData<List<WorkoutHistory>>
    
    /**
     * Get total workouts by type for a user
     * @param userId The ID of the user
     * @return Map of workout type to count
     */
    @Query("SELECT workoutType, COUNT(*) as count FROM workout_history WHERE userId = :userId GROUP BY workoutType")
    suspend fun getWorkoutTypeCounts(userId: Long): Map<String, Int>
    
    /**
     * Get total calories burned for a user in a date range
     * @param userId The ID of the user
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return Total calories burned
     */
    @Query("SELECT SUM(caloriesBurned) FROM workout_history WHERE userId = :userId AND startTime BETWEEN :startDate AND :endDate")
    suspend fun getTotalCaloriesForDateRange(userId: Long, startDate: Date, endDate: Date): Int?
    
    /**
     * Get total workout duration for a user in a date range
     * @param userId The ID of the user
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return Total workout duration in minutes
     */
    @Query("SELECT SUM(durationMinutes) FROM workout_history WHERE userId = :userId AND startTime BETWEEN :startDate AND :endDate")
    suspend fun getTotalDurationForDateRange(userId: Long, startDate: Date, endDate: Date): Int?
    
    /**
     * Get total XP earned for a user in a date range
     * @param userId The ID of the user
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return Total XP earned
     */
    @Query("SELECT SUM(xpEarned) FROM workout_history WHERE userId = :userId AND startTime BETWEEN :startDate AND :endDate")
    suspend fun getTotalXpForDateRange(userId: Long, startDate: Date, endDate: Date): Int?
    
    /**
     * Get the current streak for a user
     * @param userId The ID of the user
     * @return The current streak
     */
    @Transaction
    suspend fun getCurrentStreak(userId: Long): Int {
        // Get the most recent workout
        val recentWorkout = getRecentWorkoutForUser(userId) ?: return 0
        
        // Calculate today's date at midnight
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val today = calendar.time
        
        // Check if the most recent workout was today or yesterday
        val recentWorkoutDate = Calendar.getInstance()
        recentWorkoutDate.time = recentWorkout.startTime
        recentWorkoutDate.set(Calendar.HOUR_OF_DAY, 0)
        recentWorkoutDate.set(Calendar.MINUTE, 0)
        recentWorkoutDate.set(Calendar.SECOND, 0)
        recentWorkoutDate.set(Calendar.MILLISECOND, 0)
        
        val dayDifference = (today.time - recentWorkoutDate.time.time) / (1000 * 60 * 60 * 24)
        
        // If the most recent workout was more than a day ago, streak is broken
        if (dayDifference > 1) {
            return 0
        }
        
        // Count consecutive days with workouts
        var streak = 1 // Count the most recent day
        var currentDate = recentWorkoutDate.time
        
        while (true) {
            // Move to the previous day
            recentWorkoutDate.add(Calendar.DAY_OF_MONTH, -1)
            val previousDay = recentWorkoutDate.time
            
            // Check if there was a workout on the previous day
            val hasWorkout = hasWorkoutOnDate(userId, previousDay)
            
            if (hasWorkout) {
                streak++
                currentDate = previousDay
            } else {
                break
            }
        }
        
        return streak
    }
    
    /**
     * Get the most recent workout for a user
     * @param userId The ID of the user
     * @return The most recent workout history, or null if no workouts found
     */
    @Query("SELECT * FROM workout_history WHERE userId = :userId ORDER BY startTime DESC LIMIT 1")
    suspend fun getRecentWorkoutForUser(userId: Long): WorkoutHistory?
    
    /**
     * Check if a user has a workout on a specific date
     * @param userId The ID of the user
     * @param date The date to check
     * @return True if the user has a workout on the date, false otherwise
     */
    @Transaction
    suspend fun hasWorkoutOnDate(userId: Long, date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time
        
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.time
        
        return getWorkoutCountForDateRange(userId, startOfDay, endOfDay) > 0
    }
    
    /**
     * Get the count of workouts in a date range
     * @param userId The ID of the user
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return The count of workouts
     */
    @Query("SELECT COUNT(*) FROM workout_history WHERE userId = :userId AND startTime BETWEEN :startDate AND :endDate")
    suspend fun getWorkoutCountForDateRange(userId: Long, startDate: Date, endDate: Date): Int
}
