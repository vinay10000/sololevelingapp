package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.ExerciseDao
import com.huntersascension.data.model.Exercise

/**
 * Repository for exercise data
 */
class ExerciseRepository(private val exerciseDao: ExerciseDao) {
    
    /**
     * Get all exercises
     * @return LiveData list of all exercises
     */
    fun getAllExercises(): LiveData<List<Exercise>> {
        return exerciseDao.getAllExercises()
    }
    
    /**
     * Get an exercise by ID
     * @param exerciseId The ID of the exercise
     * @return The exercise with the specified ID, or null if not found
     */
    suspend fun getExerciseById(exerciseId: Long): Exercise? {
        return exerciseDao.getExerciseById(exerciseId)
    }
    
    /**
     * Insert a new exercise
     * @param exercise The exercise to insert
     * @return The ID of the inserted exercise
     */
    suspend fun insertExercise(exercise: Exercise): Long {
        return exerciseDao.insertExercise(exercise)
    }
    
    /**
     * Update an existing exercise
     * @param exercise The exercise to update
     */
    suspend fun updateExercise(exercise: Exercise) {
        exerciseDao.updateExercise(exercise)
    }
    
    /**
     * Delete an exercise
     * @param exercise The exercise to delete
     */
    suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.deleteExercise(exercise)
    }
    
    /**
     * Get exercises by type
     * @param type The exercise type
     * @return LiveData list of exercises of the specified type
     */
    fun getExercisesByType(type: String): LiveData<List<Exercise>> {
        return exerciseDao.getExercisesByType(type)
    }
    
    /**
     * Get exercises by muscle group
     * @param muscleGroup The muscle group
     * @return LiveData list of exercises for the specified muscle group
     */
    fun getExercisesByMuscleGroup(muscleGroup: String): LiveData<List<Exercise>> {
        return exerciseDao.getExercisesByMuscleGroup(muscleGroup)
    }
    
    /**
     * Get exercises by equipment
     * @param equipment The equipment
     * @return LiveData list of exercises that use the specified equipment
     */
    fun getExercisesByEquipment(equipment: String): LiveData<List<Exercise>> {
        return exerciseDao.getExercisesByEquipment(equipment)
    }
    
    /**
     * Get bodyweight exercises
     * @return LiveData list of bodyweight exercises
     */
    fun getBodyweightExercises(): LiveData<List<Exercise>> {
        return exerciseDao.getBodyweightExercises()
    }
    
    /**
     * Get compound exercises
     * @return LiveData list of compound exercises
     */
    fun getCompoundExercises(): LiveData<List<Exercise>> {
        return exerciseDao.getCompoundExercises()
    }
    
    /**
     * Search for exercises by name
     * @param query The search query
     * @return LiveData list of exercises matching the query
     */
    fun searchExercisesByName(query: String): LiveData<List<Exercise>> {
        return exerciseDao.searchExercisesByName("%$query%")
    }
}
