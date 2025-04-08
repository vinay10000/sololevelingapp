package com.huntersascension.data.model.social

/**
 * Enum representing types of content that can be shared socially
 */
enum class SharedContentType {
    WORKOUT_COMPLETION, // Completed workout
    ACHIEVEMENT,        // Unlocked achievement
    LEVEL_UP,           // Level up event
    RANK_UP,            // Rank up event
    STREAK_MILESTONE,   // Streak milestone (7 days, 30 days, etc.)
    CUSTOM_STATUS       // Custom status update
}
