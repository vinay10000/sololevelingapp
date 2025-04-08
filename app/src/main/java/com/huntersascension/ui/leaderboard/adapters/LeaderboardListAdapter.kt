package com.huntersascension.ui.leaderboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.model.social.Leaderboard
import com.huntersascension.data.model.social.LeaderboardType
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying a list of available leaderboards
 */
class LeaderboardListAdapter(
    private val onLeaderboardClick: (Leaderboard) -> Unit
) : ListAdapter<Leaderboard, LeaderboardListAdapter.LeaderboardItemViewHolder>(LeaderboardDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return LeaderboardItemViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: LeaderboardItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class LeaderboardItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textName: TextView = itemView.findViewById(R.id.text_leaderboard_name)
        private val textDescription: TextView = itemView.findViewById(R.id.text_leaderboard_description)
        private val textLastUpdated: TextView = itemView.findViewById(R.id.text_last_updated)
        private val imageLeaderboard: ImageView = itemView.findViewById(R.id.image_leaderboard_type)
        
        private val dateFormat = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
        
        fun bind(leaderboard: Leaderboard) {
            // Set basic info
            textName.text = leaderboard.name
            textDescription.text = leaderboard.description
            textLastUpdated.text = "Last updated: ${dateFormat.format(leaderboard.lastUpdated)}"
            
            // Set icon based on leaderboard type
            val iconResId = getIconForLeaderboardType(leaderboard.type)
            imageLeaderboard.setImageResource(iconResId)
            
            // Set click listener
            itemView.setOnClickListener { onLeaderboardClick(leaderboard) }
        }
        
        /**
         * Get the appropriate icon resource for a leaderboard type
         */
        private fun getIconForLeaderboardType(type: LeaderboardType): Int {
            return when (type) {
                LeaderboardType.TOTAL_EXP -> R.drawable.ic_experience
                LeaderboardType.WEEKLY_EXP -> R.drawable.ic_weekly
                LeaderboardType.MONTHLY_EXP -> R.drawable.ic_monthly
                LeaderboardType.STREAK -> R.drawable.ic_streak
                LeaderboardType.WORKOUT_COUNT -> R.drawable.ic_workout_count
                LeaderboardType.STRENGTH -> R.drawable.ic_strength
                LeaderboardType.ENDURANCE -> R.drawable.ic_endurance
                LeaderboardType.AGILITY -> R.drawable.ic_agility
                LeaderboardType.VITALITY -> R.drawable.ic_vitality
                LeaderboardType.INTELLIGENCE -> R.drawable.ic_intelligence
                LeaderboardType.LUCK -> R.drawable.ic_luck
            }
        }
    }
    
    /**
     * DiffUtil.Callback for efficient RecyclerView updates
     */
    private class LeaderboardDiffCallback : DiffUtil.ItemCallback<Leaderboard>() {
        override fun areItemsTheSame(oldItem: Leaderboard, newItem: Leaderboard): Boolean {
            return oldItem.leaderboardId == newItem.leaderboardId
        }
        
        override fun areContentsTheSame(oldItem: Leaderboard, newItem: Leaderboard): Boolean {
            return oldItem.name == newItem.name && 
                   oldItem.description == newItem.description && 
                   oldItem.lastUpdated == newItem.lastUpdated
        }
    }
}
