package com.huntersascension.data.model.social

/**
 * Enum representing the status of a friend request
 */
enum class FriendRequestStatus {
    PENDING,  // Request has been sent but not accepted/rejected
    ACCEPTED, // Request has been accepted
    REJECTED, // Request has been rejected
    BLOCKED   // User has been blocked
}
