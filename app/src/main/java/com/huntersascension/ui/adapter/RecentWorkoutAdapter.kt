package com.huntersascension.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.model.WorkoutHistory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying recent workout history in a RecyclerView
 */
class RecentWorkoutAdapter(
    private val onItemClicked: (WorkoutHistory) -> Unit
) : ListAdapter<WorkoutHistory, RecentWorkoutAdapter.RecentWorkoutViewHolder>(
    RecentWorkoutDiffCallback()
) {
    
    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    
    /**
     * ViewHolder for recent workout items
     */
    class RecentWorkoutViewHolder(
        itemView: View,
        private val onItemClicked: (WorkoutHistory) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val textWorkoutName: TextView = itemView.findViewById(R.id.text_workout_name)
        private val textWorkoutDate: TextView = itemView.findViewById(R.id.text_workout_date)
        private val textWorkoutType: TextView = itemView.findViewById(R.id.text_workout_type)
        private val textDuration: TextView = itemView.findViewById(R.id.text_duration)
        private val textExpGained: TextView = itemView.findViewById(R.id.text_exp_gained)
        private val textCalories: TextView = itemView.findViewById(R.id.text_calories)
        
        /**
         * Bind workout history data to the ViewHolder
         * @param workoutHistory The workout history to bind
         * @param dateFormat The date format to use
         */
        fun bind(workoutHistory: WorkoutHistory, dateFormat: SimpleDateFormat) {
            textWorkoutName.text = workoutHistory.workoutName
            textWorkoutDate.text = dateFormat.format(workoutHistory.startTime)
            textWorkoutType.text = workoutHistory.workoutType
            textDuration.text = "${workoutHistory.durationMinutes} min"
            textExpGained.text = "+${workoutHistory.xpEarned}"
            textCalories.text = workoutHistory.caloriesBurned.toString()
            
            itemView.setOnClickListener { onItemClicked(workoutHistory) }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentWorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_workout, parent, false)
        return RecentWorkoutViewHolder(view, onItemClicked)
    }
    
    override fun onBindViewHolder(holder: RecentWorkoutViewHolder, position: Int) {
        holder.bind(getItem(position), dateFormat)
    }
}

/**
 * DiffUtil callback for WorkoutHistory items
 */
class RecentWorkoutDiffCallback : DiffUtil.ItemCallback<WorkoutHistory>() {
    override fun areItemsTheSame(oldItem: WorkoutHistory, newItem: WorkoutHistory): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: WorkoutHistory, newItem: WorkoutHistory): Boolean {
        return oldItem == newItem
    }
}
