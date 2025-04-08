package com.huntersascension.ui.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.repository.UserRepository
import com.huntersascension.data.repository.WorkoutRepository
import com.huntersascension.ui.ability.AbilityViewModel
import com.huntersascension.ui.achievement.AchievementViewModel
import com.huntersascension.ui.auth.AuthViewModel
import com.huntersascension.ui.home.HomeViewModel
import com.huntersascension.ui.profile.ProfileViewModel
import com.huntersascension.ui.quest.QuestViewModel
import com.huntersascension.ui.user.UserViewModel
import com.huntersascension.ui.workout.WorkoutViewModel

/**
 * Factory for creating ViewModels
 */
class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = UserRepository(database.userDao())
            return UserViewModel(repository) as T
        }
        
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = WorkoutRepository(database.workoutDao())
            return WorkoutViewModel(repository) as T
        }
        
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel() as T
        }
        
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel() as T
        }
        
        if (modelClass.isAssignableFrom(AchievementViewModel::class.java)) {
            return AchievementViewModel() as T
        }
        
        if (modelClass.isAssignableFrom(QuestViewModel::class.java)) {
            return QuestViewModel() as T
        }
        
        if (modelClass.isAssignableFrom(AbilityViewModel::class.java)) {
            return AbilityViewModel() as T
        }
        
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel() as T
        }
        
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
