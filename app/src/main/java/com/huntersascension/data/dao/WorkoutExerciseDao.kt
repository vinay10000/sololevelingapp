package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Exercise
import com.huntersascension.data.model.WorkoutExercise

/**
 * Data Access Object for WorkoutExercise junction entity
 */
@Dao
interface WorkoutExerciseDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercises(workoutExercises: List<WorkoutExercise>)
    
    @Update
    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise)
    
    @Delete
    suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExercise)
    
    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteAllExercisesForWorkout(workoutId: String)
    
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex")
    fun getExercisesForWorkout(workoutId: String): LiveData<List<WorkoutExercise>>
    
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex")
    suspend fun getExercisesForWorkoutSync(workoutId: String): List<WorkoutExercise>
    
    @Query("SELECT COUNT(*) FROM workout_exercises WHERE workoutId = :workoutId")
    fun getExerciseCountForWorkout(workoutId: String): LiveData<Int>
    
    @Query("SELECT COUNT(*) FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun getExerciseCountForWorkoutSync(workoutId: String): Int
    
    @Query("SELECT e.* FROM exercises e JOIN workout_exercises we ON e.exerciseId = we.exerciseId WHERE we.workoutId = :workoutId ORDER BY we.orderIndex")
    fun getExerciseDetailsForWorkout(workoutId: String): LiveData<List<Exercise>>
    
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId AND exerciseId = :exerciseId")
    fun getWorkoutExercise(workoutId: String, exerciseId: String): LiveData<WorkoutExercise?>
    
    @Query("SELECT EXISTS(SELECT 1 FROM workout_exercises WHERE workoutId = :workoutId AND exerciseId = :exerciseId)")
    suspend fun hasExercise(workoutId: String, exerciseId: String): Boolean
}
