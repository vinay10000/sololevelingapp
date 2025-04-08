package com.huntersascension.ui.leaderboard

import android.app.Application
import androidx.lifecycle.*
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.dao.social.LeaderboardUserWithRank
import com.huntersascension.data.model.Rank
import com.huntersascension.data.model.Stat
import com.huntersascension.data.model.social.Leaderboard
import com.huntersascension.data.model.social.LeaderboardScope
import com.huntersascension.data.model.social.LeaderboardType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for leaderboard features
 */
class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {
    
    // Database and DAOs
    private val database = AppDatabase.getDatabase(application)
    private val leaderboardDao = database.leaderboardDao()
    
    // Current user
    private val _currentUsername = MutableLiveData<String>()
    val currentUsername: LiveData<String> = _currentUsername
    
    // List of available leaderboards
    private val _availableLeaderboards = MediatorLiveData<List<Leaderboard>>()
    val availableLeaderboards: LiveData<List<Leaderboard>> = _availableLeaderboards
    
    // Current selected leaderboard
    private val _selectedLeaderboard = MutableLiveData<Leaderboard>()
    val selectedLeaderboard: LiveData<Leaderboard> = _selectedLeaderboard
    
    // Top users on the current leaderboard
    private val _leaderboardTopUsers = MutableLiveData<List<LeaderboardUserWithRank>>()
    val leaderboardTopUsers: LiveData<List<LeaderboardUserWithRank>> = _leaderboardTopUsers
    
    // Friends on the current leaderboard
    private val _friendLeaderboardUsers = MutableLiveData<List<LeaderboardUserWithRank>>()
    val friendLeaderboardUsers: LiveData<List<LeaderboardUserWithRank>> = _friendLeaderboardUsers
    
    // Current user's rank on the leaderboard
    private val _currentUserRank = MutableLiveData<LeaderboardUserWithRank?>()
    val currentUserRank: LiveData<LeaderboardUserWithRank?> = _currentUserRank
    
    // Loading and error states
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    init {
        // Load all available leaderboards
        _availableLeaderboards.addSource(leaderboardDao.getAllLeaderboards()) { leaderboards ->
            _availableLeaderboards.value = leaderboards
        }
    }
    
    /**
     * Set the current logged-in user
     */
    fun setCurrentUser(username: String) {
        _currentUsername.value = username
    }
    
    /**
     * Load a specific leaderboard
     */
    fun loadLeaderboard(leaderboardId: String, limit: Int = 50) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get the leaderboard
                val leaderboard = leaderboardDao.getLeaderboardById(leaderboardId).value
                if (leaderboard == null) {
                    _errorMessage.value = "Leaderboard not found"
                    return@launch
                }
                
                _selectedLeaderboard.value = leaderboard
                
