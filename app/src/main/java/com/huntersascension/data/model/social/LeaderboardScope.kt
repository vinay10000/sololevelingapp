package com.huntersascension.data.model.social

/**
 * Enum representing the scope/visibility of a leaderboard
 */
enum class LeaderboardScope {
    GLOBAL,  // Visible to all users
    REGIONAL, // Limited to a geographic region
    FRIENDS, // Only friends of the user
    GUILD    // Only members of the user's guild (future expansion)
}
