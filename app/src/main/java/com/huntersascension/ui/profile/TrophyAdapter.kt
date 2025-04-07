package com.huntersascension.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.entity.Trophy
import java.text.SimpleDateFormat
import java.util.Locale

class TrophyAdapter : ListAdapter<Trophy, TrophyAdapter.TrophyViewHolder>(TrophyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrophyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trophy, parent, false)
        return TrophyViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrophyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TrophyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTrophyName: TextView = itemView.findViewById(R.id.tv_trophy_name)
        private val tvTrophyDate: TextView = itemView.findViewById(R.id.tv_trophy_date)
        private val tvTrophyBonus: TextView = itemView.findViewById(R.id.tv_trophy_bonus)
        
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        fun bind(trophy: Trophy) {
            tvTrophyName.text = trophy.name
            tvTrophyDate.text = "Unlocked: ${dateFormat.format(trophy.unlockedAt)}"
            tvTrophyBonus.text = "Bonus: ${trophy.bonus}"
        }
    }
    
    private class TrophyDiffCallback : DiffUtil.ItemCallback<Trophy>() {
        override fun areItemsTheSame(oldItem: Trophy, newItem: Trophy): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Trophy, newItem: Trophy): Boolean {
            return oldItem == newItem
        }
    }
}
