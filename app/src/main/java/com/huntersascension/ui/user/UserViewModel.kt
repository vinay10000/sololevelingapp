package com.huntersascension.ui.user

import android.app.Application
import androidx.lifecycle.*
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.model.*
import com.huntersascension.ui.util.CalculationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.*

/**
 * ViewModel for user-related operations
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {
    
    // Database and DAOs
    private val database = AppDatabase.getDatabase(application)
    private val userDao = database.userDao()
    private val workoutHistoryDao = database.workoutHistoryDao()
    private val abilityDao = database.abilityDao()
    private val questDao = database.questDao()
    private val achievementDao = database.achievementDao()
    
    // Current logged-in user
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser
    
    // User and auth state
    private val _isLoggedIn = MutableLiveData<Boolean>(false)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn
    
    // Loading and error states
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    // Rank-up related
    private val _canRankUp = MutableLiveData<Boolean>(false)
    val canRankUp: LiveData<Boolean> = _canRankUp
    
    private val _rankUpRequirements = MutableLiveData<CalculationUtils.RankUpRequirements>()
    val rankUpRequirements: LiveData<CalculationUtils.RankUpRequirements> = _rankUpRequirements
    
    // Daily stats
    val todayCaloriesBurned = MediatorLiveData<Int>()
    val todayWorkoutCount = MediatorLiveData<Int>()
    val todayWorkoutDuration = MediatorLiveData<Int>()
    
    // User's abilities
    val unlockedAbilities = MediatorLiveData<List<Ability>>()
    val activeAbilities = MediatorLiveData<List<Ability>>()
    
    // User's achievements
    val recentAchievements = MediatorLiveData<List<Achievement>>()
    
    init {
        // Check for logged in user
        viewModelScope.launch {
            checkLoggedInUser()
        }
    }
    
    /**
     * Registers a new user
     */
    fun registerUser(username: String, hunterName: String, password: String, email: String?): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Check if username already exists
                if (userDao.usernameExists(username)) {
                    _errorMessage.value = "Username already exists"
                    result.value = false
                    return@launch
                }
                
                // Create new user
                val user = User(
                    username = username,
                    hunterName = hunterName,
                    passwordHash = hashPassword(password),
                    email = email,
                    createdDate = Date()
                )
                
                // Insert user
                userDao.insertUser(user)
                
                // Login the user
                _currentUser.value = user
                _isLoggedIn.value = true
                
                // Save login info
                saveLoginInfo(username)
                
                // Create initial daily quests
                createDailyQuests()
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Registration failed: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Logs in a user
     */
    fun loginUser(username: String, password: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Attempt login
                val user = userDao.loginUser(username, hashPassword(password))
                
                if (user != null) {
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    
                    // Save login info
                    saveLoginInfo(username)
                    
                    // Set up LiveData sources for stats
                    setupLiveDataSources(username)
                    
                    // Check streak and create daily quests
                    checkAndUpdateStreak(user)
                    resetDailyExp(user.username)
                    createDailyQuests()
                    
                    // Check rank up eligibility
                    checkRankUpEligibility(user)
                    
                    result.value = true
                } else {
                    _errorMessage.value = "Invalid username or password"
                    result.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Login failed: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Logs out the current user
     */
    fun logout(): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            try {
                // Clear current user and login state
                _currentUser.value = null
                _isLoggedIn.value = false
                
                // Clear saved login info
                clearLoginInfo()
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Logout failed: ${e.message}"
                result.value = false
            }
        }
        return result
    }
    
    /**
     * Updates the user's profile information
     */
    fun updateProfile(hunterName: String, email: String?, avatarPath: String?): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUserData = _currentUser.value ?: throw Exception("Not logged in")
                
                // Update user
                val updatedUser = currentUserData.copy(
                    hunterName = hunterName,
                    email = email,
                    avatarPath = avatarPath
                )
                
                userDao.updateUser(updatedUser)
                _currentUser.value = updatedUser
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Profile update failed: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Changes the user's password
     */
    fun changePassword(currentPassword: String, newPassword: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUserData = _currentUser.value ?: throw Exception("Not logged in")
                
                // Verify current password
                if (currentUserData.passwordHash != hashPassword(currentPassword)) {
                    _errorMessage.value = "Current password is incorrect"
                    result.value = false
                    return@launch
                }
                
                // Update password
                val updatedUser = currentUserData.copy(
                    passwordHash = hashPassword(newPassword)
                )
                
                userDao.updateUser(updatedUser)
                _currentUser.value = updatedUser
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Password change failed: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Refreshes the current user data
     */
    fun refreshUserData() {
        viewModelScope.launch {
            try {
                val username = _currentUser.value?.username ?: return@launch
                val user = userDao.getUserByUsernameSync(username) ?: return@launch
                
                _currentUser.value = user
                
                // Check rank up eligibility
                checkRankUpEligibility(user)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to refresh user data: ${e.message}"
            }
        }
    }
    
    /**
     * Checks if a user can rank up
     */
    fun checkRankUpEligibility(user: User) {
        viewModelScope.launch {
            try {
                // Get requirements for next rank
                val requirements = CalculationUtils.calculateRankUpRequirements(user.rank)
                
                if (requirements != null) {
                    _rankUpRequirements.value = requirements
                    
                    // Check if user meets requirements
                    val meetsExpRequirement = user.exp >= requirements.expRequired
                    val meetsStrengthRequirement = user.strength >= requirements.strengthRequired
                    val meetsEnduranceRequirement = user.endurance >= requirements.enduranceRequired
                    val meetsAgilityRequirement = user.agility >= requirements.agilityRequired
                    val meetsVitalityRequirement = user.vitality >= requirements.vitalityRequired
                    
                    // All requirements must be met to rank up
                    val canRankUp = meetsExpRequirement && 
                                     meetsStrengthRequirement && 
                                     meetsEnduranceRequirement && 
                                     meetsAgilityRequirement && 
                                     meetsVitalityRequirement
                    
                    // Update database and LiveData
                    userDao.setCanRankUp(user.username, canRankUp)
                    _canRankUp.value = canRankUp
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to check rank up eligibility: ${e.message}"
            }
        }
    }
    
    /**
     * Performs a rank up for the user
     */
    fun rankUp(): LiveData<RankUpResult> {
        val result = MutableLiveData<RankUpResult>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = _currentUser.value ?: throw Exception("Not logged in")
                
                if (!user.canRankUp) {
                    throw Exception("User does not meet rank up requirements")
                }
                
                val nextRank = user.rank.next() ?: throw Exception("Already at maximum rank")
                
                // Update user's rank
                userDao.updateRank(user.username, nextRank)
                
                // Reset rank up flag
                userDao.setCanRankUp(user.username, false)
                userDao.setRankUpQuestCompleted(user.username, false)
                
                // Unlock abilities for new rank
                abilityDao.unlockAbilitiesForRank(user.username, nextRank)
                
                // Refresh user data
                val updatedUser = userDao.getUserByUsernameSync(user.username) ?: user
                _currentUser.value = updatedUser
                
                // Get new abilities
                val newAbilities = AbilityData.getNewAbilitiesForRank(nextRank)
                
                result.value = RankUpResult(
                    previousRank = user.rank,
                    newRank = nextRank,
                    newAbilities = newAbilities
                )
            } catch (e: Exception) {
                _errorMessage.value = "Rank up failed: ${e.message}"
                result.value = null
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Creates daily quests for the user
     */
    private suspend fun createDailyQuests() {
        withContext(Dispatchers.IO) {
            try {
                // First clear any existing daily quests
                questDao.clearDailyQuests()
                
                // Create new daily quests
                val dailyQuests = listOf(
                    Quest(
                        title = "Daily Workout",
                        description = "Complete 1 workout today",
                        type = QuestType.WORKOUT_COUNT,
                        targetValue = 1,
                        expReward = 50,
                        strengthReward = 1,
                        isDaily = true
                    ),
                    Quest(
                        title = "Burn Calories",
                        description = "Burn 100 calories through workouts",
                        type = QuestType.CALORIES_BURNED,
                        targetValue = 100,
                        expReward = 30,
                        enduranceReward = 1,
                        isDaily = true
                    ),
                    Quest(
                        title = "Active Minutes",
                        description = "Exercise for at least 20 minutes",
                        type = QuestType.WORKOUT_DURATION,
                        targetValue = 20,
                        expReward = 40,
                        vitalityReward = 1,
                        isDaily = true
                    )
                )
                
                questDao.insertQuests(dailyQuests)
            } catch (e: Exception) {
                // Log error but don't stop execution
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Checks and updates the user's streak
     */
    private suspend fun checkAndUpdateStreak(user: User) {
        withContext(Dispatchers.IO) {
            try {
                val lastWorkout = workoutHistoryDao.getLastCompletedWorkout(user.username)
                
                if (lastWorkout != null) {
                    // Check if streak is still valid
                    if (!CalculationUtils.isStreakValid(lastWorkout.startTime)) {
                        // Streak broken, reset to 0
                        userDao.resetStreak(user.username)
                    }
                }
            } catch (e: Exception) {
                // Log error but don't stop execution
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Resets daily exp cap
     */
    private suspend fun resetDailyExp(username: String) {
        withContext(Dispatchers.IO) {
            try {
                userDao.resetDailyExp(username)
            } catch (e: Exception) {
                // Log error but don't stop execution
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Setup LiveData sources after login
     */
    private fun setupLiveDataSources(username: String) {
        // Daily stats
        val caloriesSource = workoutHistoryDao.getTodayCaloriesBurned(username)
        todayCaloriesBurned.addSource(caloriesSource) { value ->
            todayCaloriesBurned.value = value ?: 0
        }
        
        val workoutCountSource = workoutHistoryDao.getTodayWorkoutCount(username)
        todayWorkoutCount.addSource(workoutCountSource) { value ->
            todayWorkoutCount.value = value ?: 0
        }
        
        val durationSource = workoutHistoryDao.getTodayWorkoutDuration(username)
        todayWorkoutDuration.addSource(durationSource) { value ->
            todayWorkoutDuration.value = value ?: 0
        }
        
        // Abilities
        val userAbilitiesSource = abilityDao.getUnlockedAbilitiesForUser(username)
        unlockedAbilities.addSource(userAbilitiesSource) { abilities ->
            unlockedAbilities.value = abilities
        }
        
        val activeAbilitiesSource = abilityDao.getActiveAbilitiesForUser(username)
        activeAbilities.addSource(activeAbilitiesSource) { abilities ->
            activeAbilities.value = abilities
        }
        
        // Achievements
        val achievementsSource = achievementDao.getRecentUnlockedAchievements(username, 5)
        recentAchievements.addSource(achievementsSource) { achievements ->
            recentAchievements.value = achievements
        }
    }
    
    /**
     * Check if a user is logged in from saved preferences
     */
    private suspend fun checkLoggedInUser() {
        withContext(Dispatchers.IO) {
            try {
                // In a real app, this would check SharedPreferences for a saved username
                val savedUsername = getSavedUsername()
                
                if (savedUsername != null) {
                    val user = userDao.getUserByUsernameSync(savedUsername)
                    if (user != null) {
                        withContext(Dispatchers.Main) {
                            _currentUser.value = user
                            _isLoggedIn.value = true
                            
                            // Set up LiveData sources
                            setupLiveDataSources(user.username)
                            
                            // Check streak and daily exp
                            checkAndUpdateStreak(user)
                            resetDailyExp(user.username)
                            
                            // Check rank up eligibility
                            checkRankUpEligibility(user)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Auto-login failed: ${e.message}"
                }
            }
        }
    }
    
    /**
     * Saves login info to preferences (simplified version)
     */
    private fun saveLoginInfo(username: String) {
        // In a real app, this would save to SharedPreferences
        // For simplicity, we'll just use a static variable in a companion object
        lastLoggedInUsername = username
    }
    
    /**
     * Clears saved login info
     */
    private fun clearLoginInfo() {
        // In a real app, this would clear SharedPreferences
        lastLoggedInUsername = null
    }
    
    /**
     * Gets the saved username (simplified version)
     */
    private fun getSavedUsername(): String? {
        // In a real app, this would retrieve from SharedPreferences
        return lastLoggedInUsername
    }
    
    /**
     * Hashes a password for secure storage
     */
    private fun hashPassword(password: String): String {
        // In a real app, use a proper password hashing library with salt
        // This is a simplified example using SHA-256
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
    
    /**
     * Data class to hold rank up result data
     */
    data class RankUpResult(
        val previousRank: Rank,
        val newRank: Rank,
        val newAbilities: List<Ability>
    )
    
    companion object {
        // Static variable to simulate SharedPreferences for demo purposes
        // In a real app, use proper persistent storage
        private var lastLoggedInUsername: String? = null
    }
}
