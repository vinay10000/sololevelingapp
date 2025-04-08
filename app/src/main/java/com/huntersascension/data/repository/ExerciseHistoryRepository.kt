package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.ExerciseHistoryDao
import com.huntersascension.data.model.ExerciseHistory

/**
 * Repository for exercise history data
 */
class ExerciseHistoryRepository(private val exerciseHistoryDao: ExerciseHistoryDao) {
    
    /**
     * Get all exercise history entries
     * @return LiveData list of all exercise history entries
     */
    fun getAllExerciseHistory(): LiveData<List<ExerciseHistory>> {
        return exerciseHistoryDao.getAllExerciseHistory()
    }
    
    /**
     * Get exercise history for a workout history
     * @param workoutHistoryId The ID of the workout history
     * @return LiveData list of exercise history for the workout history
     */
    fun getExerciseHistoryForWorkoutHistory(workoutHistoryId: Long): LiveData<List<ExerciseHistory>> {
        return exerciseHistoryDao.getExerciseHistoryForWorkoutHistory(workoutHistoryId)
    }
    
    /**
     * Get exercise history for an exercise
     * @param exerciseId The ID of the exercise
     * @return LiveData list of exercise history for the exercise
     */
    fun getExerciseHistoryForExercise(exerciseId: Long): LiveData<List<ExerciseHistory>> {
        return exerciseHistoryDao.getExerciseHistoryForExercise(exerciseId)
    }
    
    /**
     * Get exercise history by ID
     * @param exerciseHistoryId The ID of the exercise history
     * @return The exercise history with the specified ID, or null if not found
     */
    suspend fun getExerciseHistoryById(exerciseHistoryId: Long): ExerciseHistory? {
        return exerciseHistoryDao.getExerciseHistoryById(exerciseHistoryId)
    }
    
    /**
     * Insert a new exercise history entry
     * @param exerciseHistory The exercise history to insert
     * @return The ID of the inserted exercise history
     */
    suspend fun insertExerciseHistory(exerciseHistory: ExerciseHistory): Long {
        return exerciseHistoryDao.insertExerciseHistory(exerciseHistory)
    }
    
    /**
     * Insert multiple exercise history entries
     * @param exerciseHistories The exercise history entries to insert
     * @return The IDs of the inserted exercise history entries
     */
    suspend fun insertAllExerciseHistories(exerciseHistories: List<ExerciseHistory>): List<Long> {
        return exerciseHistoryDao.insertAllExerciseHistories(exerciseHistories)
    }
    
    /**
     * Update an existing exercise history entry
     * @param exerciseHistory The exercise history to update
     */
    suspend fun updateExerciseHistory(exerciseHistory: ExerciseHistory) {
        exerciseHistoryDao.updateExerciseHistory(exerciseHistory)
    }
    
    /**
     * Delete an exercise history entry
     * @param exerciseHistory The exercise history to delete
     */
    suspend fun deleteExerciseHistory(exerciseHistory: ExerciseHistory) {
        exerciseHistoryDao.deleteExerciseHistory(exerciseHistory)
    }
    
    /**
     * Delete all exercise history entries for a workout history
     * @param workoutHistoryId The ID of the workout history
     */
    suspend fun deleteExerciseHistoryForWorkoutHistory(workoutHistoryId: Long) {
        exerciseHistoryDao.deleteExerciseHistoryForWorkoutHistory(workoutHistoryId)
    }
    
    /**
     * Get exercise stats
     * @param exerciseId The ID of the exercise
     * @return Map of stat name to value
     */
    suspend fun getExerciseStats(exerciseId: Long): Map<String, Any?> {
        val totalXp = exerciseHistoryDao.getTotalXpForExercise(exerciseId) ?: 0
        val totalCalories = exerciseHistoryDao.getTotalCaloriesForExercise(exerciseId) ?: 0
        val totalSets = exerciseHistoryDao.getTotalSetsForExercise(exerciseId) ?: 0
        val maxWeight = exerciseHistoryDao.getMaxWeightForExercise(exerciseId)
        val maxDistance = exerciseHistoryDao.getMaxDistanceForExercise(exerciseId)
        val maxDuration = exerciseHistoryDao.getMaxDurationForExercise(exerciseId)
        
        return mapOf(
            "totalXp" to totalXp,
            "totalCalories" to totalCalories,
            "totalSets" to totalSets,
            "maxWeight" to maxWeight,
            "maxDistance" to maxDistance,
            "maxDuration" to maxDuration
        )
    }
}
