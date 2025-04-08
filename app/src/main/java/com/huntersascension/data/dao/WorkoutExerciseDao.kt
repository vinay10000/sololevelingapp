package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.WorkoutExercise

/**
 * Data Access Object for WorkoutExercise entities
 */
@Dao
interface WorkoutExerciseDao {
    /**
     * Get all workout exercises
     * @return LiveData list of all workout exercises
     */
    @Query("SELECT * FROM workout_exercises")
    fun getAllWorkoutExercises(): LiveData<List<WorkoutExercise>>
    
    /**
     * Get a workout exercise by ID
     * @param workoutExerciseId The ID of the workout exercise
     * @return The workout exercise with the specified ID, or null if not found
     */
    @Query("SELECT * FROM workout_exercises WHERE id = :workoutExerciseId")
    suspend fun getWorkoutExerciseById(workoutExerciseId: Long): WorkoutExercise?
    
    /**
     * Get exercises for a workout
     * @param workoutId The ID of the workout
     * @return LiveData list of exercises for the workout
     */
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId")
    fun getExercisesForWorkout(workoutId: Long): LiveData<List<WorkoutExercise>>
    
    /**
     * Count the number of exercises in a workout
     * @param workoutId The ID of the workout
     * @return The number of exercises in the workout
     */
    @Query("SELECT COUNT(*) FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun countExercisesInWorkout(workoutId: Long): Int
    
    /**
     * Insert a new workout exercise
     * @param workoutExercise The workout exercise to insert
     * @return The ID of the inserted workout exercise
     */
    @Insert
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long
    
    /**
     * Update an existing workout exercise
     * @param workoutExercise The workout exercise to update
     */
    @Update
    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise)
    
    /**
     * Delete a workout exercise
     * @param workoutExercise The workout exercise to delete
     */
    @Delete
    suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExercise)
    
    /**
     * Delete all exercises for a workout
     * @param workoutId The ID of the workout
     */
    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteExercisesForWorkout(workoutId: Long)
    
    /**
     * Update the order index of a workout exercise
     * @param workoutExerciseId The ID of the workout exercise
     * @param orderIndex The new order index
     */
    @Query("UPDATE workout_exercises SET orderIndex = :orderIndex WHERE id = :workoutExerciseId")
    suspend fun updateOrderIndex(workoutExerciseId: Long, orderIndex: Int)
    
    /**
     * Reorder exercises for a workout by moving an exercise from one position to another
     * @param workoutId The ID of the workout
     * @param fromIndex The current index of the exercise
     * @param toIndex The new index for the exercise
     */
    @Transaction
    suspend fun reorderExercises(workoutId: Long, fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return
        
        // Get all exercises for the workout
        val exercises = getExercisesForWorkout(workoutId).value ?: return
        
        if (fromIndex < 0 || fromIndex >= exercises.size || toIndex < 0 || toIndex >= exercises.size) {
            return
        }
        
        val sortedExercises = exercises.sortedBy { it.orderIndex }
        
        // Update indices
        if (fromIndex < toIndex) {
            // Moving down
            for (i in fromIndex + 1..toIndex) {
                sortedExercises[i].id.let { updateOrderIndex(it, i - 1) }
            }
        } else {
            // Moving up
            for (i in toIndex until fromIndex) {
                sortedExercises[i].id.let { updateOrderIndex(it, i + 1) }
            }
        }
        
        // Update the moved exercise
        sortedExercises[fromIndex].id.let { updateOrderIndex(it, toIndex) }
    }
}
