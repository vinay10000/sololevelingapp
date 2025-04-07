package com.huntersascension.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.model.User
import com.huntersascension.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.security.MessageDigest

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userRepository: UserRepository
    
    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        userRepository = UserRepository(userDao)
    }
    
    fun login(username: String, password: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        
        viewModelScope.launch {
            val user = userRepository.getUserByUsernameSync(username)
            if (user != null && user.passwordHash == hashPassword(password)) {
                // Store current user in preferences
                getApplication<Application>().getSharedPreferences("user_prefs", 0)
                    .edit()
                    .putString("current_user", username)
                    .apply()
                
                result.postValue(true)
            } else {
                result.postValue(false)
            }
        }
        
        return result
    }
    
    fun register(username: String, password: String, email: String?): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        
        viewModelScope.launch {
            val existingUser = userRepository.getUserByUsernameSync(username)
            
            if (existingUser == null) {
                val newUser = User(
                    username = username,
                    passwordHash = hashPassword(password),
                    email = email
                )
                userRepository.insert(newUser)
                result.postValue(true)
            } else {
                result.postValue(false)
            }
        }
        
        return result
    }
    
    fun logout() {
        getApplication<Application>().getSharedPreferences("user_prefs", 0)
            .edit()
            .remove("current_user")
            .apply()
    }
    
    fun getCurrentUser(): String? {
        return getApplication<Application>().getSharedPreferences("user_prefs", 0)
            .getString("current_user", null)
    }
    
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
