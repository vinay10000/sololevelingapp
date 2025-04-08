package com.huntersascension.ui.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huntersascension.data.model.Workout
import com.huntersascension.data.repository.WorkoutRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for workout data
 */
class WorkoutViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {
    
    private val _workouts = MutableLiveData<List<Workout>>()
    val workouts: LiveData<List<Workout>> = _workouts
    
    private val _selectedWorkout = MutableLiveData<Workout?>()
    val selectedWorkout: LiveData<Workout?> = _selectedWorkout
    
    /**
     * Loads all available workouts
     */
    fun loadWorkouts() {
        viewModelScope.launch {
            _workouts.value = workoutRepository.getAllWorkouts().value
        }
    }
    
    /**
     * Selects a workout
     */
    fun selectWorkout(workout: Workout) {
        _selectedWorkout.value = workout
    }
    
    /**
     * Clears the selected workout
     */
    fun clearSelectedWorkout() {
        _selectedWorkout.value = null
    }
}
