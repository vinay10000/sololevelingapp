package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.WorkoutDao
import com.huntersascension.data.model.Workout
import java.util.Date

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    
    fun getWorkoutById(id: Long): LiveData<Workout> {
        return workoutDao.getWorkoutById(id)
    }
    
    fun getAllWorkoutsForUser(username: String): LiveData<List<Workout>> {
        return workoutDao.getAllWorkoutsForUser(username)
    }
    
    fun getCompletedWorkoutsForUser(username: String): LiveData<List<Workout>> {
        return workoutDao.getCompletedWorkoutsForUser(username)
    }
    
    suspend fun insert(workout: Workout): Long {
        return workoutDao.insert(workout)
    }
    
    suspend fun update(workout: Workout) {
        workoutDao.update(workout)
    }
    
    suspend fun getCompletedWorkoutCountByType(username: String, type: String): Int {
        return workoutDao.getCompletedWorkoutCountByType(username, type)
    }
    
    suspend fun getTotalExpGained(username: String): Int {
        return workoutDao.getTotalExpGained(username) ?: 0
    }
    
    suspend fun completeWorkout(workoutId: Long, endTime: Date, completedExercises: List<String>, expGained: Int) {
        workoutDao.completeWorkout(workoutId, endTime, completedExercises, expGained)
    }
}
