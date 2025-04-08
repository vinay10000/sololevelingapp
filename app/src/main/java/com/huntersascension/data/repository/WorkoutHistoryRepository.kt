package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.WorkoutHistoryDao
import com.huntersascension.data.model.WorkoutHistory
import java.util.*

/**
 * Repository for workout history data
 */
class WorkoutHistoryRepository(private val workoutHistoryDao: WorkoutHistoryDao) {
    
    /**
     * Get all workout history entries
     * @return LiveData list of all workout history entries
     */
    fun getAllWorkoutHistory(): LiveData<List<WorkoutHistory>> {
        return workoutHistoryDao.getAllWorkoutHistory()
    }
    
    /**
     * Get workout history for a user
     * @param userId The ID of the user
     * @return LiveData list of workout history for the user
     */
    fun getWorkoutHistoryForUser(userId: Long): LiveData<List<WorkoutHistory>> {
        return workoutHistoryDao.getWorkoutHistoryForUser(userId)
    }
    
    /**
     * Get recent workout history for a user
     * @param userId The ID of the user
     * @param limit The maximum number of entries to return
     * @return LiveData list of recent workout history for the user
     */
    fun getRecentWorkoutHistory(userId: Long, limit: Int): LiveData<List<WorkoutHistory>> {
        return workoutHistoryDao.getRecentWorkoutHistory(userId, limit)
    }
    
    /**
     * Get workout history by workout
     * @param workoutId The ID of the workout
     * @return LiveData list of workout history for the workout
     */
    fun getWorkoutHistoryByWorkout(workoutId: Long): LiveData<List<WorkoutHistory>> {
        return workoutHistoryDao.getWorkoutHistoryByWorkout(workoutId)
    }
    
    /**
     * Get workout history by ID
     * @param workoutHistoryId The ID of the workout history
     * @return The workout history with the specified ID, or null if not found
     */
    suspend fun getWorkoutHistoryById(workoutHistoryId: Long): WorkoutHistory? {
        return workoutHistoryDao.getWorkoutHistoryById(workoutHistoryId)
    }
    
    /**
     * Insert a new workout history entry
     * @param workoutHistory The workout history to insert
     * @return The ID of the inserted workout history
     */
    suspend fun insertWorkoutHistory(workoutHistory: WorkoutHistory): Long {
        return workoutHistoryDao.insertWorkoutHistory(workoutHistory)
    }
    
    /**
     * Update an existing workout history entry
     * @param workoutHistory The workout history to update
     */
    suspend fun updateWorkoutHistory(workoutHistory: WorkoutHistory) {
        workoutHistoryDao.updateWorkoutHistory(workoutHistory)
    }
    
    /**
     * Delete a workout history entry
     * @param workoutHistory The workout history to delete
     */
    suspend fun deleteWorkoutHistory(workoutHistory: WorkoutHistory) {
        workoutHistoryDao.deleteWorkoutHistory(workoutHistory)
    }
    
    /**
     * Get workout history for a date range
     * @param userId The ID of the user
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return LiveData list of workout history within the date range
     */
    fun getWorkoutHistoryForDateRange(userId: Long, startDate: Date, endDate: Date): LiveData<List<WorkoutHistory>> {
        return workoutHistoryDao.getWorkoutHistoryForDateRange(userId, startDate, endDate)
    }
    
    /**
     * Get total workouts by type for a user
     * @param userId The ID of the user
     * @return Map of workout type to count
     */
    suspend fun getWorkoutTypeCounts(userId: Long): Map<String, Int> {
        return workoutHistoryDao.getWorkoutTypeCounts(userId)
    }
    
    /**
     * Get the current streak for a user
     * @param userId The ID of the user
     * @return The current streak
     */
    suspend fun getCurrentStreak(userId: Long): Int {
        return workoutHistoryDao.getCurrentStreak(userId)
    }
    
    /**
     * Get total stats for a user in a date range
     * @param userId The ID of the user
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return Map of stat name to value
     */
    suspend fun getTotalStatsForDateRange(userId: Long, startDate: Date, endDate: Date): Map<String, Int?> {
        val calories = workoutHistoryDao.getTotalCaloriesForDateRange(userId, startDate, endDate) ?: 0
        val duration = workoutHistoryDao.getTotalDurationForDateRange(userId, startDate, endDate) ?: 0
        val xp = workoutHistoryDao.getTotalXpForDateRange(userId, startDate, endDate) ?: 0
        val workouts = workoutHistoryDao.getWorkoutCountForDateRange(userId, startDate, endDate)
        
        return mapOf(
            "calories" to calories,
            "duration" to duration,
            "xp" to xp,
            "workouts" to workouts
        )
    }
    
    /**
     * Get weekly stats for a user
     * @param userId The ID of the user
     * @return Map of stat name to value
     */
    suspend fun getWeeklyStats(userId: Long): Map<String, Int?> {
        val calendar = Calendar.getInstance()
        
        // Set to start of the current week (Sunday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val weekStart = calendar.time
        
        // Set to end of the current week (Saturday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val weekEnd = calendar.time
        
        return getTotalStatsForDateRange(userId, weekStart, weekEnd)
    }
    
    /**
     * Get monthly stats for a user
     * @param userId The ID of the user
     * @return Map of stat name to value
     */
    suspend fun getMonthlyStats(userId: Long): Map<String, Int?> {
        val calendar = Calendar.getInstance()
        
        // Set to start of the current month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val monthStart = calendar.time
        
        // Set to end of the current month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val monthEnd = calendar.time
        
        return getTotalStatsForDateRange(userId, monthStart, monthEnd)
    }
}
