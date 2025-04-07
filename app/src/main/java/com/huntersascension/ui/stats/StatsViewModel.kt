package com.huntersascension.ui.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huntersascension.data.model.StreakMilestone
import com.huntersascension.data.model.User
import com.huntersascension.data.repository.LeaderboardRepository
import com.huntersascension.data.repository.StreakRepository
import com.huntersascension.data.repository.UserRepository
import kotlinx.coroutines.launch

class StatsViewModel(
    private val userRepository: UserRepository,
    private val streakRepository: StreakRepository,
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {
    
    private val _lastLevelUp = MutableLiveData<Int>(0)
    val lastLevelUp: LiveData<Int> = _lastLevelUp
    
    private val _experienceMessage = MutableLiveData<String>()
    val experienceMessage: LiveData<String> = _experienceMessage
    
    private val _rewardMessage = MutableLiveData<String>()
    val rewardMessage: LiveData<String> = _rewardMessage
    
    private val _streakCheckComplete = MutableLiveData<Boolean>(false)
    val streakCheckComplete: LiveData<Boolean> = _streakCheckComplete
    
    // Get user data
    fun getUserData(userEmail: String): LiveData<User> {
        return userRepository.getUserByEmail(userEmail)
    }
    
    // Get unclaimed milestones
    fun getUnclaimedMilestones(userEmail: String): LiveData<List<StreakMilestone>> {
        return streakRepository.getUnclaimedMilestones(userEmail)
    }
    
    // Add experience points
    fun addExperience(userEmail: String, expPoints: Int, activity: String) {
        viewModelScope.launch {
            val levelDifference = userRepository.addExperience(userEmail, expPoints)
            
            if (levelDifference > 0) {
                _lastLevelUp.value = levelDifference
                _experienceMessage.value = "Level Up! You've gained $levelDifference level(s) from $activity!"
            } else {
                _experienceMessage.value = "You've gained $expPoints EXP from $activity!"
            }
            
            // Sync user data to leaderboard
            leaderboardRepository.syncUserDataToLeaderboard(userEmail)
        }
    }
    
    // Check for streak milestones
    fun checkStreakMilestones(userEmail: String) {
        viewModelScope.launch {
            streakRepository.checkAndCreateMilestones(userEmail)
            _streakCheckComplete.value = true
        }
    }
    
    // Claim a streak milestone
    fun claimMilestone(milestoneId: Long, userEmail: String) {
        viewModelScope.launch {
            streakRepository.claimMilestone(milestoneId)
            _rewardMessage.value = "Reward claimed successfully!"
        }
    }
    
    // Get current user rank in leaderboard
    suspend fun getUserRank(userEmail: String, category: String): Int {
        return leaderboardRepository.getUserRank(userEmail, category)
    }
    
    // Check if user needs to be ranked up
    fun checkAndUpdateRank(user: User) {
        viewModelScope.launch {
            val currentLevel = user.level
            val currentRank = user.rank
            
            val newRank = when {
                currentLevel >= 100 -> "S+"
                currentLevel >= 80 -> "S"
                currentLevel >= 60 -> "A"
                currentLevel >= 40 -> "B"
                currentLevel >= 20 -> "C"
                currentLevel >= 10 -> "D"
                else -> "E"
            }
            
            if (newRank != currentRank) {
                userRepository.updateRank(user.email, newRank)
                _rewardMessage.value = "Congratulations! You've been promoted to Rank $newRank!"
            }
        }
    }
    
    // Update a specific stat
    fun updateStat(userEmail: String, statName: String, newValue: Int) {
        viewModelScope.launch {
            userRepository.updateStat(userEmail, statName, newValue)
            _experienceMessage.value = "Your $statName has increased to $newValue!"
            
            // Sync to leaderboard
            leaderboardRepository.syncUserDataToLeaderboard(userEmail)
        }
    }
}
