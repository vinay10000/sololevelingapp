package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Workout
import java.util.Date

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: Workout): Long
    
    @Update
    suspend fun update(workout: Workout)
    
    @Query("DELETE FROM workouts WHERE id = :workoutId")
    suspend fun deleteWorkout(workoutId: Long)
    
    @Query("SELECT * FROM workouts WHERE userEmail = :userEmail ORDER BY startTime DESC")
    fun getAllUserWorkouts(userEmail: String): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun getWorkoutById(workoutId: Long): LiveData<Workout>
    
    @Query("SELECT * FROM workouts WHERE userEmail = :userEmail AND workoutType = :workoutType ORDER BY startTime DESC")
    fun getWorkoutsByType(userEmail: String, workoutType: String): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE userEmail = :userEmail AND completed = 1 ORDER BY startTime DESC LIMIT :limit")
    fun getRecentCompletedWorkouts(userEmail: String, limit: Int): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE userEmail = :userEmail AND startTime BETWEEN :startDate AND :endDate ORDER BY startTime DESC")
    fun getWorkoutsByDateRange(userEmail: String, startDate: Date, endDate: Date): LiveData<List<Workout>>
    
    @Query("UPDATE workouts SET completed = 1, endTime = :endTime, duration = :duration, caloriesBurned = :calories, experienceGained = :experienceGained, strengthGained = :strengthGained, agilityGained = :agilityGained, vitalityGained = :vitalityGained, intelligenceGained = :intelligenceGained, luckGained = :luckGained WHERE id = :workoutId")
    suspend fun completeWorkout(workoutId: Long, endTime: Date, duration: Long, calories: Int, experienceGained: Int, strengthGained: Int, agilityGained: Int, vitalityGained: Int, intelligenceGained: Int, luckGained: Int)
    
    @Query("SELECT SUM(duration) FROM workouts WHERE userEmail = :userEmail AND completed = 1")
    suspend fun getTotalWorkoutDuration(userEmail: String): Long?
    
    @Query("SELECT SUM(caloriesBurned) FROM workouts WHERE userEmail = :userEmail AND completed = 1")
    suspend fun getTotalCaloriesBurned(userEmail: String): Int?
    
    @Query("SELECT COUNT(*) FROM workouts WHERE userEmail = :userEmail AND completed = 1 AND workoutType = :workoutType")
    suspend fun getCompletedWorkoutCountByType(userEmail: String, workoutType: String): Int
    
    @Query("SELECT AVG(duration) FROM workouts WHERE userEmail = :userEmail AND completed = 1")
    suspend fun getAverageWorkoutDuration(userEmail: String): Double?
}
