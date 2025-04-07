package com.huntersascension.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huntersascension.data.model.User
import com.huntersascension.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {
    
    private val _loginStatus = MutableLiveData<LoginResult>()
    val loginStatus: LiveData<LoginResult> = _loginStatus
    
    private val _registrationStatus = MutableLiveData<RegistrationResult>()
    val registrationStatus: LiveData<RegistrationResult> = _registrationStatus
    
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = userRepository.loginUser(email, password)
            if (user != null) {
                _currentUser.value = user
                _loginStatus.value = LoginResult.Success
            } else {
                _loginStatus.value = LoginResult.Error("Invalid email or password")
            }
        }
    }
    
    fun register(email: String, username: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            _registrationStatus.value = RegistrationResult.Error("Passwords do not match")
            return
        }
        
        viewModelScope.launch {
            val result = userRepository.registerUser(email, username, password)
            if (result) {
                // Auto-login after registration
                val user = userRepository.loginUser(email, password)
                if (user != null) {
                    _currentUser.value = user
                    _registrationStatus.value = RegistrationResult.Success
                } else {
                    _registrationStatus.value = RegistrationResult.Error("Registration successful but failed to log in")
                }
            } else {
                _registrationStatus.value = RegistrationResult.Error("Failed to register user")
            }
        }
    }
    
    fun logout() {
        _currentUser.value = null
    }
    
    sealed class LoginResult {
        object Success : LoginResult()
        data class Error(val message: String) : LoginResult()
    }
    
    sealed class RegistrationResult {
        object Success : RegistrationResult()
        data class Error(val message: String) : RegistrationResult()
    }
}
