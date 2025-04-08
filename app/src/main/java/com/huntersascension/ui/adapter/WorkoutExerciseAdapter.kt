package com.huntersascension.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.model.Exercise
import com.huntersascension.data.model.WorkoutExercise

/**
 * Adapter for displaying workout exercises in a RecyclerView
 */
class WorkoutExerciseAdapter(
    private val onEditClicked: (WorkoutExercise) -> Unit,
    private val onDeleteClicked: (WorkoutExercise) -> Unit
) : ListAdapter<WorkoutExerciseWithDetails, WorkoutExerciseAdapter.WorkoutExerciseViewHolder>(
    WorkoutExerciseDiffCallback()
) {
    
    /**
     * ViewHolder for workout exercise items
     */
    class WorkoutExerciseViewHolder(
        itemView: View,
        private val onEditClicked: (WorkoutExercise) -> Unit,
        private val onDeleteClicked: (WorkoutExercise) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val textExerciseName: TextView = itemView.findViewById(R.id.text_exercise_name)
        private val textExerciseType: TextView = itemView.findViewById(R.id.text_exercise_type)
        private val textSets: TextView = itemView.findViewById(R.id.text_sets)
        private val textReps: TextView = itemView.findViewById(R.id.text_reps)
        private val textWeight: TextView = itemView.findViewById(R.id.text_weight)
        private val layoutWeight: LinearLayout = itemView.findViewById(R.id.layout_weight)
        private val layoutCardioParams: LinearLayout = itemView.findViewById(R.id.layout_cardio_params)
        private val textTime: TextView = itemView.findViewById(R.id.text_time)
        private val textDistance: TextView = itemView.findViewById(R.id.text_distance)
        private val textPrimaryStatValue: TextView = itemView.findViewById(R.id.text_primary_stat_value)
        private val buttonEdit: Button = itemView.findViewById(R.id.button_edit_exercise)
        private val buttonDelete: ImageButton = itemView.findViewById(R.id.button_delete_exercise)
        
        /**
         * Bind workout exercise data to the ViewHolder
         * @param item The workout exercise with details to bind
         */
        fun bind(item: WorkoutExerciseWithDetails) {
            val workoutExercise = item.workoutExercise
            val exercise = item.exercise
            
            textExerciseName.text = exercise.name
            textExerciseType.text = exercise.type
            textPrimaryStatValue.text = exercise.primaryStat
            
            // Set up the appropriate layout based on exercise type
            when (exercise.type) {
                "Cardio" -> {
                    layoutWeight.visibility = View.GONE
                    layoutCardioParams.visibility = View.VISIBLE
                    
                    workoutExercise.duration?.let {
                        textTime.text = "$it min"
                    }
                    
                    workoutExercise.distance?.let {
                        textDistance.text = "$it km"
                    }
                }
                "Flexibility" -> {
                    layoutWeight.visibility = View.GONE
                    layoutCardioParams.visibility = View.GONE
                    
                    // For flexibility, we show sets and hold time in "reps"
                    textSets.text = workoutExercise.sets.toString()
                    workoutExercise.holdTime?.let {
                        textReps.text = "$it sec"
                    }
                }
                else -> {
                    // Weight Training or Bodyweight
                    layoutWeight.visibility = View.VISIBLE
                    layoutCardioParams.visibility = View.GONE
                    
                    textSets.text = workoutExercise.sets.toString()
                    workoutExercise.reps?.let {
                        textReps.text = it.toString()
                    }
                    
                    if (workoutExercise.isBodyweight) {
                        textWeight.text = "Bodyweight"
                    } else {
                        workoutExercise.weight?.let {
                            textWeight.text = "$it kg"
                        }
                    }
                }
            }
            
            buttonEdit.setOnClickListener { onEditClicked(workoutExercise) }
            buttonDelete.setOnClickListener { onDeleteClicked(workoutExercise) }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_edit, parent, false)
        return WorkoutExerciseViewHolder(view, onEditClicked, onDeleteClicked)
    }
    
    override fun onBindViewHolder(holder: WorkoutExerciseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

/**
 * DiffUtil callback for WorkoutExerciseWithDetails items
 */
class WorkoutExerciseDiffCallback : DiffUtil.ItemCallback<WorkoutExerciseWithDetails>() {
    override fun areItemsTheSame(oldItem: WorkoutExerciseWithDetails, newItem: WorkoutExerciseWithDetails): Boolean {
        return oldItem.workoutExercise.id == newItem.workoutExercise.id
    }
    
    override fun areContentsTheSame(oldItem: WorkoutExerciseWithDetails, newItem: WorkoutExerciseWithDetails): Boolean {
        return oldItem.workoutExercise == newItem.workoutExercise && 
               oldItem.exercise.id == newItem.exercise.id
    }
}

/**
 * Data class that combines WorkoutExercise with its Exercise details
 */
data class WorkoutExerciseWithDetails(
    val workoutExercise: WorkoutExercise,
    val exercise: Exercise
)
