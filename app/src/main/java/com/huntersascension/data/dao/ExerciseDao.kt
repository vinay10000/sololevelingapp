package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Exercise

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: Exercise): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiple(exercises: List<Exercise>)
    
    @Update
    suspend fun update(exercise: Exercise)
    
    @Delete
    suspend fun delete(exercise: Exercise)
    
    @Query("DELETE FROM exercises WHERE id = :exerciseId")
    suspend fun deleteById(exerciseId: Long)
    
    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY id ASC")
    fun getExercisesForWorkout(workoutId: Long): LiveData<List<Exercise>>
    
    @Query("SELECT * FROM exercises WHERE id = :exerciseId")
    fun getExerciseById(exerciseId: Long): LiveData<Exercise>
    
    @Query("UPDATE exercises SET completed = :completed WHERE id = :exerciseId")
    suspend fun updateExerciseCompletion(exerciseId: Long, completed: Boolean)
    
    @Query("UPDATE exercises SET sets = :sets, reps = :reps, weight = :weight, time = :time, distance = :distance, caloriesBurned = :calories WHERE id = :exerciseId")
    suspend fun updateExerciseMetrics(exerciseId: Long, sets: Int, reps: Int, weight: Float, time: Long, distance: Float, calories: Int)
    
    @Query("SELECT COUNT(*) FROM exercises WHERE workoutId = :workoutId")
    suspend fun getExerciseCountForWorkout(workoutId: Long): Int
    
    @Query("SELECT COUNT(*) FROM exercises WHERE workoutId = :workoutId AND completed = 1")
    suspend fun getCompletedExerciseCountForWorkout(workoutId: Long): Int
    
    @Query("SELECT SUM(experienceValue) FROM exercises WHERE workoutId = :workoutId AND completed = 1")
    suspend fun getTotalExperienceForWorkout(workoutId: Long): Int?
    
    @Query("DELETE FROM exercises WHERE workoutId = :workoutId")
    suspend fun deleteExercisesForWorkout(workoutId: Long)
}