                // Get top users
                val username = _currentUsername.value
                if (username != null) {
                    // Load top users
                    _leaderboardTopUsers.addSource(leaderboardDao.getTopUsersWithRank(leaderboardId, limit)) { users ->
                        _leaderboardTopUsers.value = users
                    }
                    
                    // Load friends
                    _friendLeaderboardUsers.addSource(leaderboardDao.getFriendsWithRanking(leaderboardId, username)) { friends ->
                        _friendLeaderboardUsers.value = friends
                    }
                    
                    // Load current user's rank
                    _currentUserRank.addSource(leaderboardDao.getUserWithRank(leaderboardId, username)) { userRank ->
                        _currentUserRank.value = userRank
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading leaderboard: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Select a leaderboard type to view
     */
    fun selectLeaderboardType(type: LeaderboardType, scope: LeaderboardScope = LeaderboardScope.GLOBAL): LiveData<String> {
        val result = MutableLiveData<String>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Create a leaderboard ID based on type and scope
                val leaderboardId = when (scope) {
                    LeaderboardScope.GLOBAL -> {
                        when (type) {
                            LeaderboardType.TOTAL_EXP -> leaderboardDao.createOrUpdateTotalExpLeaderboard()
                            LeaderboardType.WEEKLY_EXP -> leaderboardDao.createOrUpdateWeeklyExpLeaderboard()
                            LeaderboardType.STREAK -> leaderboardDao.createOrUpdateStreakLeaderboard()
                            LeaderboardType.STRENGTH, 
                            LeaderboardType.ENDURANCE, 
                            LeaderboardType.AGILITY, 
                            LeaderboardType.VITALITY, 
                            LeaderboardType.INTELLIGENCE, 
                            LeaderboardType.LUCK -> leaderboardDao.createOrUpdateStatLeaderboard(type)
                            else -> throw Exception("Unsupported leaderboard type")
                        }
                    }
                    LeaderboardScope.RANK_SPECIFIC -> {
                        // Need rank filter
                        throw Exception("Rank-specific leaderboard needs a rank filter")
                    }
                    else -> throw Exception("Unsupported leaderboard scope")
                }
                
                // Return the leaderboard ID to load
                result.value = leaderboardId
            } catch (e: Exception) {
                _errorMessage.value = "Error creating/updating leaderboard: ${e.message}"
                result.value = null
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Select a rank-specific leaderboard
     */
    fun selectRankLeaderboard(rank: Rank): LiveData<String> {
        val result = MutableLiveData<String>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val leaderboardId = leaderboardDao.createOrUpdateRankSpecificLeaderboard(rank)
                result.value = leaderboardId
            } catch (e: Exception) {
                _errorMessage.value = "Error creating/updating rank leaderboard: ${e.message}"
                result.value = null
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Refresh the current leaderboard data
     */
    fun refreshCurrentLeaderboard(): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val leaderboard = _selectedLeaderboard.value ?: throw Exception("No leaderboard selected")
                
                // Refresh based on leaderboard type
                when (leaderboard.type) {
                    LeaderboardType.TOTAL_EXP -> leaderboardDao.createOrUpdateTotalExpLeaderboard()
                    LeaderboardType.WEEKLY_EXP -> leaderboardDao.createOrUpdateWeeklyExpLeaderboard()
                    LeaderboardType.STREAK -> leaderboardDao.createOrUpdateStreakLeaderboard()
                    LeaderboardType.STRENGTH, 
                    LeaderboardType.ENDURANCE, 
                    LeaderboardType.AGILITY, 
                    LeaderboardType.VITALITY, 
                    LeaderboardType.INTELLIGENCE, 
                    LeaderboardType.LUCK -> leaderboardDao.createOrUpdateStatLeaderboard(leaderboard.type)
                    else -> throw Exception("Unsupported leaderboard type")
                }
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error refreshing leaderboard: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Get leaderboard type display name
     */
    fun getLeaderboardTypeDisplayName(type: LeaderboardType): String {
        return when (type) {
            LeaderboardType.TOTAL_EXP -> "Total Experience"
            LeaderboardType.WEEKLY_EXP -> "Weekly Experience"
            LeaderboardType.MONTHLY_EXP -> "Monthly Experience"
            LeaderboardType.STREAK -> "Workout Streak"
            LeaderboardType.WORKOUT_COUNT -> "Workout Count"
            LeaderboardType.STRENGTH -> "Strength"
            LeaderboardType.ENDURANCE -> "Endurance"
            LeaderboardType.AGILITY -> "Agility"
            LeaderboardType.VITALITY -> "Vitality"
            LeaderboardType.INTELLIGENCE -> "Intelligence"
            LeaderboardType.LUCK -> "Luck"
        }
    }
    
    /**
     * Get leaderboard scope display name
     */
    fun getLeaderboardScopeDisplayName(scope: LeaderboardScope, rank: Rank? = null): String {
        return when (scope) {
            LeaderboardScope.GLOBAL -> "Global"
            LeaderboardScope.FRIENDS_ONLY -> "Friends Only"
            LeaderboardScope.RANK_SPECIFIC -> if (rank != null) "${rank.name}-Rank" else "Rank-Specific"
            LeaderboardScope.REGION -> "Regional"
        }
    }
    
    /**
     * Navigate to friend leaderboard
     */
    fun navigateToFriendLeaderboard(leaderboardType: LeaderboardType): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            // Set the leaderboard type in the shared data repository or preferences
            // This is a simple implementation - in a real app, you might use shared preferences
            // or a data repository to pass the selected type to the friend leaderboard
            try {
                // Save the selected type (implementation will depend on your app architecture)
                // For example, using a shared ViewModel or preferences
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error navigating to friend leaderboard: ${e.message}"
                result.value = false
            }
        }
        return result
    }
    
    /**
     * Refresh leaderboard with animation
     */
    fun refreshLeaderboardWithAnimation(): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // First get the current entries to keep as "previous" state
                val currentEntries = _leaderboardTopUsers.value?.toList() ?: emptyList()
                
                // Load fresh data by refreshing the current leaderboard
                refreshCurrentLeaderboard()
                
                // Wait for new data to be loaded
                delay(100)
                
                // Create map of previous ranks
                val previousRanks = currentEntries.associate { 
                    it.user.username to it.rank 
                }
                
                // Get new entries
                val newEntries = _leaderboardTopUsers.value?.toList() ?: emptyList()
                
                // Update new entries with previous rank information for animation
                val updatedEntries = newEntries.map { entry ->
                    val previousRank = previousRanks[entry.user.username]
                    if (previousRank != null && previousRank != entry.rank) {
                        entry.copy(previousRank = previousRank)
                    } else {
                        entry
                    }
                }
                
                // Update the LiveData with the enhanced entries
                _leaderboardTopUsers.value = updatedEntries
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error refreshing leaderboard: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
}
