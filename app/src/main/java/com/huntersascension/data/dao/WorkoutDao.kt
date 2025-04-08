package com.huntersascension.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.Workout

/**
 * Data Access Object for Workout entities
 */
@Dao
interface WorkoutDao {
    /**
     * Get all workouts
     * @return LiveData list of all workouts
     */
    @Query("SELECT * FROM workouts ORDER BY name ASC")
    fun getAllWorkouts(): LiveData<List<Workout>>
    
    /**
     * Get workouts for a specific user
     * @param userId The ID of the user
     * @return LiveData list of workouts for the user
     */
    @Query("SELECT * FROM workouts WHERE userId = :userId ORDER BY name ASC")
    fun getWorkoutsForUser(userId: Long): LiveData<List<Workout>>
    
    /**
     * Get favorite workouts for a user
     * @param userId The ID of the user
     * @return LiveData list of favorite workouts for the user
     */
    @Query("SELECT * FROM workouts WHERE userId = :userId AND isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteWorkoutsForUser(userId: Long): LiveData<List<Workout>>
    
    /**
     * Get a workout by ID
     * @param workoutId The ID of the workout
     * @return The workout with the specified ID, or null if not found
     */
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutById(workoutId: Long): Workout?
    
    /**
     * Insert a new workout
     * @param workout The workout to insert
     * @return The ID of the inserted workout
     */
    @Insert
    suspend fun insertWorkout(workout: Workout): Long
    
    /**
     * Update an existing workout
     * @param workout The workout to update
     */
    @Update
    suspend fun updateWorkout(workout: Workout)
    
    /**
     * Delete a workout
     * @param workout The workout to delete
     */
    @Delete
    suspend fun deleteWorkout(workout: Workout)
    
    /**
     * Toggle favorite status for a workout
     * @param workoutId The ID of the workout
     * @param isFavorite The new favorite status
     */
    @Query("UPDATE workouts SET isFavorite = :isFavorite WHERE id = :workoutId")
    suspend fun setFavoriteStatus(workoutId: Long, isFavorite: Boolean)
    
    /**
     * Get workouts by type
     * @param type The workout type
     * @return LiveData list of workouts of the specified type
     */
    @Query("SELECT * FROM workouts WHERE type = :type ORDER BY name ASC")
    fun getWorkoutsByType(type: String): LiveData<List<Workout>>
    
    /**
     * Search for workouts by name
     * @param query The search query
     * @return LiveData list of workouts matching the query
     */
    @Query("SELECT * FROM workouts WHERE name LIKE :query ORDER BY name ASC")
    fun searchWorkoutsByName(query: String): LiveData<List<Workout>>
    
    /**
     * Get recent workouts for a user
     * @param userId The ID of the user
     * @param limit The maximum number of workouts to return
     * @return LiveData list of recent workouts for the user
     */
    @Query("SELECT * FROM workouts WHERE userId = :userId ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentWorkoutsForUser(userId: Long, limit: Int): LiveData<List<Workout>>
}
