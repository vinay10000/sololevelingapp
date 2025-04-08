package com.huntersascension.ui.achievement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.model.Achievement
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying achievements in a RecyclerView
 */
class AchievementAdapter(
    private val onAchievementClick: (Achievement) -> Unit
) : ListAdapter<Achievement, AchievementAdapter.AchievementViewHolder>(AchievementDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return AchievementViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageAchievement: ImageView = itemView.findViewById(R.id.image_achievement)
        private val textName: TextView = itemView.findViewById(R.id.text_achievement_name)
        private val textDescription: TextView = itemView.findViewById(R.id.text_achievement_description)
        private val textDate: TextView = itemView.findViewById(R.id.text_achievement_date)
        
        private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        
        fun bind(achievement: Achievement) {
            textName.text = achievement.title
            textDescription.text = achievement.description
            
            // Format and set the unlock date
            if (achievement.isUnlocked && achievement.unlockedDate != null) {
                textDate.visibility = View.VISIBLE
                textDate.text = "Earned on: ${dateFormat.format(achievement.unlockedDate!!)}"
            } else {
                textDate.visibility = View.GONE
            }
            
            // Set the background tint based on tier
            val backgroundTint = when (achievement.tier) {
                com.huntersascension.data.model.AchievementTier.BRONZE -> R.color.achievementBronze
                com.huntersascension.data.model.AchievementTier.SILVER -> R.color.achievementSilver
                com.huntersascension.data.model.AchievementTier.GOLD -> R.color.achievementGold
                com.huntersascension.data.model.AchievementTier.PLATINUM -> R.color.achievementPlatinum
                com.huntersascension.data.model.AchievementTier.LEGENDARY -> R.color.purple
            }
            
            // In a real app, you would set the background tint here
            // imageAchievement.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, backgroundTint))
            
            // Set click listener
            itemView.setOnClickListener { onAchievementClick(achievement) }
        }
    }
    
    /**
     * DiffUtil.Callback for efficient RecyclerView updates
     */
    private class AchievementDiffCallback : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem.achievementId == newItem.achievementId
        }
        
        override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem == newItem
        }
    }
}
