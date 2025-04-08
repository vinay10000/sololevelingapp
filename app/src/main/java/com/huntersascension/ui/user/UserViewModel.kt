package com.huntersascension.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huntersascension.data.model.User
import com.huntersascension.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for user data
 */
class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn
    
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    private var currentUsername: String? = null
    
    init {
        _isLoggedIn.value = false
        _currentUser.value = null
    }
    
    /**
     * Logs in a user
     */
    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = userRepository.login(username, password)
            if (success) {
                _isLoggedIn.postValue(true)
                currentUsername = username
                loadCurrentUser()
            }
            onResult(success)
        }
    }
    
    /**
     * Registers a new user
     */
    fun register(username: String, hunterName: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = userRepository.register(username, hunterName, password)
            if (success) {
                _isLoggedIn.postValue(true)
                currentUsername = username
                loadCurrentUser()
            }
            onResult(success)
        }
    }
    
    /**
     * Loads the current user from the repository
     */
    private fun loadCurrentUser() {
        currentUsername?.let { username ->
            _currentUser.value = userRepository.getUserByUsername(username).value
        }
    }
    
    /**
     * Logs out the current user
     */
    fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
        currentUsername = null
    }
    
    /**
     * Adds experience points to the user
     */
    fun addExperience(amount: Int, onComplete: (Boolean) -> Unit = {}) {
        currentUsername?.let { username ->
            viewModelScope.launch {
                val success = userRepository.addExp(username, amount) > 0
                if (success) {
                    loadCurrentUser()
                }
                onComplete(success)
            }
        }
    }
    
    /**
     * Increments the user's streak
     */
    fun incrementStreak(onComplete: (Boolean) -> Unit = {}) {
        currentUsername?.let { username ->
            viewModelScope.launch {
                val success = userRepository.incrementStreak(username) > 0
                if (success) {
                    loadCurrentUser()
                }
                onComplete(success)
            }
        }
    }
    
    /**
     * Resets the user's streak
     */
    fun resetStreak() {
        currentUsername?.let { username ->
            viewModelScope.launch {
                userRepository.resetStreak(username)
                loadCurrentUser()
            }
        }
    }
    
    /**
     * Updates the user's level and experience
     */
    fun updateLevelAndExp(level: Int, remainingExp: Int, expToNextLevel: Int) {
        currentUsername?.let { username ->
            viewModelScope.launch {
                userRepository.updateLevelAndExp(username, level, remainingExp, expToNextLevel)
                loadCurrentUser()
            }
        }
    }
}
