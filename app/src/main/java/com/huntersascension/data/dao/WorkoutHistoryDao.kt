package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Workout
import com.huntersascension.data.model.WorkoutHistory
import java.util.*

/**
 * Data Access Object for WorkoutHistory entity
 */
@Dao
interface WorkoutHistoryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutHistory(workoutHistory: WorkoutHistory): Long
    
    @Update
    suspend fun updateWorkoutHistory(workoutHistory: WorkoutHistory)
    
    @Delete
    suspend fun deleteWorkoutHistory(workoutHistory: WorkoutHistory)
    
    @Query("SELECT * FROM workout_history WHERE historyId = :historyId")
    fun getWorkoutHistoryById(historyId: String): LiveData<WorkoutHistory?>
    
    @Query("SELECT * FROM workout_history WHERE historyId = :historyId")
    suspend fun getWorkoutHistoryByIdSync(historyId: String): WorkoutHistory?
    
    @Query("SELECT * FROM workout_history WHERE username = :username ORDER BY startTime DESC")
    fun getWorkoutHistoryForUser(username: String): LiveData<List<WorkoutHistory>>
    
    @Query("SELECT * FROM workout_history WHERE username = :username ORDER BY startTime DESC LIMIT :limit")
    fun getRecentWorkoutHistoryForUser(username: String, limit: Int): LiveData<List<WorkoutHistory>>
    
    @Query("SELECT * FROM workout_history WHERE workoutId = :workoutId AND username = :username ORDER BY startTime DESC")
    fun getHistoryForWorkout(workoutId: String, username: String): LiveData<List<WorkoutHistory>>
    
    @Query("SELECT * FROM workout_history wh JOIN workouts w ON wh.workoutId = w.workoutId WHERE wh.username = :username AND w.type = :workoutType ORDER BY wh.startTime DESC")
    fun getHistoryByWorkoutType(username: String, workoutType: String): LiveData<List<WorkoutHistory>>
    
    @Query("SELECT * FROM workout_history WHERE username = :username AND isCompleted = 1 ORDER BY startTime DESC LIMIT 1")
    suspend fun getLastCompletedWorkout(username: String): WorkoutHistory?
    
    @Query("SELECT SUM(calories) FROM workout_history WHERE username = :username AND DATE(startTime/1000, 'unixepoch') = DATE('now')")
    fun getTodayCaloriesBurned(username: String): LiveData<Int?>
    
    @Query("SELECT COUNT(*) FROM workout_history WHERE username = :username AND DATE(startTime/1000, 'unixepoch') = DATE('now') AND isCompleted = 1")
    fun getTodayWorkoutCount(username: String): LiveData<Int?>
    
    @Query("SELECT SUM(duration)/60 FROM workout_history WHERE username = :username AND DATE(startTime/1000, 'unixepoch') = DATE('now')")
    fun getTodayWorkoutDuration(username: String): LiveData<Int?>
    
    @Query("SELECT SUM(calories) FROM workout_history WHERE username = :username AND isCompleted = 1 AND startTime BETWEEN :startDate AND :endDate")
    fun getCaloriesBurnedBetweenDates(username: String, startDate: Date, endDate: Date): LiveData<Int?>
    
    @Query("SELECT AVG(duration/60) FROM workout_history WHERE username = :username AND isCompleted = 1")
    fun getAvgWorkoutDurationMinutes(username: String): LiveData<Float?>
    
    @Query("UPDATE workout_history SET isCompleted = 1, endTime = :endTime, duration = :duration, calories = :calories, exp = :exp WHERE historyId = :historyId")
    suspend fun completeWorkout(historyId: String, endTime: Date, duration: Int, calories: Int, exp: Int)
    
    @Query("UPDATE workout_history SET strengthGain = :str, enduranceGain = :end, agilityGain = :agi, vitalityGain = :vit, intelligenceGain = :int, luckGain = :luck WHERE historyId = :historyId")
    suspend fun updateStatGains(historyId: String, str: Int, end: Int, agi: Int, vit: Int, int: Int, luck: Int)
    
    @Query("UPDATE workout_history SET rating = :rating, notes = :notes WHERE historyId = :historyId")
    suspend fun updateFeedback(historyId: String, rating: Int, notes: String?)
    
    // Query for workout statistics
    @Query("SELECT strftime('%w', startTime/1000, 'unixepoch') AS dayOfWeek, COUNT(*) AS count FROM workout_history WHERE username = :username AND isCompleted = 1 GROUP BY dayOfWeek ORDER BY dayOfWeek")
    fun getWorkoutCountByDayOfWeek(username: String): LiveData<Map<String, Int>>
    
    @Query("SELECT strftime('%m', startTime/1000, 'unixepoch') AS month, SUM(calories) AS total FROM workout_history WHERE username = :username AND isCompleted = 1 AND startTime BETWEEN :startDate AND :endDate GROUP BY month ORDER BY month")
    fun getCaloriesByMonth(username: String, startDate: Date, endDate: Date): LiveData<Map<String, Int>>
}
