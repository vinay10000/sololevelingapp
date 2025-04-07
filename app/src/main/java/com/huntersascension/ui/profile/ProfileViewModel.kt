package com.huntersascension.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.huntersascension.data.UserRepository
import com.huntersascension.data.WorkoutRepository
import com.huntersascension.data.entity.Trophy
import com.huntersascension.data.entity.User
import com.huntersascension.utils.PreferenceManager
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    // Current user as LiveData
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    // Trophies as LiveData, derived from the current user
    val trophies: LiveData<List<Trophy>> = currentUser.switchMap { user ->
        if (user != null) {
            userRepository.getTrophiesByUser(user.username)
        } else {
            MutableLiveData(emptyList())
        }
    }
    
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
     * Factory for creating a ProfileViewModel
     */
    class ProfileViewModelFactory(
        private val userRepository: UserRepository,
        private val workoutRepository: WorkoutRepository,
        private val preferenceManager: PreferenceManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(userRepository, workoutRepository, preferenceManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
