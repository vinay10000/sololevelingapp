package com.huntersascension.ui.workout

import android.app.AlertDialog
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.model.WorkoutExercise
import com.huntersascension.data.repository.UserRepository
import com.huntersascension.data.repository.WorkoutRepository
import com.huntersascension.utils.PreferenceManager
import com.huntersascension.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.*

class WorkoutFragment : Fragment() {
    
    private lateinit var workoutTitleTextView: TextView
    private lateinit var workoutTypeTextView: TextView
    private lateinit var workoutInfoTextView: TextView
    private lateinit var elapsedTimeTextView: TextView
    private lateinit var exercisesRecyclerView: RecyclerView
    private lateinit var addExerciseButton: Button
    private lateinit var saveWorkoutButton: Button
    private lateinit var cancelWorkoutButton: Button
    
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var exerciseAdapter: ExerciseAdapter
    
    private var workoutTimerHandler = android.os.Handler()
    private var startTimeMillis: Long = 0
    private lateinit var workoutTimerRunnable: Runnable
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout, container, false)
        
        workoutTitleTextView = view.findViewById(R.id.text_workout_title)
        workoutTypeTextView = view.findViewById(R.id.text_workout_type)
        workoutInfoTextView = view.findViewById(R.id.text_workout_info)
        elapsedTimeTextView = view.findViewById(R.id.text_elapsed_time)
        exercisesRecyclerView = view.findViewById(R.id.recycler_exercises)
        addExerciseButton = view.findViewById(R.id.button_add_exercise)
        saveWorkoutButton = view.findViewById(R.id.button_save_workout)
        cancelWorkoutButton = view.findViewById(R.id.button_cancel_workout)
        
        // Set up RecyclerView
        exercisesRecyclerView.layoutManager = LinearLayoutManager(context)
        
        // Initialize exercise adapter
        exerciseAdapter = ExerciseAdapter(
            onExerciseCompleted = { exerciseId, completed ->
                workoutViewModel.completeExercise(exerciseId, completed)
            },
            onExerciseEdit = { exercise ->
                showEditExerciseDialog(exercise)
            },
            onExerciseDelete = { exerciseId ->
                confirmExerciseDelete(exerciseId)
            }
        )
        exercisesRecyclerView.adapter = exerciseAdapter
        
        setupViewModel()
        setupTimer()
        
        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        addExerciseButton.setOnClickListener {
            showAddExerciseDialog()
        }
        
        saveWorkoutButton.setOnClickListener {
            showCompleteWorkoutDialog()
        }
        
        cancelWorkoutButton.setOnClickListener {
            confirmCancelWorkout()
        }
        
        // Observe workout data
        workoutViewModel.activeWorkout.observe(viewLifecycleOwner) { workout ->
            if (workout != null) {
                workoutTitleTextView.text = workout.workoutName
                workoutTypeTextView.text = workout.workoutType.capitalize(Locale.ROOT)
                workoutInfoTextView.text = "Level ${workout.difficulty} • ${workout.mainStat.capitalize(Locale.ROOT)} +${workout.difficulty} • ${workout.secondaryStat.capitalize(Locale.ROOT)} +1"
                
                // Start timer if it's not already running
                if (startTimeMillis == 0L) {
                    startTimeMillis = SystemClock.elapsedRealtime()
                    workoutTimerHandler.post(workoutTimerRunnable)
                }
            } else {
                // Clear workout display
                workoutTitleTextView.text = "Start New Workout"
                workoutTypeTextView.text = ""
                workoutInfoTextView.text = ""
                elapsedTimeTextView.text = "00:00:00"
                
                // Stop timer
                workoutTimerHandler.removeCallbacks(workoutTimerRunnable)
                startTimeMillis = 0
            }
        }
        
        // Observe exercises
        workoutViewModel.activeWorkoutExercises.observe(viewLifecycleOwner) { exercises ->
            exerciseAdapter.updateExercises(exercises)
        }
        
        // Observe status messages
        workoutViewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
        
        // If no active workout, show new workout dialog
        if (workoutViewModel.activeWorkout.value == null) {
            showNewWorkoutDialog()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        workoutTimerHandler.removeCallbacks(workoutTimerRunnable)
    }
    
    private fun setupViewModel() {
        val db = AppDatabase.getDatabase(requireContext())
        val workoutDao = db.workoutDao()
        val exerciseDao = db.workoutExerciseDao()
        val userDao = db.userDao()
        val userRepository = UserRepository(userDao)
        val workoutRepository = WorkoutRepository(workoutDao, exerciseDao, userRepository)
        val preferenceManager = PreferenceManager(requireContext())
        
        val userEmail = preferenceManager.getLoggedInUser() ?: "guest@example.com"
        
        workoutViewModel = ViewModelProvider(
            this,
            WorkoutViewModel.Factory(workoutRepository, userEmail)
        ).get(WorkoutViewModel::class.java)
    }
    
    private fun setupTimer() {
        workoutTimerRunnable = object : Runnable {
            override fun run() {
                val elapsedMillis = SystemClock.elapsedRealtime() - startTimeMillis
                workoutViewModel.updateTimer(elapsedMillis)
                
                // Format time as HH:MM:SS
                val hours = (elapsedMillis / (1000 * 60 * 60)) % 24
                val minutes = (elapsedMillis / (1000 * 60)) % 60
                val seconds = (elapsedMillis / 1000) % 60
                val timeStr = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                
                elapsedTimeTextView.text = timeStr
                
                // Schedule next update
                workoutTimerHandler.postDelayed(this, 1000)
            }
        }
    }
    
    private fun showNewWorkoutDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_new_workout, null)
        
        // Init dialog elements here (see separate implementation)
        val workoutNameEditText = view.findViewById<EditText>(R.id.edit_workout_name)
        val workoutTypeSpinner = view.findViewById<Spinner>(R.id.spinner_workout_type)
        val difficultySeekBar = view.findViewById<SeekBar>(R.id.seekbar_difficulty)
        val difficultyTextView = view.findViewById<TextView>(R.id.text_difficulty_value)
        val mainStatSpinner = view.findViewById<Spinner>(R.id.spinner_main_stat)
        val secondaryStatSpinner = view.findViewById<Spinner>(R.id.spinner_secondary_stat)
        
        // Set difficulty text when seekbar changes
        difficultySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val level = progress + 1
                difficultyTextView.text = "Level $level"
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        
        // Create dialog
        AlertDialog.Builder(requireContext())
            .setTitle("Start New Workout")
            .setView(view)
            .setPositiveButton("Start") { _, _ ->
                val workoutName = workoutNameEditText.text.toString().takeIf { it.isNotEmpty() } ?: "My Workout"
                val workoutType = workoutTypeSpinner.selectedItem.toString().lowercase()
                val difficulty = difficultySeekBar.progress + 1
                val mainStat = mainStatSpinner.selectedItem.toString().lowercase()
                val secondaryStat = secondaryStatSpinner.selectedItem.toString().lowercase()
                
                workoutViewModel.startWorkout(
                    workoutName,
                    workoutType,
                    difficulty,
                    mainStat,
                    secondaryStat
                )
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Do nothing, dialog will dismiss
            }
            .setCancelable(false)
            .show()
    }
    
    private fun showAddExerciseDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_exercise, null)
        
        // Init dialog elements (see separate implementation)
        val exerciseNameEditText = view.findViewById<EditText>(R.id.edit_exercise_name)
        val setsEditText = view.findViewById<EditText>(R.id.edit_sets)
        val repsEditText = view.findViewById<EditText>(R.id.edit_reps)
        val weightEditText = view.findViewById<EditText>(R.id.edit_weight)
        val primaryStatSpinner = view.findViewById<Spinner>(R.id.spinner_primary_stat)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Add New Exercise")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                val exerciseName = exerciseNameEditText.text.toString().takeIf { it.isNotEmpty() } ?: "Exercise"
                val sets = setsEditText.text.toString().toIntOrNull() ?: 3
                val reps = repsEditText.text.toString().toIntOrNull() ?: 10
                val weight = weightEditText.text.toString().toDoubleOrNull() ?: 0.0
                val primaryStat = primaryStatSpinner.selectedItem.toString().lowercase()
                
                workoutViewModel.addExercise(
                    exerciseName,
                    sets,
                    reps,
                    weight,
                    primaryStat
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showEditExerciseDialog(exercise: WorkoutExercise) {
        val view = layoutInflater.inflate(R.layout.dialog_add_exercise, null)
        
        // Init dialog elements and populate with current data
        val exerciseNameEditText = view.findViewById<EditText>(R.id.edit_exercise_name)
        val setsEditText = view.findViewById<EditText>(R.id.edit_sets)
        val repsEditText = view.findViewById<EditText>(R.id.edit_reps)
        val weightEditText = view.findViewById<EditText>(R.id.edit_weight)
        val primaryStatSpinner = view.findViewById<Spinner>(R.id.spinner_primary_stat)
        
        // Pre-populate fields with existing data
        exerciseNameEditText.setText(exercise.exerciseName)
        setsEditText.setText(exercise.sets.toString())
        repsEditText.setText(exercise.reps.toString())
        weightEditText.setText(exercise.weight.toString())
        
        // Set spinner selection (implement as needed)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Exercise")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val exerciseName = exerciseNameEditText.text.toString().takeIf { it.isNotEmpty() } ?: "Exercise"
                val sets = setsEditText.text.toString().toIntOrNull() ?: 3
                val reps = repsEditText.text.toString().toIntOrNull() ?: 10
                val weight = weightEditText.text.toString().toDoubleOrNull() ?: 0.0
                val primaryStat = primaryStatSpinner.selectedItem.toString().lowercase()
                
                val updatedExercise = exercise.copy(
                    exerciseName = exerciseName,
                    sets = sets,
                    reps = reps,
                    weight = weight,
                    primaryStat = primaryStat
                )
                
                workoutViewModel.updateExercise(updatedExercise)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun confirmExerciseDelete(exerciseId: Long) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Exercise")
            .setMessage("Are you sure you want to remove this exercise?")
            .setPositiveButton("Delete") { _, _ ->
                workoutViewModel.deleteExercise(exerciseId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showCompleteWorkoutDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_complete_workout, null)
        
        // Init dialog elements
        val caloriesEditText = view.findViewById<EditText>(R.id.edit_calories)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Complete Workout")
            .setMessage("Great job! Enter any additional information below.")
            .setView(view)
            .setPositiveButton("Complete") { _, _ ->
                val calories = caloriesEditText.text.toString().toIntOrNull() ?: 0
                workoutViewModel.completeWorkout(calories)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun confirmCancelWorkout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Workout")
            .setMessage("Are you sure you want to cancel this workout? All progress will be lost.")
            .setPositiveButton("Yes, Cancel") { _, _ ->
                workoutViewModel.cancelWorkout()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
