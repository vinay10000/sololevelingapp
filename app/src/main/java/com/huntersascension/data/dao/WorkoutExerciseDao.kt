package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.WorkoutExercise

@Dao
interface WorkoutExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: WorkoutExercise)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<WorkoutExercise>)
    
    @Update
    suspend fun update(exercise: WorkoutExercise)
    
    @Query("DELETE FROM workout_exercises WHERE id = :exerciseId")
    suspend fun deleteExercise(exerciseId: Long)
    
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY `order`")
    fun getExercisesForWorkout(workoutId: Long): LiveData<List<WorkoutExercise>>
    
    @Query("UPDATE workout_exercises SET completed = :completed WHERE id = :exerciseId")
    suspend fun markExerciseCompleted(exerciseId: Long, completed: Boolean)
    
    @Query("UPDATE workout_exercises SET sets = :sets, reps = :reps, weight = :weight, duration = :duration, distance = :distance, notes = :notes WHERE id = :exerciseId")
    suspend fun updateExerciseDetails(exerciseId: Long, sets: Int, reps: Int, weight: Double, duration: Long, distance: Double, notes: String)
    
    @Query("SELECT COUNT(*) FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun getExerciseCount(workoutId: Long): Int
    
    @Query("SELECT COUNT(*) FROM workout_exercises WHERE workoutId = :workoutId AND completed = 1")
    suspend fun getCompletedExerciseCount(workoutId: Long): Int
    
    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteExercisesForWorkout(workoutId: Long)
}
