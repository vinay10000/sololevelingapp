package com.huntersascension.ui.quest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.model.Quest

/**
 * Adapter for displaying quests in a RecyclerView
 */
class QuestAdapter(
    private val onQuestClick: (Quest) -> Unit,
    private val onStartQuestClick: (Quest) -> Unit
) : ListAdapter<Quest, QuestAdapter.QuestViewHolder>(QuestDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quest, parent, false)
        return QuestViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class QuestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textName: TextView = itemView.findViewById(R.id.text_quest_name)
        private val textDescription: TextView = itemView.findViewById(R.id.text_quest_description)
        private val textExpReward: TextView = itemView.findViewById(R.id.text_quest_exp_reward)
        private val textStatReward: TextView = itemView.findViewById(R.id.text_quest_stat_reward)
        private val progressQuest: ProgressBar = itemView.findViewById(R.id.progress_quest)
        private val textProgress: TextView = itemView.findViewById(R.id.text_quest_progress)
        private val buttonStart: Button = itemView.findViewById(R.id.button_start_quest)
        
        fun bind(quest: Quest) {
            textName.text = quest.title
            textDescription.text = quest.description
            textExpReward.text = "${quest.expReward} EXP"
            
            // Set stat reward text
            val statReward = buildStatRewardText(quest)
            if (statReward.isNotEmpty()) {
                textStatReward.visibility = View.VISIBLE
                textStatReward.text = statReward
            } else {
                textStatReward.visibility = View.GONE
            }
            
            // Set progress
            val progressPercent = quest.progressPercentage.toInt()
            progressQuest.progress = progressPercent
            textProgress.text = "Progress: $progressPercent%"
            
            // Set button state based on quest state
            if (quest.isCompleted) {
                buttonStart.isEnabled = false
                buttonStart.text = "Completed"
            } else if (quest.isInProgress) {
                buttonStart.isEnabled = true
                buttonStart.text = "Continue"
            } else {
                buttonStart.isEnabled = true
                buttonStart.text = "Start"
            }
            
            // Set click listeners
            itemView.setOnClickListener { onQuestClick(quest) }
            buttonStart.setOnClickListener { onStartQuestClick(quest) }
        }
        
        private fun buildStatRewardText(quest: Quest): String {
            val rewards = mutableListOf<String>()
            
            if (quest.strengthReward > 0) rewards.add("+${quest.strengthReward} STR")
            if (quest.enduranceReward > 0) rewards.add("+${quest.enduranceReward} END")
            if (quest.agilityReward > 0) rewards.add("+${quest.agilityReward} AGI")
            if (quest.vitalityReward > 0) rewards.add("+${quest.vitalityReward} VIT")
            if (quest.intelligenceReward > 0) rewards.add("+${quest.intelligenceReward} INT")
            if (quest.luckReward > 0) rewards.add("+${quest.luckReward} LUK")
            
            return rewards.joinToString(", ")
        }
    }
    
    /**
     * DiffUtil.Callback for efficient RecyclerView updates
     */
    private class QuestDiffCallback : DiffUtil.ItemCallback<Quest>() {
        override fun areItemsTheSame(oldItem: Quest, newItem: Quest): Boolean {
            return oldItem.questId == newItem.questId
        }
        
        override fun areContentsTheSame(oldItem: Quest, newItem: Quest): Boolean {
            return oldItem == newItem
        }
    }
}
