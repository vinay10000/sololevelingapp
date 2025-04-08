package com.huntersascension.ui.ability

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.model.Ability

/**
 * Adapter for displaying abilities in a RecyclerView
 */
class AbilityAdapter(
    private val onAbilityClick: (Ability) -> Unit,
    private val onAbilityToggle: (Ability, Boolean) -> Unit
) : ListAdapter<AbilityAdapter.AbilityItem, AbilityAdapter.AbilityViewHolder>(AbilityDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbilityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ability, parent, false)
        return AbilityViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: AbilityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class AbilityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageAbility: ImageView = itemView.findViewById(R.id.image_ability)
        private val textName: TextView = itemView.findViewById(R.id.text_ability_name)
        private val textRank: TextView = itemView.findViewById(R.id.text_ability_rank)
        private val textDescription: TextView = itemView.findViewById(R.id.text_ability_description)
        
        fun bind(abilityItem: AbilityItem) {
            val ability = abilityItem.ability
            val isActive = abilityItem.isActive
            
            textName.text = ability.name
            textRank.text = "${ability.requiredRank}-Rank"
            textDescription.text = ability.description
            
            // Set the rank badge color based on required rank
            val rankColor = when (ability.requiredRank) {
                com.huntersascension.data.model.Rank.E -> R.color.rankE
                com.huntersascension.data.model.Rank.D -> R.color.rankD
                com.huntersascension.data.model.Rank.C -> R.color.rankC
                com.huntersascension.data.model.Rank.B -> R.color.rankB
                com.huntersascension.data.model.Rank.A -> R.color.rankA
                com.huntersascension.data.model.Rank.S -> R.color.rankS
            }
            
            // In a real app, you would set the background tint here
            // textRank.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, rankColor))
            
            // Set active/inactive state visual indicator
            // In a real app, you'd change the appearance based on active state
            if (isActive) {
                imageAbility.alpha = 1.0f
            } else {
                imageAbility.alpha = 0.5f
            }
            
            // Set click listeners
            itemView.setOnClickListener { onAbilityClick(ability) }
            
            // Long click to toggle active state
            itemView.setOnLongClickListener {
                onAbilityToggle(ability, !isActive)
                true
            }
        }
    }
    
    /**
     * Data class to hold ability item with its active state
     */
    data class AbilityItem(
        val ability: Ability,
        val isActive: Boolean
    )
    
    /**
     * DiffUtil.Callback for efficient RecyclerView updates
     */
    private class AbilityDiffCallback : DiffUtil.ItemCallback<AbilityItem>() {
        override fun areItemsTheSame(oldItem: AbilityItem, newItem: AbilityItem): Boolean {
            return oldItem.ability.abilityId == newItem.ability.abilityId
        }
        
        override fun areContentsTheSame(oldItem: AbilityItem, newItem: AbilityItem): Boolean {
            return oldItem == newItem
        }
    }
}
