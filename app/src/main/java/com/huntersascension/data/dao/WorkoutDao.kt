package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Workout
import java.util.Date

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: Workout): Long

    @Update
    suspend fun update(workout: Workout)

    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getWorkoutById(id: Long): LiveData<Workout>

    @Query("SELECT * FROM workouts WHERE username = :username ORDER BY startTime DESC")
    fun getAllWorkoutsForUser(username: String): LiveData<List<Workout>>

    @Query("SELECT * FROM workouts WHERE username = :username AND isCompleted = 1 ORDER BY startTime DESC")
    fun getCompletedWorkoutsForUser(username: String): LiveData<List<Workout>>

    @Query("SELECT COUNT(*) FROM workouts WHERE username = :username AND isCompleted = 1 AND type = :type")
    suspend fun getCompletedWorkoutCountByType(username: String, type: String): Int

    @Query("SELECT SUM(expGained) FROM workouts WHERE username = :username AND isCompleted = 1")
    suspend fun getTotalExpGained(username: String): Int?

    @Query("UPDATE workouts SET isCompleted = 1, endTime = :endTime, completedExercises = :completedExercises, expGained = :expGained " +
           "WHERE id = :workoutId")
    suspend fun completeWorkout(workoutId: Long, endTime: Date, completedExercises: List<String>, expGained: Int)
}
