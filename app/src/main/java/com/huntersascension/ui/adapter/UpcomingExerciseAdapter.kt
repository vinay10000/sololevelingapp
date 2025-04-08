package com.huntersascension.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.model.Exercise
import com.huntersascension.data.model.WorkoutExercise

/**
 * Adapter for displaying upcoming exercises during an active workout
 */
class UpcomingExerciseAdapter(
    private val onItemClicked: (Int) -> Unit
) : ListAdapter<UpcomingExerciseItem, UpcomingExerciseAdapter.UpcomingExerciseViewHolder>(
    UpcomingExerciseDiffCallback()
) {
    
    /**
     * ViewHolder for upcoming exercise items
     */
    class UpcomingExerciseViewHolder(
        itemView: View,
        private val onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val textExerciseOrder: TextView = itemView.findViewById(R.id.text_exercise_order)
        private val textExerciseName: TextView = itemView.findViewById(R.id.text_exercise_name)
        private val textExerciseDetails: TextView = itemView.findViewById(R.id.text_exercise_details)
        private val textExerciseStatus: TextView = itemView.findViewById(R.id.text_exercise_status)
        
        /**
         * Bind upcoming exercise data to the ViewHolder
         * @param item The upcoming exercise item to bind
         * @param position The position in the list
         */
        fun bind(item: UpcomingExerciseItem, position: Int) {
            val exercise = item.exercise
            val workoutExercise = item.workoutExercise
            
            textExerciseOrder.text = (position + 1).toString()
            textExerciseName.text = exercise.name
            
            // Generate details text based on exercise type
            val detailsText = when (exercise.type) {
                "Cardio" -> {
                    val duration = workoutExercise.duration ?: 0
                    val distance = workoutExercise.distance ?: 0f
                    if (distance > 0) {
                        "$duration min · $distance km"
                    } else {
                        "$duration min"
                    }
                }
                "Flexibility" -> {
                    val sets = workoutExercise.sets
                    val holdTime = workoutExercise.holdTime ?: 0
                    "$sets sets · $holdTime sec hold"
                }
                else -> {
                    // Weight Training or Bodyweight
                    val sets = workoutExercise.sets
                    val reps = workoutExercise.reps ?: 0
                    val weight = workoutExercise.weight
                    
                    if (workoutExercise.isBodyweight) {
                        "$sets sets × $reps reps (Bodyweight)"
                    } else if (weight != null && weight > 0) {
                        "$sets sets × $reps reps @ $weight kg"
                    } else {
                        "$sets sets × $reps reps"
                    }
                }
            }
            textExerciseDetails.text = detailsText
            
            // Set status text and color
            when (item.status) {
                ExerciseStatus.PENDING -> {
                    textExerciseStatus.text = "Pending"
                    textExerciseStatus.setTextColor(itemView.context.getColor(R.color.grey))
                }
                ExerciseStatus.CURRENT -> {
                    textExerciseStatus.text = "Current"
                    textExerciseStatus.setTextColor(itemView.context.getColor(R.color.colorPrimary))
                }
                ExerciseStatus.COMPLETED -> {
                    textExerciseStatus.text = "Completed"
                    textExerciseStatus.setTextColor(itemView.context.getColor(R.color.green))
                }
            }
            
            itemView.setOnClickListener { onItemClicked(position) }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming_exercise, parent, false)
        return UpcomingExerciseViewHolder(view, onItemClicked)
    }
    
    override fun onBindViewHolder(holder: UpcomingExerciseViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}

/**
 * Data class for upcoming exercise items
 */
data class UpcomingExerciseItem(
    val exercise: Exercise,
    val workoutExercise: WorkoutExercise,
    val status: ExerciseStatus
)

/**
 * Enum for exercise status during a workout
 */
enum class ExerciseStatus {
    PENDING,
    CURRENT,
    COMPLETED
}

/**
 * DiffUtil callback for UpcomingExerciseItem
 */
class UpcomingExerciseDiffCallback : DiffUtil.ItemCallback<UpcomingExerciseItem>() {
    override fun areItemsTheSame(oldItem: UpcomingExerciseItem, newItem: UpcomingExerciseItem): Boolean {
        return oldItem.workoutExercise.id == newItem.workoutExercise.id
    }
    
    override fun areContentsTheSame(oldItem: UpcomingExerciseItem, newItem: UpcomingExerciseItem): Boolean {
        return oldItem.workoutExercise == newItem.workoutExercise &&
               oldItem.exercise.id == newItem.exercise.id &&
               oldItem.status == newItem.status
    }
}
