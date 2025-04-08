package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.ExerciseHistory

/**
 * Data Access Object for ExerciseHistory entities
 */
@Dao
interface ExerciseHistoryDao {
    /**
     * Get all exercise history entries
     * @return LiveData list of all exercise history entries
     */
    @Query("SELECT * FROM exercise_history")
    fun getAllExerciseHistory(): LiveData<List<ExerciseHistory>>
    
    /**
     * Get exercise history for a workout history
     * @param workoutHistoryId The ID of the workout history
     * @return LiveData list of exercise history for the workout history
     */
    @Query("SELECT * FROM exercise_history WHERE workoutHistoryId = :workoutHistoryId")
    fun getExerciseHistoryForWorkoutHistory(workoutHistoryId: Long): LiveData<List<ExerciseHistory>>
    
    /**
     * Get exercise history for an exercise
     * @param exerciseId The ID of the exercise
     * @return LiveData list of exercise history for the exercise
     */
    @Query("SELECT * FROM exercise_history WHERE exerciseId = :exerciseId ORDER BY id DESC")
    fun getExerciseHistoryForExercise(exerciseId: Long): LiveData<List<ExerciseHistory>>
    
    /**
     * Get exercise history by ID
     * @param exerciseHistoryId The ID of the exercise history
     * @return The exercise history with the specified ID, or null if not found
     */
    @Query("SELECT * FROM exercise_history WHERE id = :exerciseHistoryId")
    suspend fun getExerciseHistoryById(exerciseHistoryId: Long): ExerciseHistory?
    
    /**
     * Insert a new exercise history entry
     * @param exerciseHistory The exercise history to insert
     * @return The ID of the inserted exercise history
     */
    @Insert
    suspend fun insertExerciseHistory(exerciseHistory: ExerciseHistory): Long
    
    /**
     * Insert multiple exercise history entries
     * @param exerciseHistories The exercise history entries to insert
     * @return The IDs of the inserted exercise history entries
     */
    @Insert
    suspend fun insertAllExerciseHistories(exerciseHistories: List<ExerciseHistory>): List<Long>
    
    /**
     * Update an existing exercise history entry
     * @param exerciseHistory The exercise history to update
     */
    @Update
    suspend fun updateExerciseHistory(exerciseHistory: ExerciseHistory)
    
    /**
     * Delete an exercise history entry
     * @param exerciseHistory The exercise history to delete
     */
    @Delete
    suspend fun deleteExerciseHistory(exerciseHistory: ExerciseHistory)
    
    /**
     * Delete all exercise history entries for a workout history
     * @param workoutHistoryId The ID of the workout history
     */
    @Query("DELETE FROM exercise_history WHERE workoutHistoryId = :workoutHistoryId")
    suspend fun deleteExerciseHistoryForWorkoutHistory(workoutHistoryId: Long)
    
    /**
     * Get total XP earned for an exercise
     * @param exerciseId The ID of the exercise
     * @return Total XP earned
     */
    @Query("SELECT SUM(xpEarned) FROM exercise_history WHERE exerciseId = :exerciseId")
    suspend fun getTotalXpForExercise(exerciseId: Long): Int?
    
    /**
     * Get total calories burned for an exercise
     * @param exerciseId The ID of the exercise
     * @return Total calories burned
     */
    @Query("SELECT SUM(caloriesBurned) FROM exercise_history WHERE exerciseId = :exerciseId")
    suspend fun getTotalCaloriesForExercise(exerciseId: Long): Int?
    
    /**
     * Get total sets completed for an exercise
     * @param exerciseId The ID of the exercise
     * @return Total sets completed
     */
    @Query("SELECT SUM(setsCompleted) FROM exercise_history WHERE exerciseId = :exerciseId")
    suspend fun getTotalSetsForExercise(exerciseId: Long): Int?
    
    /**
     * Get max weight used for an exercise
     * @param exerciseId The ID of the exercise
     * @return Max weight used
     */
    @Query("SELECT MAX(weightUsed) FROM exercise_history WHERE exerciseId = :exerciseId AND weightUsed IS NOT NULL")
    suspend fun getMaxWeightForExercise(exerciseId: Long): Float?
    
    /**
     * Get max distance for an exercise
     * @param exerciseId The ID of the exercise
     * @return Max distance
     */
    @Query("SELECT MAX(distanceKm) FROM exercise_history WHERE exerciseId = :exerciseId AND distanceKm IS NOT NULL")
    suspend fun getMaxDistanceForExercise(exerciseId: Long): Float?
    
    /**
     * Get max duration for an exercise
     * @param exerciseId The ID of the exercise
     * @return Max duration
     */
    @Query("SELECT MAX(durationMinutes) FROM exercise_history WHERE exerciseId = :exerciseId AND durationMinutes IS NOT NULL")
    suspend fun getMaxDurationForExercise(exerciseId: Long): Int?
}
