package com.huntersascension.ui.leaderboard

import android.app.Application
import androidx.lifecycle.*
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.dao.social.LeaderboardUserWithRank
import com.huntersascension.data.model.social.LeaderboardType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for friend leaderboard features
 */
class FriendLeaderboardViewModel(application: Application) : AndroidViewModel(application) {
    
    // Database and DAOs
    private val database = AppDatabase.getDatabase(application)
    private val leaderboardDao = database.leaderboardDao()
    private val friendDao = database.friendDao()
    private val userDao = database.userDao()
    
    // Current user
    private val _currentUsername = MutableLiveData<String>()
    val currentUsername: LiveData<String> = _currentUsername
    
    // Selected leaderboard type
    private val _selectedLeaderboardType = MutableLiveData<LeaderboardType>(LeaderboardType.TOTAL_EXP)
    val selectedLeaderboardType: LiveData<LeaderboardType> = _selectedLeaderboardType
    
    // Friend leaderboard entries
    private val _friendLeaderboardEntries = MutableLiveData<List<LeaderboardUserWithRank>>()
    val friendLeaderboardEntries: LiveData<List<LeaderboardUserWithRank>> = _friendLeaderboardEntries
    
    // Top 3 friends
    private val _topFriends = MutableLiveData<List<LeaderboardUserWithRank>>()
    val topFriends: LiveData<List<LeaderboardUserWithRank>> = _topFriends
    
    // Current user's rank among friends
    private val _currentUserRank = MutableLiveData<LeaderboardUserWithRank?>()
    val currentUserRank: LiveData<LeaderboardUserWithRank?> = _currentUserRank
    
    // Loading and error states
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    /**
     * Set the current logged-in user
     */
    fun setCurrentUser(username: String) {
        _currentUsername.value = username
        loadFriendLeaderboard()
    }
    
    /**
     * Select a leaderboard type
     */
    fun selectLeaderboardType(type: LeaderboardType) {
        _selectedLeaderboardType.value = type
        loadFriendLeaderboard()
    }
    
    /**
     * Load the friend leaderboard for the selected type
     */
    fun loadFriendLeaderboard() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                val leaderboardType = _selectedLeaderboardType.value ?: LeaderboardType.TOTAL_EXP
                
                // Get friends leaderboard entries
                val entries = when (leaderboardType) {
                    LeaderboardType.TOTAL_EXP -> leaderboardDao.getFriendsTotalExpLeaderboard(username)
                    LeaderboardType.WEEKLY_EXP -> leaderboardDao.getFriendsWeeklyExpLeaderboard(username)
                    LeaderboardType.STREAK -> leaderboardDao.getFriendsStreakLeaderboard(username)
                    LeaderboardType.STRENGTH -> leaderboardDao.getFriendsStatLeaderboard(username, "STRENGTH")
                    LeaderboardType.ENDURANCE -> leaderboardDao.getFriendsStatLeaderboard(username, "ENDURANCE")
                    LeaderboardType.AGILITY -> leaderboardDao.getFriendsStatLeaderboard(username, "AGILITY")
                    LeaderboardType.VITALITY -> leaderboardDao.getFriendsStatLeaderboard(username, "VITALITY")
                    LeaderboardType.INTELLIGENCE -> leaderboardDao.getFriendsStatLeaderboard(username, "INTELLIGENCE")
                    LeaderboardType.LUCK -> leaderboardDao.getFriendsStatLeaderboard(username, "LUCK")
                    else -> emptyList()
                }
                
                // Add current user to the list if not present
                var allEntries = entries.toMutableList()
                if (!allEntries.any { it.user.username == username }) {
                    val currentUser = userDao.getUserByUsernameSync(username)
                    if (currentUser != null) {
                        val userScore = when (leaderboardType) {
                            LeaderboardType.TOTAL_EXP -> currentUser.totalExperience
                            LeaderboardType.WEEKLY_EXP -> currentUser.weeklyExperience
                            LeaderboardType.STREAK -> currentUser.streak
                            LeaderboardType.STRENGTH -> currentUser.strength
                            LeaderboardType.ENDURANCE -> currentUser.endurance
                            LeaderboardType.AGILITY -> currentUser.agility
                            LeaderboardType.VITALITY -> currentUser.vitality
                            LeaderboardType.INTELLIGENCE -> currentUser.intelligence
                            LeaderboardType.LUCK -> currentUser.luck
                            else -> 0
                        }
                        
                        // Create an entry for the current user
                        val currentUserEntry = LeaderboardUserWithRank(
                            user = currentUser,
                            rank = allEntries.size + 1, // This will be recalculated below
                            score = userScore,
                            previousRank = null
                        )
                        
                        allEntries.add(currentUserEntry)
                    }
                }
                
                // Sort and assign ranks
                allEntries.sortByDescending { it.score }
                for (i in allEntries.indices) {
                    allEntries[i] = allEntries[i].copy(rank = i + 1)
                }
                
                // Set leaderboard entries
                _friendLeaderboardEntries.value = allEntries
                
                // Extract top 3 friends
                val top3 = allEntries.take(3)
                _topFriends.value = top3
                
                // Find current user's rank
                _currentUserRank.value = allEntries.find { it.user.username == username }
                
            } catch (e: Exception) {
                _errorMessage.value = "Error loading friend leaderboard: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Get display name for leaderboard type
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
     * Refresh leaderboard with animation
     */
    fun refreshLeaderboardWithAnimation() {
        viewModelScope.launch {
            // First get the current entries to keep as "previous" state
            val currentEntries = _friendLeaderboardEntries.value?.toList() ?: emptyList()
            
            // Load fresh data
            loadFriendLeaderboard()
            
            // Wait for new data to be loaded
            delay(100)
            
            // Get new entries
            val newEntries = _friendLeaderboardEntries.value?.toList() ?: emptyList()
            
            // Create map of previous ranks
            val previousRanks = currentEntries.associate { 
                it.user.username to it.rank 
            }
            
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
            _friendLeaderboardEntries.value = updatedEntries
        }
    }
    
    /**
     * Simulate leaderboard changes (for testing animations)
     */
    fun simulateLeaderboardChanges() {
        viewModelScope.launch {
            val currentEntries = _friendLeaderboardEntries.value?.toMutableList() ?: return@launch
            if (currentEntries.isEmpty()) return@launch
            
            // Make a copy of the current entries to preserve their original state
            val originalEntries = currentEntries.map { it.copy() }
            
            // Randomly modify some scores to create movement
            val random = Random()
            for (i in currentEntries.indices) {
                if (random.nextBoolean() && i < currentEntries.size - 1) {
                    // Randomly boost this entry's score to create movement
                    val scoreIncrease = random.nextInt(100) + 10
                    currentEntries[i] = currentEntries[i].copy(
                        score = currentEntries[i].score + scoreIncrease
                    )
                }
            }
            
            // Re-sort and assign new ranks
            currentEntries.sortByDescending { it.score }
            for (i in currentEntries.indices) {
                // Store the previous rank for animation
                val username = currentEntries[i].user.username
                val previousRank = originalEntries.find { it.user.username == username }?.rank
                
                // Create updated entry with new rank and previous rank info
                currentEntries[i] = currentEntries[i].copy(
                    rank = i + 1,
                    previousRank = previousRank
                )
            }
            
            // Update the LiveData with the simulated changes
            _friendLeaderboardEntries.value = currentEntries
            
            // Update top 3
            _topFriends.value = currentEntries.take(3)
            
            // Update current user's rank
            val username = _currentUsername.value ?: return@launch
            _currentUserRank.value = currentEntries.find { it.user.username == username }
        }
    }
}
