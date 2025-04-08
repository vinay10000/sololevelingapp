package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.ExerciseHistory

/**
 * Data Access Object for ExerciseHistory entity
 */
@Dao
interface ExerciseHistoryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseHistory(exerciseHistory: ExerciseHistory)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseHistoryList(exerciseHistoryList: List<ExerciseHistory>)
    
    @Update
    suspend fun updateExerciseHistory(exerciseHistory: ExerciseHistory)
    
    @Delete
    suspend fun deleteExerciseHistory(exerciseHistory: ExerciseHistory)
    
    @Query("SELECT * FROM exercise_history WHERE historyId = :historyId ORDER BY exerciseId, setNumber")
    fun getExerciseHistoryForWorkout(historyId: String): LiveData<List<ExerciseHistory>>
    
    @Query("SELECT * FROM exercise_history WHERE historyId = :historyId AND exerciseId = :exerciseId ORDER BY setNumber")
    fun getExerciseHistoryForExercise(historyId: String, exerciseId: String): LiveData<List<ExerciseHistory>>
    
    @Query("UPDATE exercise_history SET reps = :reps, weight = :weight, duration = :duration, distance = :distance, isCompleted = 1 WHERE historyId = :historyId AND exerciseId = :exerciseId AND setNumber = :setNumber")
    suspend fun updateExerciseSet(historyId: String, exerciseId: String, setNumber: Int, reps: Int?, weight: Float?, duration: Int?, distance: Float?)
    
    @Query("SELECT COUNT(*) FROM exercise_history WHERE historyId = :historyId AND isCompleted = 1")
    suspend fun getCompletedExerciseCount(historyId: String): Int
    
    @Query("SELECT COUNT(*) FROM exercise_history WHERE historyId = :historyId")
    suspend fun getTotalExerciseCount(historyId: String): Int
    
    @Query("DELETE FROM exercise_history WHERE historyId = :historyId")
    suspend fun deleteExerciseHistoryForWorkout(historyId: String)
    
    // Queries for progress tracking
    @Query("SELECT MAX(weight) FROM exercise_history WHERE exerciseId = :exerciseId AND weight IS NOT NULL AND isCompleted = 1")
    fun getMaxWeightForExercise(exerciseId: String): LiveData<Float?>
    
    @Query("SELECT MAX(reps) FROM exercise_history WHERE exerciseId = :exerciseId AND reps IS NOT NULL AND isCompleted = 1")
    fun getMaxRepsForExercise(exerciseId: String): LiveData<Int?>
    
    @Query("SELECT MIN(duration) FROM exercise_history WHERE exerciseId = :exerciseId AND duration IS NOT NULL AND isCompleted = 1")
    fun getMinDurationForExercise(exerciseId: String): LiveData<Int?>
    
    @Query("SELECT MAX(distance) FROM exercise_history WHERE exerciseId = :exerciseId AND distance IS NOT NULL AND isCompleted = 1")
    fun getMaxDistanceForExercise(exerciseId: String): LiveData<Float?>
}
