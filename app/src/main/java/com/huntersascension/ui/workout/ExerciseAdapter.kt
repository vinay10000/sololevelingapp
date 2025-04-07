package com.huntersascension.ui.workout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.model.WorkoutExercise

class ExerciseAdapter(
    private var exercises: List<WorkoutExercise> = emptyList(),
    private val onExerciseCompleted: (Long, Boolean) -> Unit,
    private val onExerciseEdit: (WorkoutExercise) -> Unit,
    private val onExerciseDelete: (Long) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseName: TextView = view.findViewById(R.id.text_exercise_name)
        val exerciseDetails: TextView = view.findViewById(R.id.text_exercise_details)
        val completedCheckbox: CheckBox = view.findViewById(R.id.checkbox_completed)
        val editButton: ImageButton = view.findViewById(R.id.button_edit_exercise)
        val deleteButton: ImageButton = view.findViewById(R.id.button_delete_exercise)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        
        holder.exerciseName.text = exercise.exerciseName
        holder.exerciseDetails.text = formatExerciseDetails(exercise)
        holder.completedCheckbox.isChecked = exercise.completed
        
        holder.completedCheckbox.setOnCheckedChangeListener { _, isChecked ->
            onExerciseCompleted(exercise.id, isChecked)
        }
        
        holder.editButton.setOnClickListener {
            onExerciseEdit(exercise)
        }
        
        holder.deleteButton.setOnClickListener {
            onExerciseDelete(exercise.id)
        }
    }
    
    override fun getItemCount() = exercises.size
    
    fun updateExercises(newExercises: List<WorkoutExercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }
    
    private fun formatExerciseDetails(exercise: WorkoutExercise): String {
        val details = StringBuilder()
        
        details.append("${exercise.sets} sets × ${exercise.reps} reps")
        
        if (exercise.weight > 0) {
            details.append(" • ${exercise.weight} kg")
        }
        
        if (exercise.duration > 0) {
            val minutes = exercise.duration / (1000 * 60)
            details.append(" • ${minutes} min")
        }
        
        if (exercise.distance > 0) {
            details.append(" • ${exercise.distance} km")
        }
        
        return details.toString()
    }
}
