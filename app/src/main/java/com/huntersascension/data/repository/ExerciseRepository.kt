package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.ExerciseDao
import com.huntersascension.data.model.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExerciseRepository(private val exerciseDao: ExerciseDao) {
    
    fun getExercisesForWorkout(workoutId: Long): LiveData<List<Exercise>> {
        return exerciseDao.getExercisesForWorkout(workoutId)
    }
    
    fun getExerciseById(exerciseId: Long): LiveData<Exercise> {
        return exerciseDao.getExerciseById(exerciseId)
    }
    
    suspend fun insertExercise(exercise: Exercise): Long {
        return withContext(Dispatchers.IO) {
            exerciseDao.insert(exercise)
        }
    }
    
    suspend fun insertMultipleExercises(exercises: List<Exercise>) {
        withContext(Dispatchers.IO) {
            exerciseDao.insertMultiple(exercises)
        }
    }
    
    suspend fun updateExercise(exercise: Exercise) {
        withContext(Dispatchers.IO) {
            exerciseDao.update(exercise)
        }
    }
    
    suspend fun deleteExercise(exercise: Exercise) {
        withContext(Dispatchers.IO) {
            exerciseDao.delete(exercise)
        }
    }
    
    suspend fun deleteExerciseById(exerciseId: Long) {
        withContext(Dispatchers.IO) {
            exerciseDao.deleteById(exerciseId)
        }
    }
    
    suspend fun markExerciseComplete(exerciseId: Long, completed: Boolean) {
        withContext(Dispatchers.IO) {
            exerciseDao.updateExerciseCompletion(exerciseId, completed)
        }
    }
    
    suspend fun updateExerciseMetrics(exerciseId: Long, sets: Int, reps: Int, weight: Float, time: Long, distance: Float, calories: Int) {
        withContext(Dispatchers.IO) {
            exerciseDao.updateExerciseMetrics(exerciseId, sets, reps, weight, time, distance, calories)
        }
    }
    
    suspend fun getExerciseCount(workoutId: Long): Int {
        return withContext(Dispatchers.IO) {
            exerciseDao.getExerciseCountForWorkout(workoutId)
        }
    }
    
    suspend fun getCompletedExerciseCount(workoutId: Long): Int {
        return withContext(Dispatchers.IO) {
            exerciseDao.getCompletedExerciseCountForWorkout(workoutId)
        }
    }
    
    suspend fun getTotalExperienceForWorkout(workoutId: Long): Int {
        return withContext(Dispatchers.IO) {
            exerciseDao.getTotalExperienceForWorkout(workoutId) ?: 0
        }
    }
    
    suspend fun clearExercisesForWorkout(workoutId: Long) {
        withContext(Dispatchers.IO) {
            exerciseDao.deleteExercisesForWorkout(workoutId)
        }
    }
}
