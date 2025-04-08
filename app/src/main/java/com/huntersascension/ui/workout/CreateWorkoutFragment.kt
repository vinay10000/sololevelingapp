package com.huntersascension.ui.workout

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.huntersascension.R
import com.huntersascension.data.model.Workout
import com.huntersascension.data.model.WorkoutExercise
import com.huntersascension.ui.adapter.WorkoutExerciseAdapter
import com.huntersascension.ui.adapter.WorkoutExerciseWithDetails
import java.util.*

/**
 * Fragment for creating a new workout
 */
class CreateWorkoutFragment : Fragment() {
    
    private val viewModel: WorkoutViewModel by activityViewModels()
    
    private lateinit var editWorkoutName: TextInputEditText
    private lateinit var spinnerWorkoutType: Spinner
    private lateinit var seekbarDifficulty: SeekBar
    private lateinit var textDifficultyValue: TextView
    private lateinit var editWorkoutDescription: TextInputEditText
    private lateinit var spinnerPrimaryStat: Spinner
    private lateinit var spinnerSecondaryStat: Spinner
    private lateinit var checkboxFavorite: CheckBox
    private lateinit var recyclerExercises: RecyclerView
    private lateinit var textNoExercises: TextView
    private lateinit var buttonAddExercise: Button
    private lateinit var buttonSaveWorkout: Button
    
    private lateinit var exerciseAdapter: WorkoutExerciseAdapter
    
    // Temporary storage for exercises to be added to the workout
    private val temporaryExercises = mutableListOf<WorkoutExerciseWithDetails>()
    
