package com.huntersascension.ui.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.huntersascension.data.UserRepository
import com.huntersascension.data.WorkoutRepository
import com.huntersascension.data.entity.Workout
import com.huntersascension.utils.PreferenceManager
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    // Workout states
    enum class WorkoutState {
        IDLE,
        IN_PROGRESS,
        COMPLETED
    }
    
    // LiveData for workout state
    private val _workoutState = MutableLiveData<WorkoutState>(WorkoutState.IDLE)
    val workoutState: LiveData<WorkoutState> = _workoutState
    
    // Current workout details
    private var currentExerciseType: String? = null
    private var currentDifficulty: String? = null
    private var currentReps: Int = 0
    
    // Completed workout
    private val _completedWorkout = MutableLiveData<Workout?>()
    val completedWorkout: LiveData<Workout?> = _completedWorkout
    
    /**
     * Start a new workout
     */
    fun startWorkout(exerciseType: String, difficulty: String, reps: Int) {
        currentExerciseType = exerciseType
        currentDifficulty = difficulty
        currentReps = reps
        
        _workoutState.value = WorkoutState.IN_PROGRESS
    }
    
    /**
     * Complete the current workout
     */
    fun completeWorkout() {
        viewModelScope.launch {
            val username = preferenceManager.getLoggedInUser() ?: return@launch
            
            if (currentExerciseType == null || currentDifficulty == null) {
                return@launch
            }
            
            val result = workoutRepository.recordWorkout(
                username = username,
                exerciseType = currentExerciseType!!,
                difficulty = currentDifficulty!!,
                reps = currentReps
            )
            
            if (result.isSuccess) {
                _completedWorkout.postValue(result.getOrNull())
                _workoutState.postValue(WorkoutState.COMPLETED)
            } else {
                // Handle error
                _workoutState.postValue(WorkoutState.IDLE)
            }
        }
    }
    
    /**
     * Cancel the current workout
     */
    fun cancelWorkout() {
        _workoutState.value = WorkoutState.IDLE
        currentExerciseType = null
        currentDifficulty = null
        currentReps = 0
    }
    
    /**
     * Factory for creating a WorkoutViewModel
     */
    class WorkoutViewModelFactory(
        private val workoutRepository: WorkoutRepository,
        private val userRepository: UserRepository,
        private val preferenceManager: PreferenceManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WorkoutViewModel(workoutRepository, userRepository, preferenceManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
