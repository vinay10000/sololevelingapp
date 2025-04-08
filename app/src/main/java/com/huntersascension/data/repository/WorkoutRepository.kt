package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.WorkoutDao
import com.huntersascension.data.model.Workout
import com.huntersascension.data.model.WorkoutType
import com.huntersascension.data.model.WorkoutDifficulty
import com.huntersascension.data.model.Rank
import com.huntersascension.data.model.Stat

/**
 * Repository for interacting with workout data
 */
class WorkoutRepository(private val workoutDao: WorkoutDao) {
    
    /**
     * Gets a workout by ID
     */
    fun getWorkoutById(id: Long): LiveData<Workout?> {
        return workoutDao.getWorkoutById(id)
    }
    
    /**
     * Gets a workout by ID synchronously
     */
    suspend fun getWorkoutByIdSync(id: Long): Workout? {
        return workoutDao.getWorkoutByIdSync(id)
    }
    
    /**
     * Gets all workouts
     */
    fun getAllWorkouts(): LiveData<List<Workout>> {
        return workoutDao.getAllWorkouts()
    }
    
    /**
     * Gets all public workouts
     */
    fun getPublicWorkouts(): LiveData<List<Workout>> {
        return workoutDao.getPublicWorkouts()
    }
    
    /**
     * Gets workouts available to a specific user
     */
    fun getWorkoutsForUser(username: String): LiveData<List<Workout>> {
        return workoutDao.getWorkoutsForUser(username)
    }
    
    /**
     * Gets workouts created by a specific user
     */
    fun getUserCreatedWorkouts(username: String): LiveData<List<Workout>> {
        return workoutDao.getUserCreatedWorkouts(username)
    }
    
    /**
     * Gets recommended workouts for a user's rank
     */
    fun getRecommendedWorkouts(userRank: Rank): LiveData<List<Workout>> {
        return workoutDao.getRecommendedWorkouts(userRank)
    }
    
    /**
     * Gets workouts of a specific type available to a user's rank
     */
    fun getWorkoutsByType(type: WorkoutType, userRank: Rank): LiveData<List<Workout>> {
        return workoutDao.getWorkoutsByType(type, userRank)
    }
    
    /**
     * Gets workouts of a specific difficulty available to a user's rank
     */
    fun getWorkoutsByDifficulty(difficulty: WorkoutDifficulty, userRank: Rank): LiveData<List<Workout>> {
        return workoutDao.getWorkoutsByDifficulty(difficulty, userRank)
    }
    
    /**
     * Gets workouts that target a specific stat
     */
    fun getWorkoutsByStat(stat: Stat): LiveData<List<Workout>> {
        return workoutDao.getWorkoutsByStat(stat)
    }
    
    /**
     * Creates a new workout
     */
    suspend fun createWorkout(workout: Workout): Long {
        return workoutDao.insertWorkout(workout)
    }
    
    /**
     * Updates an existing workout
     */
    suspend fun updateWorkout(workout: Workout) {
        workoutDao.updateWorkout(workout)
    }
    
    /**
     * Deletes a workout
     */
    suspend fun deleteWorkout(workout: Workout) {
        workoutDao.deleteWorkout(workout)
    }
    
    /**
     * Deletes a workout by ID and creator username
     */
    suspend fun deleteWorkoutByIdAndCreator(id: Long, username: String): Int {
        return workoutDao.deleteWorkoutByIdAndCreator(id, username)
    }
    
    /**
     * Gets the count of workouts created by a user
     */
    suspend fun getUserWorkoutCount(username: String): Int {
        return workoutDao.getUserWorkoutCount(username)
    }
}
