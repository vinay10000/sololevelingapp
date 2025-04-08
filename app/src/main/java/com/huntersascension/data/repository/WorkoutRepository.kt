package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.WorkoutDao
import com.huntersascension.data.model.Workout

/**
 * Repository for workout data
 */
class WorkoutRepository(private val workoutDao: WorkoutDao) {
    
    /**
     * Get all workouts
     * @return LiveData list of all workouts
     */
    fun getAllWorkouts(): LiveData<List<Workout>> {
        return workoutDao.getAllWorkouts()
    }
    
    /**
     * Get workouts for a specific user
     * @param userId The ID of the user
     * @return LiveData list of workouts for the user
     */
    fun getWorkoutsForUser(userId: Long): LiveData<List<Workout>> {
        return workoutDao.getWorkoutsForUser(userId)
    }
    
    /**
     * Get favorite workouts for a user
     * @param userId The ID of the user
     * @return LiveData list of favorite workouts for the user
     */
    fun getFavoriteWorkoutsForUser(userId: Long): LiveData<List<Workout>> {
        return workoutDao.getFavoriteWorkoutsForUser(userId)
    }
    
    /**
     * Get a workout by ID
     * @param workoutId The ID of the workout
     * @return The workout with the specified ID, or null if not found
     */
    suspend fun getWorkoutById(workoutId: Long): Workout? {
        return workoutDao.getWorkoutById(workoutId)
    }
    
    /**
     * Insert a new workout
     * @param workout The workout to insert
     * @return The ID of the inserted workout
     */
    suspend fun insertWorkout(workout: Workout): Long {
        return workoutDao.insertWorkout(workout)
    }
    
    /**
     * Update an existing workout
     * @param workout The workout to update
     */
    suspend fun updateWorkout(workout: Workout) {
        workoutDao.updateWorkout(workout)
    }
    
    /**
     * Delete a workout
     * @param workout The workout to delete
     */
    suspend fun deleteWorkout(workout: Workout) {
        workoutDao.deleteWorkout(workout)
    }
    
    /**
     * Toggle favorite status for a workout
     * @param workoutId The ID of the workout
     * @param isFavorite The new favorite status
     */
    suspend fun setFavoriteStatus(workoutId: Long, isFavorite: Boolean) {
        workoutDao.setFavoriteStatus(workoutId, isFavorite)
    }
    
    /**
     * Get workouts by type
     * @param type The workout type
     * @return LiveData list of workouts of the specified type
     */
    fun getWorkoutsByType(type: String): LiveData<List<Workout>> {
        return workoutDao.getWorkoutsByType(type)
    }
    
    /**
     * Search for workouts by name
     * @param query The search query
     * @return LiveData list of workouts matching the query
     */
    fun searchWorkoutsByName(query: String): LiveData<List<Workout>> {
        return workoutDao.searchWorkoutsByName("%$query%")
    }
    
    /**
     * Get recent workouts for a user
     * @param userId The ID of the user
     * @param limit The maximum number of workouts to return
     * @return LiveData list of recent workouts for the user
     */
    fun getRecentWorkoutsForUser(userId: Long, limit: Int): LiveData<List<Workout>> {
        return workoutDao.getRecentWorkoutsForUser(userId, limit)
    }
}
