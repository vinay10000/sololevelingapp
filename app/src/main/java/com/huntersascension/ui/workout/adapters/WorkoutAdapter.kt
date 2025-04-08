package com.huntersascension.ui.workout.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.model.Workout

/**
 * Adapter for displaying workouts in a RecyclerView
 */
class WorkoutAdapter(
    private val onWorkoutClick: (Workout) -> Unit,
    private val onStartClick: (Workout) -> Unit,
    private val onEditClick: (Workout) -> Unit,
    private val onFavoriteToggle: (Workout) -> Unit
) : ListAdapter<Workout, WorkoutAdapter.WorkoutViewHolder>(WorkoutDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textName: TextView = itemView.findViewById(R.id.text_workout_name)
        private val textType: TextView = itemView.findViewById(R.id.text_workout_type)
        private val textDifficulty: TextView = itemView.findViewById(R.id.text_difficulty)
        private val textExerciseCount: TextView = itemView.findViewById(R.id.text_exercise_count)
        private val textDuration: TextView = itemView.findViewById(R.id.text_duration)
        private val textExp: TextView = itemView.findViewById(R.id.text_exp)
        private val imageFavorite: ImageView = itemView.findViewById(R.id.image_favorite)
        private val buttonStart: Button = itemView.findViewById(R.id.button_start)
        private val buttonEdit: Button = itemView.findViewById(R.id.button_edit)
        
        fun bind(workout: Workout) {
            textName.text = workout.name
            textType.text = workout.type.toString()
            textDifficulty.text = workout.difficulty.toString()
            textDuration.text = "${workout.estimatedDuration} min"
            textExp.text = "${workout.baseExpReward} EXP"
            
            // Exercise count will be set externally with setExerciseCount method
            
            // Set favorite icon visibility
            imageFavorite.visibility = if (workout.isFavorite) View.VISIBLE else View.INVISIBLE
            
            // Set background color based on primary stat
            // In a real app, you'd use a custom background or apply a tint
            // For this example, we'll just mention the concept
            
            // Set click listeners
            itemView.setOnClickListener { onWorkoutClick(workout) }
            buttonStart.setOnClickListener { onStartClick(workout) }
            buttonEdit.setOnClickListener { onEditClick(workout) }
            imageFavorite.setOnClickListener { onFavoriteToggle(workout) }
        }
        
        fun setExerciseCount(count: Int) {
            textExerciseCount.text = "$count exercises"
        }
    }
    
    /**
     * DiffUtil.Callback for efficient RecyclerView updates
     */
    private class WorkoutDiffCallback : DiffUtil.ItemCallback<Workout>() {
        override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem.workoutId == newItem.workoutId
        }
        
        override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem == newItem
        }
    }
}
