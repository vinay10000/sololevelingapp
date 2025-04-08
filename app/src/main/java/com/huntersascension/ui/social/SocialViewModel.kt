package com.huntersascension.ui.social

import android.app.Application
import androidx.lifecycle.*
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.dao.social.CommentWithUser
import com.huntersascension.data.dao.social.SocialShareWithUser
import com.huntersascension.data.model.Achievement
import com.huntersascension.data.model.User
import com.huntersascension.data.model.WorkoutHistory
import com.huntersascension.data.model.social.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for social features
 */
class SocialViewModel(application: Application) : AndroidViewModel(application) {
    
    // Database and DAOs
    private val database = AppDatabase.getDatabase(application)
    private val socialDao = database.socialDao()
    private val friendDao = database.friendDao()
    private val userDao = database.userDao()
    
    // Current user
    private val _currentUsername = MutableLiveData<String>()
    val currentUsername: LiveData<String> = _currentUsername
    
    // Social data
    private val _userFeed = MediatorLiveData<List<SocialShareWithUser>>()
    val userFeed: LiveData<List<SocialShareWithUser>> = _userFeed
    
    private val _userProfileShares = MediatorLiveData<List<SocialShareWithUser>>()
    val userProfileShares: LiveData<List<SocialShareWithUser>> = _userProfileShares
    
    // Friends data
    private val _friends = MediatorLiveData<List<User>>()
    val friends: LiveData<List<User>> = _friends
    
    private val _friendRequests = MediatorLiveData<List<User>>()
    val friendRequests: LiveData<List<User>> = _friendRequests
    
    private val _friendCount = MediatorLiveData<Int>()
    val friendCount: LiveData<Int> = _friendCount
    
    // Current viewed profile
    private val _viewedProfile = MutableLiveData<String>()
    
    // Loading and error states
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    /**
     * Set the current logged-in user
     */
    fun setCurrentUser(username: String) {
        _currentUsername.value = username
        
        // Setup LiveData sources for feed and friends
        _userFeed.addSource(socialDao.getFeedForUser(username)) { shares ->
            viewModelScope.launch {
                // Set isLikedByCurrentUser for each share
                val updatedShares = shares.map { share ->
                    share.apply {
                        isLikedByCurrentUser = socialDao.isLikedByUserSync(share.share.shareId, username)
                    }
                }
                _userFeed.value = updatedShares
            }
        }
        
        // Setup friends data
        _friends.addSource(friendDao.getFriendUsers(username)) { users ->
            _friends.value = users
        }
        
        _friendRequests.addSource(friendDao.getFriendRequestUsers(username)) { users ->
            _friendRequests.value = users
        }
        
        _friendCount.addSource(friendDao.getFriendCount(username)) { count ->
            _friendCount.value = count
        }
    }
    
    /**
     * Load a user's profile with their social shares
     */
    fun loadUserProfile(profileUsername: String) {
        _viewedProfile.value = profileUsername
        
        val currentUser = _currentUsername.value ?: return
        
        _userProfileShares.addSource(socialDao.getUserProfileShares(profileUsername, currentUser)) { shares ->
            viewModelScope.launch {
                // Set isLikedByCurrentUser for each share
                val updatedShares = shares.map { share ->
                    share.apply {
                        isLikedByCurrentUser = socialDao.isLikedByUserSync(share.share.shareId, currentUser)
                    }
                }
                _userProfileShares.value = updatedShares
            }
        }
    }
    
    /**
     * Create a new social share post
     */
    fun createPost(message: String, isPublic: Boolean): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                
                val share = SocialShare(
                    username = username,
                    contentType = SharedContentType.CUSTOM_MESSAGE,
                    message = message,
                    isPublic = isPublic
                )
                
                socialDao.insertShare(share)
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error creating post: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Share a workout completion
     */
    fun shareWorkoutCompletion(workout: WorkoutHistory, message: String, isPublic: Boolean): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                
                val share = SocialShare(
                    username = username,
                    contentType = SharedContentType.WORKOUT_COMPLETE,
                    contentId = workout.workoutId,
                    message = message,
                    isPublic = isPublic
                )
                
                socialDao.insertShare(share)
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error sharing workout: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Share an achievement unlock
     */
    fun shareAchievement(achievement: Achievement, message: String, isPublic: Boolean): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                
                val share = SocialShare(
                    username = username,
                    contentType = SharedContentType.ACHIEVEMENT_UNLOCKED,
                    contentId = achievement.achievementId,
                    message = message,
                    isPublic = isPublic
                )
                
                socialDao.insertShare(share)
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error sharing achievement: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Share a rank-up event
     */
    fun shareRankUp(newRank: String, message: String, isPublic: Boolean): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                
                val share = SocialShare(
                    username = username,
                    contentType = SharedContentType.RANK_UP,
                    message = message,
                    isPublic = isPublic
                )
                
