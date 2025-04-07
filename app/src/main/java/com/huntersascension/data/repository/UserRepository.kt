package com.huntersascension.data.repository

import androidx.lifecycle.LiveData
import com.huntersascension.data.dao.UserDao
import com.huntersascension.data.model.User
import java.util.Date
import java.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {
    
    fun getUserByEmail(email: String): LiveData<User> {
        return userDao.getUserByEmail(email)
    }
    
    suspend fun registerUser(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                userDao.insert(user)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    suspend fun loginUser(email: String, passwordHash: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.login(email, passwordHash)
        }
    }
    
    suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.update(user)
        }
    }
    
    suspend fun addExperience(email: String, expPoints: Int): Int {
        return withContext(Dispatchers.IO) {
            val user = userDao.getUserByEmail(email).value ?: return@withContext 0
            
            // Calculate new experience and level
            val currentExp = user.experience
            val currentLevel = user.level
            val newExp = currentExp + expPoints
            
            // Simple leveling formula: level = sqrt(exp / 100)
            val newLevel = kotlin.math.sqrt(newExp.toDouble() / 100).toInt().coerceAtLeast(1)
            
            // Update user level and experience
            userDao.updateLevelAndExp(email, newLevel, newExp)
            
            // Return level difference to check if user leveled up
            newLevel - currentLevel
        }
    }
    
    suspend fun updateRank(email: String, newRank: String) {
        withContext(Dispatchers.IO) {
            userDao.updateRank(email, newRank)
        }
    }
    
    suspend fun incrementWorkout(email: String) {
        withContext(Dispatchers.IO) {
            userDao.incrementWorkouts(email)
            val currentDate = Date()
            userDao.updateLastWorkoutDate(email, currentDate)
            
            // Update streak logic
            updateStreak(email, currentDate)
        }
    }
    
    private suspend fun updateStreak(email: String, currentDate: Date) {
        val lastWorkoutDate = userDao.getLastWorkoutDate(email)
        
        if (lastWorkoutDate == null) {
            // First workout, set streak to 1
            userDao.updateConsecutiveStreak(email, 1)
            userDao.updateMaxStreak(email, 1)
            return
        }
        
        // Check if the last workout was yesterday or today
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val thisYear = calendar.get(Calendar.YEAR)
        
        calendar.time = lastWorkoutDate
        val lastWorkoutDay = calendar.get(Calendar.DAY_OF_YEAR)
        val lastWorkoutYear = calendar.get(Calendar.YEAR)
        
        when {
            // Same day workout, no streak update
            thisYear == lastWorkoutYear && today == lastWorkoutDay -> {
                // Do nothing, already worked out today
            }
            
            // Yesterday's workout, increment streak
            (thisYear == lastWorkoutYear && today - lastWorkoutDay == 1) ||
            (thisYear > lastWorkoutYear && today == 1 && 
             isLastDayOfYear(lastWorkoutDay, lastWorkoutYear)) -> {
                userDao.incrementStreak(email)
                userDao.updateMaxStreakIfNeeded(email)
            }
            
            // Workout after missing days, reset streak to 1
            else -> {
                userDao.updateConsecutiveStreak(email, 1)
            }
        }
    }
    
    private fun isLastDayOfYear(dayOfYear: Int, year: Int): Boolean {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        calendar.set(Calendar.DAY_OF_MONTH, 31)
        return dayOfYear == calendar.get(Calendar.DAY_OF_YEAR)
    }
    
    suspend fun getCurrentStreak(email: String): Int {
        return withContext(Dispatchers.IO) {
            userDao.getCurrentStreak(email)
        }
    }
    
    suspend fun updateStat(email: String, statName: String, value: Int) {
        withContext(Dispatchers.IO) {
            when (statName.lowercase()) {
                "strength" -> userDao.updateStrength(email, value)
                "agility" -> userDao.updateAgility(email, value)
                "vitality" -> userDao.updateVitality(email, value)
                "intelligence" -> userDao.updateIntelligence(email, value)
                "luck" -> userDao.updateLuck(email, value)
            }
        }
    }
}
