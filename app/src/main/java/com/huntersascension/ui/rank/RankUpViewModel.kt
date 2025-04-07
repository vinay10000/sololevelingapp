package com.huntersascension.ui.rank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.huntersascension.data.UserRepository
import com.huntersascension.data.WorkoutRepository
import com.huntersascension.data.entity.User
import com.huntersascension.utils.PreferenceManager
import com.huntersascension.utils.RankManager
import kotlinx.coroutines.launch

class RankUpViewModel(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val preferenceManager: PreferenceManager,
    private val rankManager: RankManager
) : ViewModel() {

    // Current user as LiveData
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    // Rank up result
    private val _rankUpResult = MutableLiveData<Result<User>?>()
    val rankUpResult: LiveData<Result<User>?> = _rankUpResult
    
    /**
     * Load user data from repository
     */
    fun loadUserData() {
        viewModelScope.launch {
            val username = preferenceManager.getLoggedInUser()
            if (username != null) {
                val user = userRepository.getUserByUsername(username)
                _currentUser.postValue(user)
            } else {
                _currentUser.postValue(null)
            }
        }
    }
    
    /**
     * Get rank up challenge description
     */
    fun getRankUpChallengeDescription(nextRank: String): String {
        return rankManager.getRankUpChallengeDescription(nextRank)
    }
    
    /**
     * Get rank requirements
     */
    fun getRankRequirements(nextRank: String): Map<String, Int> {
        val requirements = mutableMapOf<String, Int>()
        
        // Add EXP requirement
        requirements["EXP"] = rankManager.getExpForRank(nextRank)
        
        // Add stat requirements
        val statRequirements = rankManager.getStatRequirementsForRank(nextRank)
        requirements.putAll(statRequirements)
        
        return requirements
    }
    
    /**
     * Attempt to rank up the user
     */
    fun attemptRankUp() {
        viewModelScope.launch {
            val username = preferenceManager.getLoggedInUser() ?: return@launch
            
            val result = userRepository.rankUp(username)
            _rankUpResult.postValue(result)
            
            // Reload user data if successful
            if (result.isSuccess) {
                loadUserData()
            }
        }
    }
    
    /**
     * Factory for creating a RankUpViewModel
     */
    class RankUpViewModelFactory(
        private val userRepository: UserRepository,
        private val workoutRepository: WorkoutRepository,
        private val preferenceManager: PreferenceManager,
        private val rankManager: RankManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RankUpViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RankUpViewModel(userRepository, workoutRepository, preferenceManager, rankManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
