package com.huntersascension.viewmodel

import androidx.lifecycle.*
import com.huntersascension.data.model.Workout
import com.huntersascension.data.model.WorkoutExercise
import com.huntersascension.data.repository.WorkoutRepository
import kotlinx.coroutines.launch
import java.util.Date

class WorkoutViewModel(
    private val repository: WorkoutRepository,
    private val userEmail: String
) : ViewModel() {
    
    // LiveData for all user workouts
    val userWorkouts: LiveData<List<Workout>> = repository.getAllUserWorkouts(userEmail)
    
    // LiveData for active workout
    private val _activeWorkout = MutableLiveData<Workout?>()
    val activeWorkout: LiveData<Workout?> = _activeWorkout
    
    // LiveData for workout exercises
    private val _activeWorkoutId = MutableLiveData<Long>()
    val activeWorkoutExercises: LiveData<List<WorkoutExercise>> = _activeWorkoutId.switchMap { workoutId ->
        repository.getExercisesForWorkout(workoutId)
    }
    
    // Workout timer state
    private val _elapsedTime = MutableLiveData<Long>(0)
    val elapsedTime: LiveData<Long> = _elapsedTime
    
    // Status messages
    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage
    
    // Start a new workout
    fun startWorkout(
        workoutName: String,
        workoutType: String,
        difficulty: Int,
        mainStat: String,
        secondaryStat: String
    ) {
        viewModelScope.launch {
            val workout = Workout(
                userEmail = userEmail,
                workoutName = workoutName,
                workoutType = workoutType,
                difficulty = difficulty,
                mainStat = mainStat,
                secondaryStat = secondaryStat,
                startTime = Date(),
                completed = false
            )
            
            val workoutId = repository.createWorkout(workout)
            _activeWorkoutId.value = workoutId
            _activeWorkout.value = workout.copy(id = workoutId)
            _statusMessage.value = "Workout started: $workoutName"
        }
    }
    
    // Load a specific workout
    fun loadWorkout(workoutId: Long) {
        _activeWorkoutId.value = workoutId
        viewModelScope.launch {
            repository.getWorkoutById(workoutId).observeForever { workout ->
                if (workout != null) {
                    _activeWorkout.value = workout
                }
            }
        }
    }
    
    // Add an exercise to the current workout
    fun addExercise(
        exerciseName: String,
        sets: Int,
        reps: Int,
        weight: Double,
        primaryStat: String = "strength"
    ) {
        viewModelScope.launch {
            val workoutId = _activeWorkoutId.value ?: return@launch
            
            // Get the current number of exercises to set proper order
            val currentExerciseCount = repository.getExercisesForWorkout(workoutId).value?.size ?: 0
            
            val exercise = WorkoutExercise(
                workoutId = workoutId,
                exerciseName = exerciseName,
                sets = sets,
                reps = reps,
                weight = weight,
                primaryStat = primaryStat,
                order = currentExerciseCount + 1
            )
            
            repository.addExerciseToWorkout(exercise)
            _statusMessage.value = "Added exercise: $exerciseName"
        }
    }
    
    // Mark an exercise as completed
    fun completeExercise(exerciseId: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.markExerciseCompleted(exerciseId, completed)
            updateWorkoutProgress()
        }
    }
    
    // Update exercise details
    fun updateExerciseDetails(
        exerciseId: Long,
        sets: Int,
        reps: Int,
        weight: Double,
        duration: Long = 0,
        distance: Double = 0.0,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.updateExerciseDetails(
                exerciseId,
                sets,
                reps,
                weight,
                duration,
                distance,
                notes
            )
            _statusMessage.value = "Exercise updated"
        }
    }
    
    // Update workout timer
    fun updateTimer(elapsedTimeMs: Long) {
        _elapsedTime.value = elapsedTimeMs
    }
    
    // Complete the current workout
    fun completeWorkout(caloriesBurned: Int) {
        viewModelScope.launch {
            val workoutId = _activeWorkoutId.value ?: return@launch
            val duration = _elapsedTime.value ?: 0
            
            repository.completeWorkout(
                workoutId,
                userEmail,
                duration,
                caloriesBurned
            )
            
            _statusMessage.value = "Workout completed! You've earned XP and stat gains."
            _activeWorkout.value = null
            _activeWorkoutId.value = 0
            _elapsedTime.value = 0
        }
    }
    
    // Calculate completion percentage
    private fun updateWorkoutProgress() {
        viewModelScope.launch {
            val workoutId = _activeWorkoutId.value ?: return@launch
            val exercises = repository.getExercisesForWorkout(workoutId).value ?: return@launch
            
            if (exercises.isEmpty()) return@launch
            
            val totalExercises = exercises.size
            val completedExercises = exercises.count { it.completed }
            val progressPercentage = (completedExercises * 100) / totalExercises
            
            // Could update workout progress here if needed
        }
    }
    
    // Cancel the current workout
    fun cancelWorkout() {
        viewModelScope.launch {
            val workoutId = _activeWorkoutId.value ?: return@launch
            repository.deleteWorkout(workoutId)
            
            _statusMessage.value = "Workout cancelled"
            _activeWorkout.value = null
            _activeWorkoutId.value = 0
            _elapsedTime.value = 0
        }
    }
    
    // Get workouts by type
    fun getWorkoutsByType(workoutType: String): LiveData<List<Workout>> {
        return repository.getWorkoutsByType(userEmail, workoutType)
    }
    
    // Get recent workouts
    fun getRecentWorkouts(limit: Int = 5): LiveData<List<Workout>> {
        return repository.getRecentCompletedWorkouts(userEmail, limit)
    }
    
    // Factory class for creating WorkoutViewModel with dependencies
    class Factory(
        private val repository: WorkoutRepository,
        private val userEmail: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WorkoutViewModel(repository, userEmail) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
