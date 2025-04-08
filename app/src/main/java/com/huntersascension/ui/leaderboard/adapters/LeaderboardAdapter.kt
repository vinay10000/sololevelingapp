package com.huntersascension.ui.leaderboard.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.dao.social.LeaderboardUserWithRank

/**
 * Adapter for displaying leaderboard entries
 */
class LeaderboardAdapter(
    private val onUserClick: (LeaderboardUserWithRank) -> Unit,
    private val currentUsername: String,
    private val highlightTop3: Boolean = true
) : ListAdapter<LeaderboardUserWithRank, LeaderboardAdapter.LeaderboardViewHolder>(LeaderboardDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_entry, parent, false)
        return LeaderboardViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textRank: TextView = itemView.findViewById(R.id.text_rank)
        private val textUsername: TextView = itemView.findViewById(R.id.text_username)
        private val textHunterRank: TextView = itemView.findViewById(R.id.text_hunter_rank)
        private val textLevel: TextView = itemView.findViewById(R.id.text_level)
        private val textScore: TextView = itemView.findViewById(R.id.text_score)
        private val imageTrend: ImageView = itemView.findViewById(R.id.image_trend)
        private val imageAvatar: ImageView = itemView.findViewById(R.id.image_avatar)
        private val imageMedal: ImageView = itemView.findViewById(R.id.image_medal)
        
        fun bind(entry: LeaderboardUserWithRank) {
            val user = entry.user
            
            // Set rank and position styling
            textRank.text = "#${entry.rank}"
            
            // Set user info
            textUsername.text = user.getDisplayName()
            textHunterRank.text = "${user.rank}-Rank"
            textLevel.text = "Level ${user.level}"
            textScore.text = entry.score.toString()
            
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
            
            // Set avatar if available
            // if (user.avatarPath != null) {
            //     Glide.with(itemView.context).load(user.avatarPath).into(imageAvatar)
            // }
            
            // Set click listener
            itemView.setOnClickListener { onUserClick(entry) }
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
