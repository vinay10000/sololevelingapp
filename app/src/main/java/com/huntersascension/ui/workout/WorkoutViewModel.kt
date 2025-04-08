package com.huntersascension.ui.workout

import android.app.Application
import androidx.lifecycle.*
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.model.*
import com.huntersascension.ui.util.CalculationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * ViewModel for workout-related operations
 */
class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    
    // Database and DAOs
    private val database = AppDatabase.getDatabase(application)
    private val workoutDao = database.workoutDao()
    private val exerciseDao = database.exerciseDao()
    private val workoutExerciseDao = database.workoutExerciseDao()
    private val workoutHistoryDao = database.workoutHistoryDao()
    private val exerciseHistoryDao = database.exerciseHistoryDao()
    private val userDao = database.userDao()
    private val questDao = database.questDao()
    
    // LiveData for workouts
    val workouts: LiveData<List<Workout>> = workoutDao.getWorkoutsByUser(getCurrentUsername())
    val favoriteWorkouts: LiveData<List<Workout>> = workoutDao.getFavoriteWorkouts(getCurrentUsername())
    
    // Current workout data
    private val _currentWorkout = MutableLiveData<Workout>()
    val currentWorkout: LiveData<Workout> = _currentWorkout
    
    private val _workoutExercises = MutableLiveData<List<WorkoutExercise>>()
    val workoutExercises: LiveData<List<WorkoutExercise>> = _workoutExercises
    
    // Workout creation data
    private val _newWorkout = MutableLiveData<Workout>()
    val newWorkout: LiveData<Workout> = _newWorkout
    
    private val _newWorkoutExercises = MutableLiveData<MutableList<WorkoutExercise>>(mutableListOf())
    val newWorkoutExercises: LiveData<MutableList<WorkoutExercise>> = _newWorkoutExercises
    
    // Workout history
    val recentWorkoutHistory = workoutHistoryDao.getRecentWorkoutHistoryForUser(getCurrentUsername(), 5)
    
    // Current active workout session
    private val _activeWorkoutHistory = MutableLiveData<WorkoutHistory>()
    val activeWorkoutHistory: LiveData<WorkoutHistory> = _activeWorkoutHistory
    
    private val _activeExerciseHistoryList = MutableLiveData<List<ExerciseHistory>>()
    val activeExerciseHistoryList: LiveData<List<ExerciseHistory>> = _activeExerciseHistoryList
    
    // Workout completion results
    private val _workoutResults = MutableLiveData<WorkoutResults>()
    val workoutResults: LiveData<WorkoutResults> = _workoutResults
    
    // Loading and error states
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    /**
     * Loads a workout by ID and its exercises
     */
    fun loadWorkout(workoutId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workout = workoutDao.getWorkoutByIdSync(workoutId)
                if (workout != null) {
                    _currentWorkout.value = workout
                    _workoutExercises.value = workoutExerciseDao.getExercisesForWorkoutSync(workoutId)
                } else {
                    _errorMessage.value = "Workout not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading workout: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Creates a new workout
     */
    fun createWorkout(name: String, description: String, type: WorkoutType, difficulty: WorkoutDifficulty, 
                      primaryStat: Stat, secondaryStat: Stat? = null) {
        val workout = Workout(
            name = name,
            description = description,
            createdBy = getCurrentUsername(),
            type = type,
            difficulty = difficulty,
            primaryStat = primaryStat,
            secondaryStat = secondaryStat
        )
        _newWorkout.value = workout
    }
    
    /**
     * Adds an exercise to the new workout being created
     */
    fun addExerciseToNewWorkout(exerciseId: String, sets: Int, reps: Int?, weight: Float?, 
                               duration: Int?, distance: Float?, restTime: Int) {
        val currentExercises = _newWorkoutExercises.value ?: mutableListOf()
        val orderIndex = currentExercises.size
        
        val workoutExercise = WorkoutExercise(
            workoutId = _newWorkout.value?.workoutId ?: "",
            exerciseId = exerciseId,
            orderIndex = orderIndex,
            sets = sets,
            reps = reps,
            weight = weight,
            duration = duration,
            distance = distance,
            restTime = restTime
        )
        
        currentExercises.add(workoutExercise)
        _newWorkoutExercises.value = currentExercises
    }
    
    /**
     * Removes an exercise from the new workout being created
     */
    fun removeExerciseFromNewWorkout(position: Int) {
        val currentExercises = _newWorkoutExercises.value ?: return
        if (position in currentExercises.indices) {
            currentExercises.removeAt(position)
            
            // Update order indices
            for (i in position until currentExercises.size) {
                currentExercises[i] = currentExercises[i].copy(orderIndex = i)
            }
            
            _newWorkoutExercises.value = currentExercises
        }
    }
    
    /**
     * Saves the new workout to the database
     */
    fun saveNewWorkout(): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workout = _newWorkout.value
                val exercises = _newWorkoutExercises.value
                
                if (workout != null && !exercises.isNullOrEmpty()) {
                    // Insert workout
                    workoutDao.insertWorkout(workout)
                    
                    // Update exercise workoutIds and insert them
                    val updatedExercises = exercises.map { it.copy(workoutId = workout.workoutId) }
                    workoutExerciseDao.insertWorkoutExercises(updatedExercises)
                    
                    result.value = true
                    clearNewWorkoutData()
                } else {
                    _errorMessage.value = "Invalid workout data"
                    result.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error saving workout: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Updates an existing workout
     */
    fun updateWorkout(workout: Workout, exercises: List<WorkoutExercise>): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Update workout with last modified date
                val updatedWorkout = workout.copy(lastModifiedDate = Date())
                workoutDao.updateWorkout(updatedWorkout)
                
                // Delete existing exercises and insert new ones
                workoutExerciseDao.deleteAllExercisesForWorkout(workout.workoutId)
                workoutExerciseDao.insertWorkoutExercises(exercises)
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error updating workout: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Toggles the favorite status of a workout
     */
    fun toggleFavorite(workoutId: String) {
        viewModelScope.launch {
            try {
                val workout = workoutDao.getWorkoutByIdSync(workoutId) ?: return@launch
                workoutDao.setFavorite(workoutId, !workout.isFavorite)
            } catch (e: Exception) {
                _errorMessage.value = "Error updating favorite status: ${e.message}"
            }
        }
    }
    
    /**
     * Deletes a workout
     */
    fun deleteWorkout(workoutId: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workout = workoutDao.getWorkoutByIdSync(workoutId)
                if (workout != null) {
                    // Delete workout exercises first
                    workoutExerciseDao.deleteAllExercisesForWorkout(workoutId)
                    // Then delete the workout
                    workoutDao.deleteWorkout(workout)
                    result.value = true
                } else {
                    _errorMessage.value = "Workout not found"
                    result.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting workout: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Starts a workout session
     */
    fun startWorkout(workoutId: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workout = workoutDao.getWorkoutByIdSync(workoutId) ?: throw Exception("Workout not found")
                val exercises = workoutExerciseDao.getExercisesForWorkoutSync(workoutId)
                
                if (exercises.isEmpty()) {
                    throw Exception("Workout has no exercises")
                }
                
                // Create workout history
                val workoutHistory = WorkoutHistory(
                    workoutId = workoutId,
                    username = getCurrentUsername(),
                    startTime = Date()
                )
                
                val historyId = workoutHistoryDao.insertWorkoutHistory(workoutHistory).toString()
                val history = workoutHistoryDao.getWorkoutHistoryByIdSync(historyId) ?: throw Exception("Failed to create workout session")
                
                // Create exercise history entries for each exercise
                val exerciseHistoryList = exercises.map { workoutExercise ->
                    ExerciseHistory(
                        historyId = history.historyId,
                        exerciseId = workoutExercise.exerciseId
                    )
                }
                
                exerciseHistoryDao.insertExerciseHistoryList(exerciseHistoryList)
                
                // Set active workout session
                _activeWorkoutHistory.value = history
                _activeExerciseHistoryList.value = exerciseHistoryList
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error starting workout: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Completes a workout session
     */
    fun completeWorkout(intensity: WorkoutIntensity = WorkoutIntensity.NORMAL): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val history = _activeWorkoutHistory.value ?: throw Exception("No active workout")
                val endTime = Date()
                
                // Calculate duration in seconds
                val durationSeconds = (endTime.time - history.startTime.time) / 1000
                val durationMinutes = durationSeconds / 60
                
                // Get the workout
                val workout = workoutDao.getWorkoutByIdSync(history.workoutId) ?: throw Exception("Workout not found")
                
                // Get the user
                val user = userDao.getUserByUsernameSync(history.username) ?: throw Exception("User not found")
                
                // Calculate calories burned based on workout difficulty and duration
                val caloriesBurned = calculateCaloriesBurned(workout.difficulty, durationMinutes.toInt())
                
                // Calculate exp gained with intensity multiplier
                val expGained = calculateExpGained(workout, intensity, user)
                
                // Calculate stat gains
                val statGains = calculateStatGains(workout, intensity)
                
                // Update workout history with completion data
                workoutHistoryDao.completeWorkout(
                    historyId = history.historyId,
                    endTime = endTime,
                    duration = durationSeconds.toInt(),
                    calories = caloriesBurned,
                    exp = expGained
                )
                
                // Update workout history with stat gains
                workoutHistoryDao.updateStatGains(
                    historyId = history.historyId,
                    str = statGains.strengthGain,
                    end = statGains.enduranceGain,
                    agi = statGains.agilityGain,
                    vit = statGains.vitalityGain,
                    int = statGains.intelligenceGain,
                    luck = statGains.luckGain
                )
                
                // Update user stats
                addExperienceToUser(user.username, expGained)
                addStatsToUser(user.username, statGains)
                
                // Update user workout stats
                userDao.updateWorkoutStats(
                    username = user.username,
                    duration = durationMinutes.toInt(),
                    calories = caloriesBurned
                )
                
                // Update streak
                if (!user.hasWorkedOutToday) {
                    userDao.incrementStreak(user.username)
                }
                
                // Update quests
                updateQuestsAfterWorkout(workout, durationMinutes.toInt(), caloriesBurned)
                
                // Create workout results for display
                val updatedUser = userDao.getUserByUsernameSync(user.username) ?: user
                _workoutResults.value = WorkoutResults(
                    durationMinutes = durationMinutes.toInt(),
                    caloriesBurned = caloriesBurned,
                    expGained = expGained,
                    statGains = statGains,
                    streakUpdated = !user.hasWorkedOutToday,
                    newStreak = updatedUser.currentStreak,
                    leveledUp = updatedUser.level > user.level,
                    newLevel = if (updatedUser.level > user.level) updatedUser.level else null
                )
                
                // Clear active workout data
                _activeWorkoutHistory.value = null
                _activeExerciseHistoryList.value = null
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error completing workout: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Cancels the current workout session
     */
    fun cancelWorkout(): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            try {
                val history = _activeWorkoutHistory.value ?: throw Exception("No active workout")
                
                // Delete workout history and all associated exercise history
                workoutHistoryDao.deleteWorkoutHistory(history)
                
                // Clear active workout data
                _activeWorkoutHistory.value = null
                _activeExerciseHistoryList.value = null
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error canceling workout: ${e.message}"
                result.value = false
            }
        }
        return result
    }
    
    /**
     * Calculates calories burned based on workout difficulty and duration
     */
    private fun calculateCaloriesBurned(difficulty: WorkoutDifficulty, durationMinutes: Int): Int {
        // Base burn rate per minute varies by difficulty
        val burnRate = when (difficulty) {
            WorkoutDifficulty.EASY -> 5
            WorkoutDifficulty.MEDIUM -> 7
            WorkoutDifficulty.HARD -> 10
            WorkoutDifficulty.EXTREME -> 12
        }
        
        return burnRate * durationMinutes
    }
    
    /**
     * Calculates experience points gained from a workout
     */
    private fun calculateExpGained(workout: Workout, intensity: WorkoutIntensity, user: User): Int {
        // Base exp is from the workout
        var expGained = workout.calculateExpReward(intensity.getExpMultiplier())
        
        // Limit to daily exp cap
        expGained = min(expGained, user.remainingDailyExp)
        
        // Update user's remaining daily exp
        userDao.updateRemainingDailyExp(user.username, user.remainingDailyExp - expGained)
        
        return expGained
    }
    
    /**
     * Calculates stat gains from a workout
     */
    private fun calculateStatGains(workout: Workout, intensity: WorkoutIntensity): StatGains {
        // Base stat gain depends on workout difficulty
        val baseGain = when (workout.difficulty) {
            WorkoutDifficulty.EASY -> 1
            WorkoutDifficulty.MEDIUM -> 2
            WorkoutDifficulty.HARD -> 3
            WorkoutDifficulty.EXTREME -> 4
        }
        
        // Apply intensity multiplier
        val adjustedGain = (baseGain * intensity.getExpMultiplier()).toInt()
        
        // Primary stat gets full gain
        val primaryGain = adjustedGain
        
        // Secondary stat gets half gain
        val secondaryGain = adjustedGain / 2
        
        // Small random chance for other stats
        val otherGain = if (Random.nextFloat() < 0.3f) 1 else 0
        
        // Create stat gains based on workout type
        return when (workout.primaryStat) {
            Stat.STRENGTH -> StatGains(
                strengthGain = primaryGain,
                enduranceGain = if (workout.secondaryStat == Stat.ENDURANCE) secondaryGain else otherGain,
                agilityGain = if (workout.secondaryStat == Stat.AGILITY) secondaryGain else otherGain,
                vitalityGain = if (workout.secondaryStat == Stat.VITALITY) secondaryGain else otherGain,
                intelligenceGain = otherGain,
                luckGain = if (Random.nextFloat() < 0.1f) 1 else 0
            )
            Stat.ENDURANCE -> StatGains(
                strengthGain = if (workout.secondaryStat == Stat.STRENGTH) secondaryGain else otherGain,
                enduranceGain = primaryGain,
                agilityGain = if (workout.secondaryStat == Stat.AGILITY) secondaryGain else otherGain,
                vitalityGain = if (workout.secondaryStat == Stat.VITALITY) secondaryGain else otherGain,
                intelligenceGain = otherGain,
                luckGain = if (Random.nextFloat() < 0.1f) 1 else 0
            )
            Stat.AGILITY -> StatGains(
                strengthGain = if (workout.secondaryStat == Stat.STRENGTH) secondaryGain else otherGain,
                enduranceGain = if (workout.secondaryStat == Stat.ENDURANCE) secondaryGain else otherGain,
                agilityGain = primaryGain,
                vitalityGain = if (workout.secondaryStat == Stat.VITALITY) secondaryGain else otherGain,
                intelligenceGain = otherGain,
                luckGain = if (Random.nextFloat() < 0.1f) 1 else 0
            )
            Stat.VITALITY -> StatGains(
                strengthGain = if (workout.secondaryStat == Stat.STRENGTH) secondaryGain else otherGain,
                enduranceGain = if (workout.secondaryStat == Stat.ENDURANCE) secondaryGain else otherGain,
                agilityGain = if (workout.secondaryStat == Stat.AGILITY) secondaryGain else otherGain,
                vitalityGain = primaryGain,
                intelligenceGain = otherGain,
                luckGain = if (Random.nextFloat() < 0.1f) 1 else 0
            )
            else -> StatGains(
                strengthGain = otherGain,
                enduranceGain = otherGain,
                agilityGain = otherGain,
                vitalityGain = otherGain,
                intelligenceGain = primaryGain,
                luckGain = if (Random.nextFloat() < 0.1f) 1 else 0
            )
        }
    }
    
    /**
     * Adds experience to a user and handles level ups
     */
    private suspend fun addExperienceToUser(username: String, expAmount: Int) {
        withContext(Dispatchers.IO) {
            val user = userDao.getUserByUsernameSync(username) ?: return@withContext
            
            // Add exp to user
            var newExp = user.exp + expAmount
            var newLevel = user.level
            var expToNextLevel = user.expToNextLevel
            
            // Check for level up
            while (newExp >= expToNextLevel) {
                // Level up
                newExp -= expToNextLevel
                newLevel++
                
                // Calculate new exp required for next level
                expToNextLevel = CalculationUtils.calculateExpForLevel(newLevel)
            }
            
            // Update user level and exp
            userDao.updateLevel(username, newLevel, newExp, expToNextLevel)
        }
    }
    
    /**
     * Adds stats to a user
     */
    private suspend fun addStatsToUser(username: String, statGains: StatGains) {
        withContext(Dispatchers.IO) {
            if (statGains.strengthGain > 0) userDao.addStrength(username, statGains.strengthGain)
            if (statGains.enduranceGain > 0) userDao.addEndurance(username, statGains.enduranceGain)
            if (statGains.agilityGain > 0) userDao.addAgility(username, statGains.agilityGain)
            if (statGains.vitalityGain > 0) userDao.addVitality(username, statGains.vitalityGain)
            if (statGains.intelligenceGain > 0) userDao.addIntelligence(username, statGains.intelligenceGain)
            if (statGains.luckGain > 0) userDao.addLuck(username, statGains.luckGain)
        }
    }
    
    /**
     * Updates quest progress after a workout
     */
    private suspend fun updateQuestsAfterWorkout(workout: Workout, durationMinutes: Int, caloriesBurned: Int) {
        withContext(Dispatchers.IO) {
            // Update workout count quests
            questDao.updateAllQuestsOfType(QuestType.WORKOUT_COUNT, 1)
            
            // Update workout duration quests
            questDao.updateAllQuestsOfType(QuestType.WORKOUT_DURATION, durationMinutes)
            
            // Update calories burned quests
            questDao.updateAllQuestsOfType(QuestType.CALORIES_BURNED, caloriesBurned)
        }
    }
    
    /**
     * Clears data for a new workout
     */
    private fun clearNewWorkoutData() {
        _newWorkout.value = null
        _newWorkoutExercises.value = mutableListOf()
    }
    
    /**
     * Gets the current username from shared preferences
     */
    private fun getCurrentUsername(): String {
        // In a real app, this would get the username from shared preferences or a user manager
        // For simplicity, we'll return a placeholder for now
        return "testuser" // Placeholder, should be replaced with actual user management
    }
    
    /**
     * Data class to hold workout result data for display
     */
    data class WorkoutResults(
        val durationMinutes: Int,
        val caloriesBurned: Int,
        val expGained: Int,
        val statGains: StatGains,
        val streakUpdated: Boolean,
        val newStreak: Int,
        val leveledUp: Boolean,
        val newLevel: Int?
    )
    
    /**
     * Data class to hold stat gains
     */
    data class StatGains(
        val strengthGain: Int = 0,
        val enduranceGain: Int = 0,
        val agilityGain: Int = 0,
        val vitalityGain: Int = 0,
        val intelligenceGain: Int = 0,
        val luckGain: Int = 0
    )
}
