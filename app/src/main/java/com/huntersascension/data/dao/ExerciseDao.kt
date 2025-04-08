package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Exercise
import com.huntersascension.data.model.ExerciseCategory
import com.huntersascension.data.model.MuscleGroup
import com.huntersascension.data.model.Rank

/**
 * Data Access Object for Exercise entity
 */
@Dao
interface ExerciseDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)
    
    @Update
    suspend fun updateExercise(exercise: Exercise)
    
    @Delete
    suspend fun deleteExercise(exercise: Exercise)
    
    @Query("SELECT * FROM exercises WHERE exerciseId = :exerciseId")
    fun getExerciseById(exerciseId: String): LiveData<Exercise?>
    
    @Query("SELECT * FROM exercises WHERE exerciseId = :exerciseId")
    suspend fun getExerciseByIdSync(exerciseId: String): Exercise?
    
    @Query("SELECT * FROM exercises ORDER BY name")
    fun getAllExercises(): LiveData<List<Exercise>>
    
    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY difficulty, name")
    fun getExercisesByCategory(category: ExerciseCategory): LiveData<List<Exercise>>
    
    @Query("SELECT * FROM exercises WHERE primaryMuscle = :muscleGroup OR :muscleGroup IN (secondaryMuscles) ORDER BY difficulty, name")
    fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): LiveData<List<Exercise>>
    
    @Query("SELECT * FROM exercises WHERE requiredRank <= :rank AND isLocked = 0 ORDER BY requiredRank, difficulty, name")
    fun getAvailableExercisesForRank(rank: Rank): LiveData<List<Exercise>>
    
    @Query("SELECT * FROM exercises WHERE difficulty <= :maxDifficulty ORDER BY difficulty, name")
    fun getExercisesByMaxDifficulty(maxDifficulty: Int): LiveData<List<Exercise>>
    
    @Query("SELECT * FROM exercises WHERE isCustom = 1 AND createdBy = :username ORDER BY name")
    fun getCustomExercisesForUser(username: String): LiveData<List<Exercise>>
    
    @Query("SELECT * FROM exercises WHERE requiredRank <= :rank AND isLocked = 0 AND (:hasEquipment = 1 OR equipment = '[]') ORDER BY requiredRank, difficulty, name")
    fun getFilteredExercises(rank: Rank, hasEquipment: Boolean): LiveData<List<Exercise>>
    
    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%' ORDER BY name")
    fun searchExercises(searchQuery: String): LiveData<List<Exercise>>
}
