package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Workout
import com.huntersascension.data.model.WorkoutType
import com.huntersascension.data.model.WorkoutDifficulty
import com.huntersascension.data.model.Rank
import com.huntersascension.data.model.Stat

/**
 * Data Access Object for Workout entity
 */
@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getWorkoutById(id: Long): LiveData<Workout?>
    
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutByIdSync(id: Long): Workout?
    
    @Query("SELECT * FROM workouts ORDER BY id DESC")
    fun getAllWorkouts(): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE isPublic = 1 ORDER BY id DESC")
    fun getPublicWorkouts(): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE createdBy = :username OR isPublic = 1 ORDER BY id DESC")
    fun getWorkoutsForUser(username: String): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE createdBy = :username ORDER BY id DESC")
    fun getUserCreatedWorkouts(username: String): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE isRecommended = 1 AND (requiredRank IS NULL OR requiredRank <= :userRank) ORDER BY id DESC")
    fun getRecommendedWorkouts(userRank: Rank): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE type = :type AND (requiredRank IS NULL OR requiredRank <= :userRank) ORDER BY id DESC")
    fun getWorkoutsByType(type: WorkoutType, userRank: Rank): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE difficulty = :difficulty AND (requiredRank IS NULL OR requiredRank <= :userRank) ORDER BY id DESC")
    fun getWorkoutsByDifficulty(difficulty: WorkoutDifficulty, userRank: Rank): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE primaryStat = :stat OR secondaryStat = :stat ORDER BY id DESC")
    fun getWorkoutsByStat(stat: Stat): LiveData<List<Workout>>
    
    @Insert
    suspend fun insertWorkout(workout: Workout): Long
    
    @Insert
    suspend fun insertWorkouts(workouts: List<Workout>)
    
    @Update
    suspend fun updateWorkout(workout: Workout)
    
    @Delete
    suspend fun deleteWorkout(workout: Workout)
    
    @Query("DELETE FROM workouts WHERE id = :id AND createdBy = :username")
    suspend fun deleteWorkoutByIdAndCreator(id: Long, username: String): Int
    
    @Query("SELECT COUNT(*) FROM workouts WHERE createdBy = :username")
    suspend fun getUserWorkoutCount(username: String): Int
}