    // Current user ID - would normally come from a UserManager or similar
    private val currentUserId = 1L
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_workout, container, false)
        
        editWorkoutName = view.findViewById(R.id.edit_workout_name)
        spinnerWorkoutType = view.findViewById(R.id.spinner_workout_type)
        seekbarDifficulty = view.findViewById(R.id.seekbar_difficulty)
        textDifficultyValue = view.findViewById(R.id.text_difficulty_value)
        editWorkoutDescription = view.findViewById(R.id.edit_workout_description)
        spinnerPrimaryStat = view.findViewById(R.id.spinner_primary_stat)
        spinnerSecondaryStat = view.findViewById(R.id.spinner_secondary_stat)
        checkboxFavorite = view.findViewById(R.id.checkbox_favorite)
        recyclerExercises = view.findViewById(R.id.recycler_exercises)
        textNoExercises = view.findViewById(R.id.text_no_exercises)
        buttonAddExercise = view.findViewById(R.id.button_add_exercise)
        buttonSaveWorkout = view.findViewById(R.id.button_save_workout)
        
        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupSeekBar()
        setupRecyclerView()
        setupButtons()
        observeViewModel()
        updateExercisesUI()
    }
    
    private fun setupSeekBar() {
        seekbarDifficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Display as 1-5 instead of 0-4
                val displayValue = progress + 1
                textDifficultyValue.text = "$displayValue/5"
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    
    private fun setupRecyclerView() {
        exerciseAdapter = WorkoutExerciseAdapter(
            onEditClicked = { workoutExercise -> editExercise(workoutExercise) },
            onDeleteClicked = { workoutExercise -> removeExercise(workoutExercise) }
        )
        
        recyclerExercises.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exerciseAdapter
        }
    }
    
    private fun setupButtons() {
        buttonAddExercise.setOnClickListener {
            showAddExerciseDialog()
        }
        
        buttonSaveWorkout.setOnClickListener {
            saveWorkout()
        }
    }
    
    private fun observeViewModel() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
    }
    
    private fun showAddExerciseDialog() {
        val dialogFragment = AddExerciseDialogFragment { exercise, parameters ->
            // Create a temporary WorkoutExercise
            val workoutExercise = WorkoutExercise(
                workoutId = 0, // Will be set when workout is created
                exerciseId = exercise.id,
                orderIndex = temporaryExercises.size,
                sets = parameters.sets,
                reps = parameters.reps,
                weight = parameters.weight,
                isBodyweight = parameters.isBodyweight,
                duration = parameters.duration,
                distance = parameters.distance,
                holdTime = parameters.holdTime
            )
            
            // Add to temporary list
            temporaryExercises.add(WorkoutExerciseWithDetails(workoutExercise, exercise))
            updateExercisesUI()
        }
        
        dialogFragment.show(childFragmentManager, "add_exercise")
    }
    
    private fun editExercise(workoutExercise: WorkoutExercise) {
        // Find the exercise in the temporary list
        val index = temporaryExercises.indexOfFirst { it.workoutExercise.id == workoutExercise.id }
        if (index != -1) {
            val item = temporaryExercises[index]
            
            val dialogFragment = EditExerciseDialogFragment.newInstance(
                exercise = item.exercise,
                workoutExercise = item.workoutExercise
            ) { updatedWorkoutExercise ->
                // Update the exercise in the temporary list
                temporaryExercises[index] = item.copy(workoutExercise = updatedWorkoutExercise)
                updateExercisesUI()
            }
            
            dialogFragment.show(childFragmentManager, "edit_exercise")
        }
    }
    
    private fun removeExercise(workoutExercise: WorkoutExercise) {
        // Remove the exercise from the temporary list
        temporaryExercises.removeAll { it.workoutExercise.id == workoutExercise.id }
        
        // Update order indices
        temporaryExercises.forEachIndexed { index, item ->
            if (item.workoutExercise.orderIndex != index) {
                val updatedWorkoutExercise = item.workoutExercise.copy(orderIndex = index)
                temporaryExercises[index] = item.copy(workoutExercise = updatedWorkoutExercise)
            }
        }
        
        updateExercisesUI()
    }
    
    private fun updateExercisesUI() {
        exerciseAdapter.submitList(temporaryExercises.toList())
        
        if (temporaryExercises.isEmpty()) {
            textNoExercises.visibility = View.VISIBLE
            recyclerExercises.visibility = View.GONE
        } else {
            textNoExercises.visibility = View.GONE
            recyclerExercises.visibility = View.VISIBLE
        }
    }
    
    private fun saveWorkout() {
        val workoutName = editWorkoutName.text.toString().trim()
        if (workoutName.isEmpty()) {
            Snackbar.make(requireView(), "Please enter a workout name", Snackbar.LENGTH_SHORT).show()
            return
        }
        
        if (temporaryExercises.isEmpty()) {
            Snackbar.make(requireView(), "Please add at least one exercise", Snackbar.LENGTH_SHORT).show()
            return
        }
        
        val workoutType = spinnerWorkoutType.selectedItem.toString()
        val difficulty = seekbarDifficulty.progress + 1 // Convert from 0-4 to 1-5
        val description = editWorkoutDescription.text.toString().trim().takeIf { it.isNotEmpty() }
        val primaryStat = spinnerPrimaryStat.selectedItem.toString()
        val secondaryStat = spinnerSecondaryStat.selectedItem.toString().takeIf { it != primaryStat }
        val isFavorite = checkboxFavorite.isChecked
        
        // Calculate estimated duration based on exercises
        var estimatedDuration = 0
        for (item in temporaryExercises) {
            when (item.exercise.type) {
                "Cardio" -> {
                    estimatedDuration += item.workoutExercise.duration ?: 0
                }
                "Flexibility" -> {
                    val sets = item.workoutExercise.sets
                    val holdTime = item.workoutExercise.holdTime ?: 30
                    // Estimate 10 seconds between sets + hold time for each set
                    estimatedDuration += ((holdTime + 10) * sets) / 60 + 1 // Convert to minutes
                }
                else -> {
                    // For strength/weight training, estimate based on sets and reps
                    val sets = item.workoutExercise.sets
                    val reps = item.workoutExercise.reps ?: 10
                    // Rough estimate: 3 seconds per rep + 60 seconds rest between sets
                    estimatedDuration += ((reps * 3) * sets + 60 * (sets - 1)) / 60 + 1 // Convert to minutes
                }
            }
        }
        
        // Create the workout
        val workout = Workout(
            userId = currentUserId,
            name = workoutName,
            description = description,
            type = workoutType,
            difficulty = difficulty,
            primaryStat = primaryStat,
            secondaryStat = secondaryStat,
            estimatedDuration = estimatedDuration,
            isFavorite = isFavorite,
            createdAt = Date(),
            updatedAt = Date()
        )
        
        viewModel.createWorkout(workout).observe(viewLifecycleOwner) { workoutId ->
            if (workoutId > 0) {
                // Add exercises to the workout
                for (item in temporaryExercises) {
                    val updatedWorkoutExercise = item.workoutExercise.copy(workoutId = workoutId)
                    viewModel.addExerciseToWorkout(
                        workoutId = workoutId,
                        exerciseId = item.exercise.id,
                        workoutExercise = updatedWorkoutExercise
                    )
                }
                
                // Show success message
                Snackbar.make(requireView(), "Workout created successfully", Snackbar.LENGTH_SHORT).show()
                
                // Clear the form
                clearForm()
                
                // Navigate back to My Workouts tab
                val viewPager = requireParentFragment().view?.findViewById<androidx.viewpager2.widget.ViewPager2>(
                    R.id.view_pager_workout
                )
                viewPager?.currentItem = 0
            }
        }
    }
    
    private fun clearForm() {
        editWorkoutName.setText("")
        spinnerWorkoutType.setSelection(0)
        seekbarDifficulty.progress = 2 // Default to 3/5
        editWorkoutDescription.setText("")
        spinnerPrimaryStat.setSelection(0)
        spinnerSecondaryStat.setSelection(0)
        checkboxFavorite.isChecked = false
        temporaryExercises.clear()
        updateExercisesUI()
    }
}
