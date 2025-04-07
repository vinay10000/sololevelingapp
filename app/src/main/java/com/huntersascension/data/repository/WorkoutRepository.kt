package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.WorkoutDao
import com.huntersascension.data.dao.WorkoutExerciseDao
import com.huntersascension.data.model.Workout
import com.huntersascension.data.model.WorkoutExercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Calendar

class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: WorkoutExerciseDao,
    private val userRepository: UserRepository
) {
    
    // Get all workouts for a user
    fun getAllUserWorkouts(userEmail: String): LiveData<List<Workout>> {
        return workoutDao.getAllUserWorkouts(userEmail)
    }
    
    // Get a specific workout by ID
    fun getWorkoutById(workoutId: Long): LiveData<Workout> {
        return workoutDao.getWorkoutById(workoutId)
    }
    
    // Get workouts by type
    fun getWorkoutsByType(userEmail: String, workoutType: String): LiveData<List<Workout>> {
        return workoutDao.getWorkoutsByType(userEmail, workoutType)
    }
    
    // Get recent completed workouts
    fun getRecentCompletedWorkouts(userEmail: String, limit: Int = 10): LiveData<List<Workout>> {
        return workoutDao.getRecentCompletedWorkouts(userEmail, limit)
    }
    
    // Get workouts in a date range
    fun getWorkoutsByDateRange(userEmail: String, startDate: Date, endDate: Date): LiveData<List<Workout>> {
        return workoutDao.getWorkoutsByDateRange(userEmail, startDate, endDate)
    }
    
    // Create a new workout
    suspend fun createWorkout(workout: Workout): Long {
        return withContext(Dispatchers.IO) {
            workoutDao.insert(workout)
        }
    }
    
    // Update an existing workout
    suspend fun updateWorkout(workout: Workout) {
        withContext(Dispatchers.IO) {
            workoutDao.update(workout)
        }
    }
    
    // Delete a workout
    suspend fun deleteWorkout(workoutId: Long) {
        withContext(Dispatchers.IO) {
            // Delete associated exercises first
            exerciseDao.deleteExercisesForWorkout(workoutId)
            // Then delete the workout
            workoutDao.deleteWorkout(workoutId)
        }
    }
    
    // Complete a workout and calculate gains
    suspend fun completeWorkout(
        workoutId: Long,
        userEmail: String,
        duration: Long,
        calories: Int
    ) {
        withContext(Dispatchers.IO) {
            val workout = workoutDao.getWorkoutById(workoutId).value ?: return@withContext
            
            // Calculate stat gains based on workout type and exercises
            val expGained = calculateExperienceGain(workout, duration)
            val strengthGained = calculateStatGain("strength", workout)
            val agilityGained = calculateStatGain("agility", workout)
            val vitalityGained = calculateStatGain("vitality", workout)
            val intelligenceGained = calculateStatGain("intelligence", workout)
            val luckGained = calculateStatGain("luck", workout)
            
            // Update the workout with completion data
            workoutDao.completeWorkout(
                workoutId, 
                Date(), 
                duration,
                calories,
                expGained,
                strengthGained,
                agilityGained,
                vitalityGained,
                intelligenceGained,
                luckGained
            )
            
            // Update user stats
            userRepository.addExperience(userEmail, expGained)
            userRepository.updateStat(userEmail, "strength", strengthGained)
            userRepository.updateStat(userEmail, "agility", agilityGained)
            userRepository.updateStat(userEmail, "vitality", vitalityGained)
            userRepository.updateStat(userEmail, "intelligence", intelligenceGained)
            userRepository.updateStat(userEmail, "luck", luckGained)
            
            // Increment user workout count and update streak
            userRepository.incrementWorkout(userEmail)
        }
    }
    
    // Get all exercises for a workout
    fun getExercisesForWorkout(workoutId: Long): LiveData<List<WorkoutExercise>> {
        return exerciseDao.getExercisesForWorkout(workoutId)
    }
    
    // Add an exercise to a workout
    suspend fun addExerciseToWorkout(exercise: WorkoutExercise) {
        withContext(Dispatchers.IO) {
            exerciseDao.insert(exercise)
        }
    }
    
    // Add multiple exercises to a workout
    suspend fun addExercisesToWorkout(exercises: List<WorkoutExercise>) {
        withContext(Dispatchers.IO) {
            exerciseDao.insertAll(exercises)
        }
    }
    
    // Update an exercise
    suspend fun updateExercise(exercise: WorkoutExercise) {
        withContext(Dispatchers.IO) {
            exerciseDao.update(exercise)
        }
    }
    
    // Mark an exercise as completed
    suspend fun markExerciseCompleted(exerciseId: Long, completed: Boolean) {
        withContext(Dispatchers.IO) {
            exerciseDao.markExerciseCompleted(exerciseId, completed)
        }
    }
    
    // Update exercise details
    suspend fun updateExerciseDetails(
        exerciseId: Long,
        sets: Int,
        reps: Int,
        weight: Double,
        duration: Long,
        distance: Double,
        notes: String
    ) {
        withContext(Dispatchers.IO) {
            exerciseDao.updateExerciseDetails(
                exerciseId,
                sets,
                reps,
                weight,
                duration,
                distance,
                notes
            )
        }
    }
    
    // Delete an exercise
    suspend fun deleteExercise(exerciseId: Long) {
        withContext(Dispatchers.IO) {
            exerciseDao.deleteExercise(exerciseId)
        }
    }
    
    // Get workout statistics
    suspend fun getWorkoutStats(userEmail: String): WorkoutStats {
        return withContext(Dispatchers.IO) {
            val totalDuration = workoutDao.getTotalWorkoutDuration(userEmail) ?: 0
            val totalCalories = workoutDao.getTotalCaloriesBurned(userEmail) ?: 0
            val avgDuration = workoutDao.getAverageWorkoutDuration(userEmail) ?: 0.0
            val strengthWorkouts = workoutDao.getCompletedWorkoutCountByType(userEmail, "strength")
            val cardioWorkouts = workoutDao.getCompletedWorkoutCountByType(userEmail, "cardio")
            val flexibilityWorkouts = workoutDao.getCompletedWorkoutCountByType(userEmail, "flexibility")
            
            WorkoutStats(
                totalDuration,
                totalCalories,
                avgDuration,
                strengthWorkouts,
                cardioWorkouts,
                flexibilityWorkouts
            )
        }
    }
    
    // Helper function to calculate experience gain
    private fun calculateExperienceGain(workout: Workout, duration: Long): Int {
        // Base XP calculation:
        // - Difficulty factor (1-5)
        // - Duration factor (minutes)
        // - Workout type bonus

        val difficultyFactor = workout.difficulty * 10
        val durationMinutes = duration / (1000 * 60) // Convert milliseconds to minutes
        val durationFactor = durationMinutes * 2
        
        val workoutTypeBonus = when (workout.workoutType) {
            "strength" -> 20
            "cardio" -> 15
            "flexibility" -> 10
            "hybrid" -> 25
            else -> 5
        }
        
        // Calculate base XP
        val baseXp = difficultyFactor + durationFactor.toInt() + workoutTypeBonus
        
        // Random bonus (luck factor)
        val randomBonus = (0..10).random()
        
        return baseXp + randomBonus
    }
    
    // Helper function to calculate stat gains
    private fun calculateStatGain(statName: String, workout: Workout): Int {
        // Base gain calculation:
        // - Is it primary stat for this workout?
        // - Is it secondary stat for this workout?
        // - Workout difficulty
        
        val isPrimary = workout.mainStat == statName
        val isSecondary = workout.secondaryStat == statName
        
        return when {
            isPrimary -> (1..workout.difficulty).random()
            isSecondary -> if ((0..2).random() > 0) 1 else 0
            else -> 0
        }
    }
    
    // Data class to hold workout statistics
    data class WorkoutStats(
        val totalDuration: Long,
        val totalCaloriesBurned: Int,
        val averageWorkoutDuration: Double,
        val strengthWorkoutCount: Int,
        val cardioWorkoutCount: Int,
        val flexibilityWorkoutCount: Int
    )
}
