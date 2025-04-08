package com.huntersascension.ui.workout

import android.app.Application
import androidx.lifecycle.*
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.model.*
import com.huntersascension.data.repository.*
import com.huntersascension.ui.adapter.UpcomingExerciseItem
import com.huntersascension.ui.adapter.ExerciseStatus
import com.huntersascension.ui.adapter.WorkoutExerciseWithDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * ViewModel for the workout screens
 */
class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val workoutRepository = WorkoutRepository(database.workoutDao())
    private val exerciseRepository = ExerciseRepository(database.exerciseDao())
    private val workoutExerciseRepository = WorkoutExerciseRepository(database.workoutExerciseDao())
    private val workoutHistoryRepository = WorkoutHistoryRepository(database.workoutHistoryDao())
    private val exerciseHistoryRepository = ExerciseHistoryRepository(database.exerciseHistoryDao())
    private val userRepository = UserRepository(database.userDao())
    
    // LiveData for workouts
    private val _userWorkouts = MutableLiveData<List<Workout>>()
    val userWorkouts: LiveData<List<Workout>> = _userWorkouts
    
    // LiveData for filtered workouts
    private val _filteredWorkouts = MutableLiveData<List<Workout>>()
    val filteredWorkouts: LiveData<List<Workout>> = _filteredWorkouts
    
    // Currently selected filter
    private var currentFilter: String = "All"
    
    // LiveData for workout exercises
    private val _workoutExercises = MutableLiveData<List<WorkoutExerciseWithDetails>>()
    val workoutExercises: LiveData<List<WorkoutExerciseWithDetails>> = _workoutExercises
    
    // LiveData for workout history
    private val _recentWorkouts = MutableLiveData<List<WorkoutHistory>>()
    val recentWorkouts: LiveData<List<WorkoutHistory>> = _recentWorkouts
    
    // LiveData for workout statistics
    private val _workoutStats = MutableLiveData<WorkoutStats>()
    val workoutStats: LiveData<WorkoutStats> = _workoutStats
    
    // LiveData for active workout
    private val _activeWorkout = MutableLiveData<Workout?>()
    val activeWorkout: LiveData<Workout?> = _activeWorkout
    
    // LiveData for exercises in active workout
    private val _activeWorkoutExercises = MutableLiveData<List<UpcomingExerciseItem>>()
    val activeWorkoutExercises: LiveData<List<UpcomingExerciseItem>> = _activeWorkoutExercises
    
    // Current active exercise index
    private val _currentExerciseIndex = MutableLiveData<Int>()
    val currentExerciseIndex: LiveData<Int> = _currentExerciseIndex
    
    // Current set index for active exercise
    private val _currentSetIndex = MutableLiveData<Int>()
    val currentSetIndex: LiveData<Int> = _currentSetIndex
    
    // Workout timer
    private val _workoutDuration = MutableLiveData<Long>()
    val workoutDuration: LiveData<Long> = _workoutDuration
    
    // Rest timer
    private val _restTimeRemaining = MutableLiveData<Int>()
    val restTimeRemaining: LiveData<Int> = _restTimeRemaining
    
    // Is workout paused
    private val _isWorkoutPaused = MutableLiveData<Boolean>()
    val isWorkoutPaused: LiveData<Boolean> = _isWorkoutPaused
    
    // Is rest timer active
    private val _isRestActive = MutableLiveData<Boolean>()
    val isRestActive: LiveData<Boolean> = _isRestActive
    
    // Workout completion results
    private val _workoutResults = MutableLiveData<WorkoutResults>()
    val workoutResults: LiveData<WorkoutResults> = _workoutResults
    
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Error message
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    /**
     * Initialize data
     */
    init {
        _isLoading.value = false
        _errorMessage.value = null
        _isWorkoutPaused.value = false
        _isRestActive.value = false
        _currentExerciseIndex.value = 0
        _currentSetIndex.value = 0
    }
    
    /**
     * Load user workouts
     * @param userId The ID of the user
     */
    fun loadUserWorkouts(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workouts = withContext(Dispatchers.IO) {
                    workoutRepository.getWorkoutsForUser(userId).value ?: emptyList()
                }
                _userWorkouts.value = workouts
                _filteredWorkouts.value = workouts
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error loading workouts: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Filter workouts by type
     * @param filter The filter to apply (All, Strength, Cardio, Flexibility, Hybrid)
     */
    fun filterWorkouts(filter: String) {
        currentFilter = filter
        val workouts = _userWorkouts.value ?: emptyList()
        
        _filteredWorkouts.value = if (filter == "All") {
            workouts
        } else {
            workouts.filter { it.type == filter }
        }
    }
    
    /**
     * Load exercises for a workout
     * @param workoutId The ID of the workout
     */
    fun loadWorkoutExercises(workoutId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workoutExerciseList = withContext(Dispatchers.IO) {
                    workoutExerciseRepository.getExercisesForWorkout(workoutId).value ?: emptyList()
                }
                
                val detailedList = mutableListOf<WorkoutExerciseWithDetails>()
                
                for (workoutExercise in workoutExerciseList) {
                    val exercise = withContext(Dispatchers.IO) {
                        exerciseRepository.getExerciseById(workoutExercise.exerciseId)
                    }
                    
                    exercise?.let {
                        detailedList.add(WorkoutExerciseWithDetails(workoutExercise, it))
                    }
                }
                
                _workoutExercises.value = detailedList.sortedBy { it.workoutExercise.orderIndex }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error loading workout exercises: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load recent workout history
     * @param userId The ID of the user
     * @param limit The maximum number of entries to return
     */
    fun loadRecentWorkoutHistory(userId: Long, limit: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val history = withContext(Dispatchers.IO) {
                    workoutHistoryRepository.getRecentWorkoutHistory(userId, limit).value ?: emptyList()
                }
                _recentWorkouts.value = history
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error loading workout history: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load workout statistics
     * @param userId The ID of the user
     */
    fun loadWorkoutStats(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = withContext(Dispatchers.IO) {
                    userRepository.getUserById(userId)
                }
                
                if (user != null) {
                    val totalWorkouts = user.totalWorkouts
                    val totalDuration = user.totalWorkoutMinutes
                    val totalCalories = user.totalCaloriesBurned
                    val currentStreak = user.currentStreak
                    val bestStreak = user.bestStreak
                    
                    val avgDuration = if (totalWorkouts > 0) {
                        totalDuration / totalWorkouts
                    } else {
                        0
                    }
                    
                    val workoutTypeCounts = withContext(Dispatchers.IO) {
                        workoutHistoryRepository.getWorkoutTypeCounts(userId)
                    }
                    
                    _workoutStats.value = WorkoutStats(
                        totalWorkouts = totalWorkouts,
                        totalDuration = totalDuration,
                        avgDuration = avgDuration,
                        totalCalories = totalCalories,
                        currentStreak = currentStreak,
                        bestStreak = bestStreak,
                        strengthCount = workoutTypeCounts["Strength"] ?: 0,
                        cardioCount = workoutTypeCounts["Cardio"] ?: 0,
                        flexibilityCount = workoutTypeCounts["Flexibility"] ?: 0,
                        hybridCount = workoutTypeCounts["Hybrid"] ?: 0
                    )
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error loading workout stats: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Start a workout
     * @param workoutId The ID of the workout to start
     */
    fun startWorkout(workoutId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workout = withContext(Dispatchers.IO) {
                    workoutRepository.getWorkoutById(workoutId)
                }
                
                if (workout != null) {
                    _activeWorkout.value = workout
                    loadActiveWorkoutExercises(workoutId)
                    
                    // Initialize workout state
                    _currentExerciseIndex.value = 0
                    _currentSetIndex.value = 0
                    _workoutDuration.value = 0
                    _isWorkoutPaused.value = false
                    _isRestActive.value = false
                } else {
                    _errorMessage.value = "Workout not found"
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error starting workout: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load exercises for the active workout
     * @param workoutId The ID of the workout
     */
    private fun loadActiveWorkoutExercises(workoutId: Long) {
        viewModelScope.launch {
            try {
                val workoutExerciseList = withContext(Dispatchers.IO) {
                    workoutExerciseRepository.getExercisesForWorkout(workoutId).value ?: emptyList()
                }
                
                val exerciseItems = mutableListOf<UpcomingExerciseItem>()
                
                for (workoutExercise in workoutExerciseList) {
                    val exercise = withContext(Dispatchers.IO) {
                        exerciseRepository.getExerciseById(workoutExercise.exerciseId)
                    }
                    
                    exercise?.let {
                        exerciseItems.add(
                            UpcomingExerciseItem(
                                exercise = it,
                                workoutExercise = workoutExercise,
                                status = ExerciseStatus.PENDING
                            )
                        )
                    }
                }
                
                // Sort by order index
                val sortedItems = exerciseItems.sortedBy { it.workoutExercise.orderIndex }
                
                // Mark first exercise as current
                if (sortedItems.isNotEmpty()) {
                    val updatedItems = sortedItems.toMutableList()
                    updatedItems[0] = updatedItems[0].copy(status = ExerciseStatus.CURRENT)
                    _activeWorkoutExercises.value = updatedItems
                } else {
                    _activeWorkoutExercises.value = sortedItems
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading active workout exercises: ${e.message}"
            }
        }
    }
    
    /**
     * Complete the current set
     */
    fun completeCurrentSet() {
        val exerciseItems = _activeWorkoutExercises.value ?: return
        val currentIndex = _currentExerciseIndex.value ?: 0
        
        if (currentIndex >= exerciseItems.size) return
        
        val currentExerciseItem = exerciseItems[currentIndex]
        val currentSetIndex = _currentSetIndex.value ?: 0
        val totalSets = currentExerciseItem.workoutExercise.sets
        
        // If we completed all sets for this exercise
        if (currentSetIndex + 1 >= totalSets) {
            // Mark this exercise as completed
            val updatedItems = exerciseItems.toMutableList()
            updatedItems[currentIndex] = updatedItems[currentIndex].copy(status = ExerciseStatus.COMPLETED)
            
            // Move to next exercise
            if (currentIndex + 1 < exerciseItems.size) {
                updatedItems[currentIndex + 1] = updatedItems[currentIndex + 1].copy(status = ExerciseStatus.CURRENT)
                _activeWorkoutExercises.value = updatedItems
                _currentExerciseIndex.value = currentIndex + 1
                _currentSetIndex.value = 0
            } else {
                // All exercises completed
                _activeWorkoutExercises.value = updatedItems
                _currentExerciseIndex.value = currentIndex
                _currentSetIndex.value = currentSetIndex + 1
            }
        } else {
            // Move to next set of current exercise
            _currentSetIndex.value = currentSetIndex + 1
        }
    }
    
    /**
     * Move to the previous set
     */
    fun previousSet() {
        val exerciseItems = _activeWorkoutExercises.value ?: return
        val currentIndex = _currentExerciseIndex.value ?: 0
        val currentSetIndex = _currentSetIndex.value ?: 0
        
        if (currentSetIndex > 0) {
            // Move to previous set of current exercise
            _currentSetIndex.value = currentSetIndex - 1
        } else if (currentIndex > 0) {
            // Move to previous exercise, last set
            val prevExercise = exerciseItems[currentIndex - 1]
            val prevExerciseSets = prevExercise.workoutExercise.sets - 1
            
            val updatedItems = exerciseItems.toMutableList()
            
            // Update exercise statuses
            if (prevExercise.status == ExerciseStatus.COMPLETED) {
                updatedItems[currentIndex - 1] = updatedItems[currentIndex - 1].copy(status = ExerciseStatus.CURRENT)
            }
            
            if (updatedItems[currentIndex].status == ExerciseStatus.CURRENT) {
                updatedItems[currentIndex] = updatedItems[currentIndex].copy(status = ExerciseStatus.PENDING)
            }
            
            _activeWorkoutExercises.value = updatedItems
            _currentExerciseIndex.value = currentIndex - 1
            _currentSetIndex.value = max(0, prevExerciseSets)
        }
    }
    
    /**
     * Move to the next set
     */
    fun nextSet() {
        completeCurrentSet()
    }
    
    /**
     * Toggle workout pause state
     */
    fun togglePause() {
        _isWorkoutPaused.value = !(_isWorkoutPaused.value ?: false)
    }
    
    /**
     * Start or stop rest timer
     * @param restDuration Rest duration in seconds
     */
    fun toggleRest(restDuration: Int = 60) {
        val isCurrentlyResting = _isRestActive.value ?: false
        _isRestActive.value = !isCurrentlyResting
        
        if (!isCurrentlyResting) {
            _restTimeRemaining.value = restDuration
        }
    }
    
    /**
     * Decrement rest timer by one second
     */
    fun decrementRestTimer() {
        val currentTime = _restTimeRemaining.value ?: 0
        if (currentTime > 0) {
            _restTimeRemaining.value = currentTime - 1
        } else {
            _isRestActive.value = false
        }
    }
    
    /**
     * Increment workout duration by one second
     */
    fun incrementWorkoutDuration() {
        val currentDuration = _workoutDuration.value ?: 0
        _workoutDuration.value = currentDuration + 1
    }
    
    /**
     * Complete the active workout
     * @param userId The ID of the user
     */
    fun completeWorkout(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workout = _activeWorkout.value
                val exerciseItems = _activeWorkoutExercises.value
                
                if (workout != null && exerciseItems != null) {
                    // Calculate workout duration in minutes
                    val durationSeconds = _workoutDuration.value ?: 0
                    val durationMinutes = (durationSeconds / 60.0).roundToInt()
                    
                    // Calculate total calories and XP
                    var totalCalories = 0
                    var totalXp = 0
                    
                    // Get completed exercises (both CURRENT and COMPLETED status)
                    val completedExercises = exerciseItems.filter { 
                        it.status == ExerciseStatus.COMPLETED || it.status == ExerciseStatus.CURRENT 
                    }
                    
                    // Calculate calories and XP based on completed exercises
                    for (exerciseItem in completedExercises) {
                        val exercise = exerciseItem.exercise
                        val workoutExercise = exerciseItem.workoutExercise
                        
                        // Calculate calories based on exercise type
                        val exerciseCalories = when (exercise.type) {
                            "Cardio" -> {
                                val duration = workoutExercise.duration ?: 0
                                // Simplified calorie calculation for cardio
                                // In a real app, you'd use more factors like user weight
                                duration * (exercise.caloriesPerUnit ?: 5)
                            }
                            else -> {
                                val sets = workoutExercise.sets
                                val reps = workoutExercise.reps ?: 0
                                // Simplified calorie calculation for strength/flexibility
                                sets * reps * (exercise.caloriesPerUnit ?: 1)
                            }
                        }
                        
                        // Calculate XP based on exercise difficulty and sets
                        val exerciseXp = workoutExercise.sets * exercise.difficulty * (exercise.xpPerSet ?: 5)
                        
                        totalCalories += exerciseCalories
                        totalXp += exerciseXp
                    }
                    
                    // Create workout history entry
                    val workoutHistory = WorkoutHistory(
                        userId = userId,
                        workoutId = workout.id,
                        workoutName = workout.name,
                        workoutType = workout.type,
                        startTime = Date(System.currentTimeMillis() - (durationSeconds * 1000)),
                        endTime = Date(),
                        durationMinutes = durationMinutes,
                        exercisesCompleted = completedExercises.size,
                        caloriesBurned = totalCalories,
                        xpEarned = totalXp,
                        primaryStatGained = 3,  // Example values, in real app would depend on workout type
                        secondaryStatGained = 1
                    )
                    
                    // Insert workout history
                    val workoutHistoryId = withContext(Dispatchers.IO) {
                        workoutHistoryRepository.insertWorkoutHistory(workoutHistory)
                    }
                    
                    // Create exercise history entries for completed exercises
                    val exerciseHistoryEntries = completedExercises.map { exerciseItem ->
                        val exercise = exerciseItem.exercise
                        val workoutExercise = exerciseItem.workoutExercise
                        
                        // Calculate calories and XP for this exercise
                        val exerciseCalories = when (exercise.type) {
                            "Cardio" -> {
                                val duration = workoutExercise.duration ?: 0
                                duration * (exercise.caloriesPerUnit ?: 5)
                            }
                            else -> {
                                val sets = workoutExercise.sets
                                val reps = workoutExercise.reps ?: 0
                                sets * reps * (exercise.caloriesPerUnit ?: 1)
                            }
                        }
                        
                        val exerciseXp = workoutExercise.sets * exercise.difficulty * (exercise.xpPerSet ?: 5)
                        
                        ExerciseHistory(
                            workoutHistoryId = workoutHistoryId,
                            exerciseId = exercise.id,
                            exerciseName = exercise.name,
                            exerciseType = exercise.type,
                            setsCompleted = workoutExercise.sets,
                            repsPerSet = workoutExercise.reps,
                            weightUsed = workoutExercise.weight,
                            wasBodyweight = workoutExercise.isBodyweight,
                            durationMinutes = workoutExercise.duration,
                            distanceKm = workoutExercise.distance,
                            holdTimeSeconds = workoutExercise.holdTime,
                            caloriesBurned = exerciseCalories,
                            xpEarned = exerciseXp
                        )
                    }
                    
                    // Insert exercise history entries
                    withContext(Dispatchers.IO) {
                        exerciseHistoryRepository.insertAllExerciseHistories(exerciseHistoryEntries)
                    }
                    
                    // Update user stats (XP, level, streak, workout stats)
                    // 1. Update XP and check if user leveled up
                    val leveledUp = withContext(Dispatchers.IO) {
                        userRepository.addXp(userId, totalXp)
                    }
                    
                    // 2. Update user's stats based on workout type
                    withContext(Dispatchers.IO) {
                        userRepository.incrementStat(userId, workout.primaryStat, 3)
                        
                        if (workout.secondaryStat != null) {
                            userRepository.incrementStat(userId, workout.secondaryStat, 1)
                        }
                    }
                    
                    // 3. Update user's workout stats
                    withContext(Dispatchers.IO) {
                        userRepository.updateWorkoutStats(
                            userId = userId,
                            workoutsToAdd = 1,
                            caloriesToAdd = totalCalories,
                            minutesToAdd = durationMinutes
                        )
                    }
                    
                    // 4. Update last workout date and streak
                    withContext(Dispatchers.IO) {
                        userRepository.updateLastWorkoutDate(userId)
                        
                        // Calculate new streak
                        val newStreak = workoutHistoryRepository.getCurrentStreak(userId)
                        userRepository.updateStreak(userId, newStreak)
                    }
                    
                    // Get updated user data
                    val updatedUser = withContext(Dispatchers.IO) {
                        userRepository.getUserById(userId)
                    }
                    
                    // Set workout results
                    _workoutResults.value = WorkoutResults(
                        workoutName = workout.name,
                        workoutType = workout.type,
                        durationMinutes = durationMinutes,
                        exercisesCompleted = completedExercises.size,
                        caloriesBurned = totalCalories,
                        xpEarned = totalXp,
                        primaryStatGained = 3,
                        secondaryStatGained = if (workout.secondaryStat != null) 1 else 0,
                        leveledUp = leveledUp,
                        newLevel = updatedUser?.level ?: 0,
                        streakUpdated = true,
                        currentStreak = updatedUser?.currentStreak ?: 0
                    )
                    
                    // Reset active workout
                    _activeWorkout.value = null
                    _activeWorkoutExercises.value = emptyList()
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error completing workout: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Create a new workout
     * @param workout The workout to create
     * @return The ID of the created workout
     */
    fun createWorkout(workout: Workout): LiveData<Long> {
        val result = MutableLiveData<Long>()
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workoutId = withContext(Dispatchers.IO) {
                    workoutRepository.insertWorkout(workout)
                }
                result.value = workoutId
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error creating workout: ${e.message}"
                _isLoading.value = false
                result.value = -1
            }
        }
        
        return result
    }
    
    /**
     * Update a workout
     * @param workout The workout to update
     */
    fun updateWorkout(workout: Workout) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                withContext(Dispatchers.IO) {
                    workoutRepository.updateWorkout(workout)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error updating workout: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Delete a workout
     * @param workout The workout to delete
     */
    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                withContext(Dispatchers.IO) {
                    workoutRepository.deleteWorkout(workout)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting workout: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Add an exercise to a workout
     * @param workoutId The ID of the workout
     * @param exerciseId The ID of the exercise
     * @param workoutExercise The workout exercise parameters
     * @return The ID of the created workout exercise
     */
    fun addExerciseToWorkout(
        workoutId: Long,
        exerciseId: Long,
        workoutExercise: WorkoutExercise
    ): LiveData<Long> {
        val result = MutableLiveData<Long>()
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get the current count of exercises in the workout to determine order index
                val exerciseCount = withContext(Dispatchers.IO) {
                    workoutExerciseRepository.countExercisesInWorkout(workoutId)
                }
                
                // Create a new workout exercise with the correct workout ID, exercise ID, and order index
                val newWorkoutExercise = workoutExercise.copy(
                    workoutId = workoutId,
                    exerciseId = exerciseId,
                    orderIndex = exerciseCount
                )
                
                val workoutExerciseId = withContext(Dispatchers.IO) {
                    workoutExerciseRepository.insertWorkoutExercise(newWorkoutExercise)
                }
                
                result.value = workoutExerciseId
                _isLoading.value = false
                
                // Reload workout exercises
                loadWorkoutExercises(workoutId)
            } catch (e: Exception) {
                _errorMessage.value = "Error adding exercise to workout: ${e.message}"
                _isLoading.value = false
                result.value = -1
            }
        }
        
        return result
    }
    
    /**
     * Update a workout exercise
     * @param workoutExercise The workout exercise to update
     */
    fun updateWorkoutExercise(workoutExercise: WorkoutExercise) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                withContext(Dispatchers.IO) {
                    workoutExerciseRepository.updateWorkoutExercise(workoutExercise)
                }
                
                // Reload workout exercises
                loadWorkoutExercises(workoutExercise.workoutId)
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error updating workout exercise: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Remove an exercise from a workout
     * @param workoutExercise The workout exercise to remove
     */
    fun removeExerciseFromWorkout(workoutExercise: WorkoutExercise) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workoutId = workoutExercise.workoutId
                
                withContext(Dispatchers.IO) {
                    workoutExerciseRepository.deleteWorkoutExercise(workoutExercise)
                    
                    // Reorder remaining exercises
                    val remainingExercises = workoutExerciseRepository.getExercisesForWorkout(workoutId).value
                        ?.sortedBy { it.orderIndex } ?: emptyList()
                    
                    // Update order indices
                    remainingExercises.forEachIndexed { index, exercise ->
                        if (exercise.orderIndex != index) {
                            workoutExerciseRepository.updateOrderIndex(exercise.id, index)
                        }
                    }
                }
                
                // Reload workout exercises
                loadWorkoutExercises(workoutId)
                
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error removing exercise from workout: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

/**
 * Data class for workout statistics
 */
data class WorkoutStats(
    val totalWorkouts: Int = 0,
    val totalDuration: Int = 0,  // In minutes
    val avgDuration: Int = 0,    // In minutes
    val totalCalories: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val strengthCount: Int = 0,
    val cardioCount: Int = 0,
    val flexibilityCount: Int = 0,
    val hybridCount: Int = 0
)

/**
 * Data class for workout completion results
 */
data class WorkoutResults(
    val workoutName: String,
    val workoutType: String,
    val durationMinutes: Int,
    val exercisesCompleted: Int,
    val caloriesBurned: Int,
    val xpEarned: Int,
    val primaryStatGained: Int,
    val secondaryStatGained: Int,
    val leveledUp: Boolean,
    val newLevel: Int,
    val streakUpdated: Boolean,
    val currentStreak: Int,
    val trophiesEarned: List<String> = emptyList()
)
