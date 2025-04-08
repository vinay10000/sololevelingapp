package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Rank
import com.huntersascension.data.model.Workout
import com.huntersascension.data.model.WorkoutDifficulty
import com.huntersascension.data.model.WorkoutType

/**
 * Data Access Object for Workout entity
 */
@Dao
interface WorkoutDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(workouts: List<Workout>)
    
    @Update
    suspend fun updateWorkout(workout: Workout)
    
    @Delete
    suspend fun deleteWorkout(workout: Workout)
    
    @Query("SELECT * FROM workouts WHERE workoutId = :workoutId")
    fun getWorkoutById(workoutId: String): LiveData<Workout?>
    
    @Query("SELECT * FROM workouts WHERE workoutId = :workoutId")
    suspend fun getWorkoutByIdSync(workoutId: String): Workout?
    
    @Query("SELECT * FROM workouts WHERE createdBy = :username OR isPublic = 1 ORDER BY name")
    fun getWorkoutsByUser(username: String): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE (createdBy = :username OR isPublic = 1) AND isFavorite = 1 ORDER BY name")
    fun getFavoriteWorkouts(username: String): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE type = :type AND (createdBy = :username OR isPublic = 1) ORDER BY difficulty, name")
    fun getWorkoutsByType(type: WorkoutType, username: String): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE difficulty = :difficulty AND (createdBy = :username OR isPublic = 1) ORDER BY name")
    fun getWorkoutsByDifficulty(difficulty: WorkoutDifficulty, username: String): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE requiredRank <= :rank AND (createdBy = :username OR isPublic = 1) ORDER BY requiredRank, difficulty, name")
    fun getAvailableWorkoutsForRank(rank: Rank, username: String): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE isRecommended = 1 AND requiredRank <= :rank AND (createdBy = :username OR isPublic = 1) ORDER BY requiredRank, difficulty")
    fun getRecommendedWorkouts(rank: Rank, username: String): LiveData<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE name LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%' AND (createdBy = :username OR isPublic = 1) ORDER BY name")
    fun searchWorkouts(searchQuery: String, username: String): LiveData<List<Workout>>
    
    @Query("UPDATE workouts SET isFavorite = :isFavorite WHERE workoutId = :workoutId")
    suspend fun setFavorite(workoutId: String, isFavorite: Boolean)
}
