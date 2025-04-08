package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.WorkoutExerciseDao
import com.huntersascension.data.model.WorkoutExercise

/**
 * Repository for workout exercise data
 */
class WorkoutExerciseRepository(private val workoutExerciseDao: WorkoutExerciseDao) {
    
    /**
     * Get all workout exercises
     * @return LiveData list of all workout exercises
     */
    fun getAllWorkoutExercises(): LiveData<List<WorkoutExercise>> {
        return workoutExerciseDao.getAllWorkoutExercises()
    }
    
    /**
     * Get a workout exercise by ID
     * @param workoutExerciseId The ID of the workout exercise
     * @return The workout exercise with the specified ID, or null if not found
     */
    suspend fun getWorkoutExerciseById(workoutExerciseId: Long): WorkoutExercise? {
        return workoutExerciseDao.getWorkoutExerciseById(workoutExerciseId)
    }
    
    /**
     * Get exercises for a workout
     * @param workoutId The ID of the workout
     * @return LiveData list of exercises for the workout
     */
    fun getExercisesForWorkout(workoutId: Long): LiveData<List<WorkoutExercise>> {
        return workoutExerciseDao.getExercisesForWorkout(workoutId)
    }
    
    /**
     * Count the number of exercises in a workout
     * @param workoutId The ID of the workout
     * @return The number of exercises in the workout
     */
    suspend fun countExercisesInWorkout(workoutId: Long): Int {
        return workoutExerciseDao.countExercisesInWorkout(workoutId)
    }
    
    /**
     * Insert a new workout exercise
     * @param workoutExercise The workout exercise to insert
     * @return The ID of the inserted workout exercise
     */
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long {
        return workoutExerciseDao.insertWorkoutExercise(workoutExercise)
    }
    
    /**
     * Update an existing workout exercise
     * @param workoutExercise The workout exercise to update
     */
    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise) {
        workoutExerciseDao.updateWorkoutExercise(workoutExercise)
    }
    
    /**
     * Delete a workout exercise
     * @param workoutExercise The workout exercise to delete
     */
    suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExercise) {
        workoutExerciseDao.deleteWorkoutExercise(workoutExercise)
    }
    
    /**
     * Delete all exercises for a workout
     * @param workoutId The ID of the workout
     */
    suspend fun deleteExercisesForWorkout(workoutId: Long) {
        workoutExerciseDao.deleteExercisesForWorkout(workoutId)
    }
    
    /**
     * Update the order index of a workout exercise
     * @param workoutExerciseId The ID of the workout exercise
     * @param orderIndex The new order index
     */
    suspend fun updateOrderIndex(workoutExerciseId: Long, orderIndex: Int) {
        workoutExerciseDao.updateOrderIndex(workoutExerciseId, orderIndex)
    }
    
    /**
     * Reorder exercises for a workout by moving an exercise from one position to another
     * @param workoutId The ID of the workout
     * @param fromIndex The current index of the exercise
     * @param toIndex The new index for the exercise
     */
    suspend fun reorderExercises(workoutId: Long, fromIndex: Int, toIndex: Int) {
        workoutExerciseDao.reorderExercises(workoutId, fromIndex, toIndex)
    }
}
