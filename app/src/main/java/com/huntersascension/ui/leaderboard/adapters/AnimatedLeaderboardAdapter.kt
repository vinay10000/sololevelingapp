package com.huntersascension.ui.leaderboard.adapters

import android.animation.ValueAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.dao.social.LeaderboardUserWithRank

/**
 * Adapter for displaying leaderboard entries with animations
 */
class AnimatedLeaderboardAdapter(
    private val onUserClick: (LeaderboardUserWithRank) -> Unit,
    private val currentUsername: String,
    private val highlightTop3: Boolean = false,
    private val animationDuration: Long = 800
) : ListAdapter<LeaderboardUserWithRank, AnimatedLeaderboardAdapter.AnimatedLeaderboardViewHolder>(LeaderboardDiffCallback()) {
    
    private var animationsEnabled = true
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimatedLeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_entry, parent, false)
        return AnimatedLeaderboardViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: AnimatedLeaderboardViewHolder, position: Int) {
        val item = getItem(position)
        val animate = animationsEnabled && position < 10 // Only animate top items for performance
        holder.bind(item, animate)
    }
    
    override fun onBindViewHolder(holder: AnimatedLeaderboardViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            // Handle partial updates if needed
            holder.updateScore(getItem(position))
        }
    }
    
    /**
     * Enable or disable animations
     */
    fun setAnimationsEnabled(enabled: Boolean) {
        animationsEnabled = enabled
    }
    
    /**
     * Trigger animations for all visible items
     */
    fun animateItems(recyclerView: RecyclerView) {
        for (i in 0 until recyclerView.childCount) {
            val view = recyclerView.getChildAt(i)
            val viewHolder = recyclerView.getChildViewHolder(view) as? AnimatedLeaderboardViewHolder
            viewHolder?.let {
                val position = recyclerView.getChildAdapterPosition(view)
                if (position != RecyclerView.NO_POSITION) {
                    it.animateScore(getItem(position))
                }
            }
        }
    }
    
    inner class AnimatedLeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textRank: TextView = itemView.findViewById(R.id.text_rank)
        private val textUsername: TextView = itemView.findViewById(R.id.text_username)
        private val textHunterRank: TextView = itemView.findViewById(R.id.text_hunter_rank)
        private val textLevel: TextView = itemView.findViewById(R.id.text_level)
        private val textScore: TextView = itemView.findViewById(R.id.text_score)
        private val imageTrend: ImageView = itemView.findViewById(R.id.image_trend)
        private val imageAvatar: ImageView = itemView.findViewById(R.id.image_avatar)
        private val imageMedal: ImageView = itemView.findViewById(R.id.image_medal)
        
        private var currentScoreAnimator: ValueAnimator? = null
        private var displayedScore: Int = 0
        
        fun bind(entry: LeaderboardUserWithRank, animate: Boolean) {
            val user = entry.user
            
            // Set rank and position styling
            textRank.text = "#${entry.rank}"
            
            // Set user info
            textUsername.text = user.getDisplayName()
            textHunterRank.text = "${user.rank}-Rank"
            textLevel.text = "Level ${user.level}"
            
            // Initialize displayed score (without animation)
            displayedScore = entry.score
            textScore.text = displayedScore.toString()
            
            // Highlight current user
            if (user.username == currentUsername) {
                itemView.setBackgroundResource(R.drawable.bg_leaderboard_current_user)
            } else {
                itemView.setBackgroundResource(0)
            }
            
            // Set medal for top 3 positions if enabled
            if (highlightTop3 && entry.rank <= 3) {
                imageMedal.visibility = View.VISIBLE
                when (entry.rank) {
                    1 -> {
                        imageMedal.setImageResource(R.drawable.medal_gold)
                        textRank.setTextColor(Color.parseColor("#FFD700")) // Gold
                    }
                    2 -> {
                        imageMedal.setImageResource(R.drawable.medal_silver)
                        textRank.setTextColor(Color.parseColor("#C0C0C0")) // Silver
                    }
                    3 -> {
                        imageMedal.setImageResource(R.drawable.medal_bronze)
                        textRank.setTextColor(Color.parseColor("#CD7F32")) // Bronze
                    }
                }
            } else {
                imageMedal.visibility = View.GONE
                textRank.setTextColor(Color.parseColor("#808080")) // Default gray
            }
            
            // Set trend indicator if previous rank exists
            if (entry.previousRank != null && entry.previousRank != entry.rank) {
                imageTrend.visibility = View.VISIBLE
                if (entry.previousRank > entry.rank) {
                    // Improved rank (moved up)
                    imageTrend.setImageResource(R.drawable.ic_trend_up)
                    imageTrend.setColorFilter(Color.GREEN)
                } else {
                    // Worsened rank (moved down)
                    imageTrend.setImageResource(R.drawable.ic_trend_down)
                    imageTrend.setColorFilter(Color.RED)
                }
            } else {
                imageTrend.visibility = View.INVISIBLE
            }
            
            // Animate score if enabled
            if (animate) {
                animateScore(entry)
            }
            
            // Set click listener
            itemView.setOnClickListener { onUserClick(entry) }
        }
        
        /**
         * Animate the score value counting up
         */
        fun animateScore(entry: LeaderboardUserWithRank) {
            // Cancel any running animation
            currentScoreAnimator?.cancel()
            
            // If the score is the same, no need to animate
            if (displayedScore == entry.score) {
                return
            }
            
            // Create a new animation from the current displayed value to the target
            val animator = ValueAnimator.ofInt(displayedScore, entry.score)
            animator.duration = animationDuration
            animator.interpolator = DecelerateInterpolator()
            
            animator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                displayedScore = animatedValue
                textScore.text = animatedValue.toString()
            }
            
            animator.start()
            currentScoreAnimator = animator
        }
        
        /**
         * Update the score without animation
         */
        fun updateScore(entry: LeaderboardUserWithRank) {
            displayedScore = entry.score
            textScore.text = displayedScore.toString()
        }
    }
    
    /**
     * DiffUtil.Callback for efficient RecyclerView updates
     */
    private class LeaderboardDiffCallback : DiffUtil.ItemCallback<LeaderboardUserWithRank>() {
        override fun areItemsTheSame(oldItem: LeaderboardUserWithRank, newItem: LeaderboardUserWithRank): Boolean {
            return oldItem.user.username == newItem.user.username
        }
        
        override fun areContentsTheSame(oldItem: LeaderboardUserWithRank, newItem: LeaderboardUserWithRank): Boolean {
            return oldItem.rank == newItem.rank && 
                   oldItem.score == newItem.score && 
                   oldItem.previousRank == newItem.previousRank
        }
    }
}