                socialDao.insertShare(share)
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error sharing rank-up: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Delete a social share
     */
    fun deleteShare(shareId: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val share = socialDao.getShareById(shareId).value ?: throw Exception("Share not found")
                
                // Check if this is the user's share
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                if (share.username != username) {
                    throw Exception("Cannot delete someone else's share")
                }
                
                socialDao.deleteShare(share)
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting share: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Like or unlike a social share
     */
    fun toggleLike(shareId: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                
                // This will return true if the share is now liked, false if unliked
                val isNowLiked = socialDao.toggleLike(shareId, username)
                result.value = isNowLiked
            } catch (e: Exception) {
                _errorMessage.value = "Error toggling like: ${e.message}"
                result.value = null
            }
        }
        return result
    }
    
    /**
     * Add a comment to a social share
     */
    fun addComment(shareId: String, content: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                
                socialDao.addComment(shareId, username, content)
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error adding comment: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Get comments for a share
     */
    fun getCommentsForShare(shareId: String): LiveData<List<CommentWithUser>> {
        return socialDao.getCommentsForShare(shareId)
    }
    
    /**
     * Get replies for a comment
     */
    fun getRepliesForComment(commentId: String): LiveData<List<CommentWithUser>> {
        return socialDao.getRepliesForComment(commentId)
    }
    
    /**
     * Add a reply to a comment
     */
    fun addReply(shareId: String, parentCommentId: String, content: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                
                socialDao.addComment(shareId, username, content, parentCommentId)
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error adding reply: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Delete a comment
     */
    fun deleteComment(commentId: String, shareId: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val comment = socialDao.getCommentByIdSync(commentId) ?: throw Exception("Comment not found")
                
                // Check if this is the user's comment
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                if (comment.username != username) {
                    throw Exception("Cannot delete someone else's comment")
                }
                
                socialDao.deleteCommentAndReplies(commentId, shareId)
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting comment: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Send a friend request to another user
     */
    fun sendFriendRequest(friendUsername: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                
                // Check if friend exists
                val friend = userDao.getUserByUsernameSync(friendUsername) ?: throw Exception("User not found")
                
                // Check if already friends or blocked
                if (friendDao.areFriends(username, friendUsername)) {
                    throw Exception("Already friends with this user")
                }
                
                if (friendDao.isBlocked(username, friendUsername)) {
                    throw Exception("You have blocked this user or been blocked by them")
                }
                
                // Send friend request
                friendDao.sendFriendRequest(username, friendUsername)
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error sending friend request: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Accept a friend request
     */
    fun acceptFriendRequest(requesterUsername: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                
                // Check if request exists
                val request = friendDao.getFriendRelationSync(requesterUsername, username)
                    ?: throw Exception("Friend request not found")
                
                if (request.status != FriendRequestStatus.PENDING) {
                    throw Exception("This request is no longer pending")
                }
                
                // Accept friend request
                friendDao.acceptFriendRequest(requesterUsername, username)
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error accepting friend request: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Reject a friend request
     */
    fun rejectFriendRequest(requesterUsername: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                
                // Update status to rejected
                friendDao.updateFriendRequestStatus(
                    requesterUsername,
                    username,
                    FriendRequestStatus.REJECTED
                )
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error rejecting friend request: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Remove a friend
     */
    fun removeFriend(friendUsername: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                
                // Remove the friendship in both directions
                friendDao.removeFriendship(username, friendUsername)
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error removing friend: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Block a user
     */
    fun blockUser(targetUsername: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                
                // First remove any existing friendship
                friendDao.removeFriendship(username, targetUsername)
                
                // Then create a block relation
                friendDao.insertFriendRelation(
                    FriendRelation(
                        username = username,
                        friendUsername = targetUsername,
                        status = FriendRequestStatus.BLOCKED
                    )
                )
                
                result.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error blocking user: ${e.message}"
                result.value = false
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
    
    /**
     * Check if users are friends
     */
    fun checkFriendshipStatus(otherUsername: String): LiveData<FriendshipStatus> {
        val result = MutableLiveData<FriendshipStatus>()
        viewModelScope.launch {
            try {
                val username = _currentUsername.value ?: throw Exception("User not logged in")
                
                // Check for friendship in both directions
                val relation1 = friendDao.getFriendRelationSync(username, otherUsername)
                val relation2 = friendDao.getFriendRelationSync(otherUsername, username)
                
                val status = when {
                    relation1?.status == FriendRequestStatus.BLOCKED || relation2?.status == FriendRequestStatus.BLOCKED ->
                        FriendshipStatus.BLOCKED
                    relation1?.status == FriendRequestStatus.ACCEPTED || relation2?.status == FriendRequestStatus.ACCEPTED ->
                        FriendshipStatus.FRIENDS
                    relation1?.status == FriendRequestStatus.PENDING ->
                        FriendshipStatus.REQUEST_SENT
                    relation2?.status == FriendRequestStatus.PENDING ->
                        FriendshipStatus.REQUEST_RECEIVED
                    else ->
                        FriendshipStatus.NOT_FRIENDS
                }
                
                result.value = status
            } catch (e: Exception) {
                _errorMessage.value = e.message
                result.value = FriendshipStatus.NOT_FRIENDS
            }
        }
        return result
    }
    
    /**
     * Helper enum for friendship status
     */
    enum class FriendshipStatus {
        NOT_FRIENDS,
        FRIENDS,
        REQUEST_SENT,
        REQUEST_RECEIVED,
        BLOCKED
    }
}
