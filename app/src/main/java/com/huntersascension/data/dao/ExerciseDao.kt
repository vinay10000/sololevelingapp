package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Exercise

/**
 * Data Access Object for Exercise entities
 */
@Dao
interface ExerciseDao {
    /**
     * Get all exercises
     * @return LiveData list of all exercises
     */
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): LiveData<List<Exercise>>
    
    /**
     * Get an exercise by ID
     * @param exerciseId The ID of the exercise
     * @return The exercise with the specified ID, or null if not found
     */
    @Query("SELECT * FROM exercises WHERE id = :exerciseId")
    suspend fun getExerciseById(exerciseId: Long): Exercise?
    
    /**
     * Insert a new exercise
     * @param exercise The exercise to insert
     * @return The ID of the inserted exercise
     */
    @Insert
    suspend fun insertExercise(exercise: Exercise): Long
    
    /**
     * Update an existing exercise
     * @param exercise The exercise to update
     */
    @Update
    suspend fun updateExercise(exercise: Exercise)
    
    /**
     * Delete an exercise
     * @param exercise The exercise to delete
     */
    @Delete
    suspend fun deleteExercise(exercise: Exercise)
    
    /**
     * Get exercises by type
     * @param type The exercise type
     * @return LiveData list of exercises of the specified type
     */
    @Query("SELECT * FROM exercises WHERE type = :type ORDER BY name ASC")
    fun getExercisesByType(type: String): LiveData<List<Exercise>>
    
    /**
     * Get exercises by muscle group
     * @param muscleGroup The muscle group
     * @return LiveData list of exercises for the specified muscle group
     */
    @Query("SELECT * FROM exercises WHERE muscleGroup = :muscleGroup ORDER BY name ASC")
    fun getExercisesByMuscleGroup(muscleGroup: String): LiveData<List<Exercise>>
    
    /**
     * Get exercises by equipment
     * @param equipment The equipment
     * @return LiveData list of exercises that use the specified equipment
     */
    @Query("SELECT * FROM exercises WHERE equipment = :equipment ORDER BY name ASC")
    fun getExercisesByEquipment(equipment: String): LiveData<List<Exercise>>
    
    /**
     * Get bodyweight exercises
     * @return LiveData list of bodyweight exercises
     */
    @Query("SELECT * FROM exercises WHERE isBodyweight = 1 ORDER BY name ASC")
    fun getBodyweightExercises(): LiveData<List<Exercise>>
    
    /**
     * Get compound exercises
     * @return LiveData list of compound exercises
     */
    @Query("SELECT * FROM exercises WHERE isCompound = 1 ORDER BY name ASC")
    fun getCompoundExercises(): LiveData<List<Exercise>>
    
    /**
     * Search for exercises by name
     * @param query The search query
     * @return LiveData list of exercises matching the query
     */
    @Query("SELECT * FROM exercises WHERE name LIKE :query ORDER BY name ASC")
    fun searchExercisesByName(query: String): LiveData<List<Exercise>>
}
