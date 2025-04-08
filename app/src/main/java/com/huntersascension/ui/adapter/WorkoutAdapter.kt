package com.huntersascension.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.huntersascension.R
import com.huntersascension.data.model.Workout

/**
 * Adapter for displaying workouts in a RecyclerView
 */
class WorkoutAdapter(
    private val onStartClicked: (Workout) -> Unit,
    private val onEditClicked: (Workout) -> Unit,
    private val onDeleteClicked: (Workout) -> Unit
) : ListAdapter<Workout, WorkoutAdapter.WorkoutViewHolder>(WorkoutDiffCallback()) {
    
    /**
     * ViewHolder for workout items
     */
    class WorkoutViewHolder(
        itemView: View,
        private val onStartClicked: (Workout) -> Unit,
        private val onEditClicked: (Workout) -> Unit,
        private val onDeleteClicked: (Workout) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val textWorkoutName: TextView = itemView.findViewById(R.id.text_workout_name)
        private val chipWorkoutType: Chip = itemView.findViewById(R.id.chip_workout_type)
        private val textPrimaryStat: TextView = itemView.findViewById(R.id.text_primary_stat)
        private val textSecondaryStat: TextView = itemView.findViewById(R.id.text_secondary_stat)
        private val ratingDifficulty: RatingBar = itemView.findViewById(R.id.rating_difficulty)
        private val textExerciseCount: TextView = itemView.findViewById(R.id.text_exercise_count)
        private val buttonStart: Button = itemView.findViewById(R.id.button_start_workout)
        private val buttonEdit: Button = itemView.findViewById(R.id.button_edit_workout)
        private val buttonDelete: Button = itemView.findViewById(R.id.button_delete_workout)
        
        /**
         * Bind workout data to the ViewHolder
         * @param workout The workout to bind
         */
        fun bind(workout: Workout) {
            textWorkoutName.text = workout.name
            chipWorkoutType.text = workout.type
            textPrimaryStat.text = workout.primaryStat
            textSecondaryStat.text = workout.secondaryStat ?: "-"
            ratingDifficulty.rating = workout.difficulty.toFloat()
            
            // Exercise count will be set from outside this adapter using tag
            // Set a default value initially
            textExerciseCount.text = itemView.tag?.toString() ?: "?"
            
            buttonStart.setOnClickListener { onStartClicked(workout) }
            buttonEdit.setOnClickListener { onEditClicked(workout) }
            buttonDelete.setOnClickListener { onDeleteClicked(workout) }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view, onStartClicked, onEditClicked, onDeleteClicked)
    }
    
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    /**
     * Set the exercise count for a workout at a specific position
     * This method allows setting the exercise count from outside the adapter
     * @param position The position of the workout
     * @param count The exercise count
     */
    fun setExerciseCount(position: Int, count: Int) {
        notifyItemChanged(position)
        // We store the count as a tag on the item view in onBindViewHolder
        val holder = (findViewHolderForAdapterPosition(position) as? WorkoutViewHolder) ?: return
        holder.itemView.tag = count.toString()
    }
}

/**
 * DiffUtil callback for Workout items
 */
class WorkoutDiffCallback : DiffUtil.ItemCallback<Workout>() {
    override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
        return oldItem == newItem
    }
}
