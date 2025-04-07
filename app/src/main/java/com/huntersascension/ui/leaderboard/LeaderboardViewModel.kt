package com.huntersascension.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.huntersascension.data.model.LeaderboardEntry
import com.huntersascension.data.repository.LeaderboardRepository
import com.huntersascension.data.repository.UserSettingsRepository
import kotlinx.coroutines.launch

class LeaderboardViewModel(
    private val leaderboardRepository: LeaderboardRepository,
    private val settingsRepository: UserSettingsRepository
) : ViewModel() {
    
    private val _category = MutableLiveData<String>("overall")
    private val _sortBy = MutableLiveData<String>("experience")
    private val _region = MutableLiveData<String>("global")
    private val _limit = MutableLiveData<Int>(100)
    
    private val _userRank = MutableLiveData<Int>()
    val userRank: LiveData<Int> = _userRank
    
    // Get leaderboard entries using the current filters
    val leaderboardEntries: LiveData<List<LeaderboardEntry>> = _category.switchMap { category ->
        _sortBy.switchMap { sortBy ->
            _region.switchMap { region ->
                _limit.switchMap { limit ->
                    leaderboardRepository.getLeaderboard(category, sortBy, region, limit)
                }
            }
        }
    }
    
    // Get user's own leaderboard entry
    fun getUserEntry(userEmail: String): LiveData<LeaderboardEntry> {
        return leaderboardRepository.getUserLeaderboardEntry(userEmail, _category.value ?: "overall")
    }
    
    // Update the category filter
    fun setCategory(category: String) {
        _category.value = category
    }
    
    // Update the sort method
    fun setSortBy(sortBy: String) {
        _sortBy.value = sortBy
    }
    
    // Update the region filter
    fun setRegion(region: String) {
        _region.value = region
    }
    
    // Update the limit of entries to show
    fun setLimit(limit: Int) {
        _limit.value = limit
    }
    
    // Get user's current rank in the leaderboard
    fun refreshUserRank(userEmail: String) {
        viewModelScope.launch {
            val category = _category.value ?: "overall"
            val region = _region.value ?: "global"
            val rank = leaderboardRepository.getUserRank(userEmail, category, region)
            _userRank.value = rank
        }
    }
    
    // Handle visibility toggle
    fun updateLeaderboardVisibility(userEmail: String, isVisible: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateLeaderboardVisibility(userEmail, isVisible)
            
            if (!isVisible) {
                // If user opts out, remove their entries
                _category.value?.let { currentCategory ->
                    leaderboardRepository.deleteUserEntryForCategory(userEmail, currentCategory)
                }
            } else {
                // If user opts in, sync their data
                leaderboardRepository.syncUserDataToLeaderboard(userEmail)
            }
        }
    }
    
    // Refresh leaderboard data
    fun refreshLeaderboard() {
        val currentCategory = _category.value ?: return
        val currentSortBy = _sortBy.value ?: return
        
        // Force refresh by setting values again
        _sortBy.value = currentSortBy
        _category.value = currentCategory
    }
}
