package com.huntersascension.ui.home

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

class HomeViewModel(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val preferenceManager: PreferenceManager,
    private val rankManager: RankManager
) : ViewModel() {

    // LiveData for the current user
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    // LiveData for rank up eligibility
    private val _isEligibleForRankUp = MutableLiveData<Boolean>()
    val isEligibleForRankUp: LiveData<Boolean> = _isEligibleForRankUp
    
    init {
        loadCurrentUser()
    }
    
    /**
     * Load the current logged in user
     */
    fun loadCurrentUser() {
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
     * Get next EXP target for rank up
     */
    fun getNextExpTarget(): Int? {
        val user = currentUser.value ?: return null
        return rankManager.getNextExpTarget(user.rank)
    }
    
    /**
     * Check if user is eligible for rank up
     */
    fun checkRankUpEligibility() {
        viewModelScope.launch {
            val username = preferenceManager.getLoggedInUser() ?: return@launch
            val isEligible = userRepository.checkRankUpEligibility(username)
            _isEligibleForRankUp.postValue(isEligible)
        }
    }
    
    /**
     * Log out the current user
     */
    fun logout() {
        preferenceManager.clearLoggedInUser()
        _currentUser.postValue(null)
    }
    
    /**
     * Factory for creating a HomeViewModel
     */
    class HomeViewModelFactory(
        private val userRepository: UserRepository,
        private val workoutRepository: WorkoutRepository,
        private val preferenceManager: PreferenceManager,
        private val rankManager: RankManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(userRepository, workoutRepository, preferenceManager, rankManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
