package com.huntersascension.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.huntersascension.R
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.UserRepository
import com.huntersascension.data.WorkoutRepository
import com.huntersascension.data.entity.Workout
import com.huntersascension.databinding.FragmentWorkoutBinding
import com.huntersascension.utils.ExpCalculator
import com.huntersascension.utils.PreferenceManager
import com.huntersascension.utils.RankManager
import com.huntersascension.utils.StatCalculator

class WorkoutFragment : Fragment() {

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: WorkoutViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val userDao = database.userDao()
        val workoutDao = database.workoutDao()
        val trophyDao = database.trophyDao()
        val rankManager = RankManager()
        val statCalculator = StatCalculator()
        val expCalculator = ExpCalculator()
        val preferenceManager = PreferenceManager(requireContext())
        
        val userRepository = UserRepository(userDao, workoutDao, trophyDao, rankManager, statCalculator, expCalculator)
        val workoutRepository = WorkoutRepository(workoutDao, userDao, trophyDao, expCalculator, statCalculator)
        
        WorkoutViewModel.WorkoutViewModelFactory(
            workoutRepository,
            userRepository,
            preferenceManager
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up button click listeners
        binding.btnStartWorkout.setOnClickListener {
            startWorkout()
        }
        
        binding.btnCompleteWorkout.setOnClickListener {
            completeWorkout()
        }
        
        binding.btnCancelWorkout.setOnClickListener {
            cancelWorkout()
        }
        
        // Observe workout state
        viewModel.workoutState.observe(viewLifecycleOwner) { state ->
            updateUIForWorkoutState(state)
        }
        
        // Observe completed workout
        viewModel.completedWorkout.observe(viewLifecycleOwner) { workout ->
            workout?.let {
                showWorkoutResult(it)
            }
        }
    }
    
    private fun updateUIForWorkoutState(state: WorkoutViewModel.WorkoutState) {
        when (state) {
            WorkoutViewModel.WorkoutState.IDLE -> {
                // In idle state, show setup UI
                binding.btnStartWorkout.visibility = View.VISIBLE
                binding.btnCompleteWorkout.visibility = View.GONE
                binding.btnCancelWorkout.visibility = View.GONE
                binding.tvExpGained.visibility = View.GONE
                
                // Enable input controls
                binding.tilReps.isEnabled = true
                binding.rgExerciseType.isEnabled = true
                binding.rgDifficulty.isEnabled = true
            }
            
            WorkoutViewModel.WorkoutState.IN_PROGRESS -> {
                // In progress state, show workout in progress UI
                binding.btnStartWorkout.visibility = View.GONE
                binding.btnCompleteWorkout.visibility = View.VISIBLE
                binding.btnCancelWorkout.visibility = View.VISIBLE
                binding.tvExpGained.visibility = View.GONE
                
                // Disable input controls during workout
                binding.tilReps.isEnabled = false
                binding.rgExerciseType.isEnabled = false
                binding.rgDifficulty.isEnabled = false
            }
            
            WorkoutViewModel.WorkoutState.COMPLETED -> {
                // In completed state, show result UI
                binding.btnStartWorkout.visibility = View.VISIBLE
                binding.btnCompleteWorkout.visibility = View.GONE
                binding.btnCancelWorkout.visibility = View.GONE
                binding.tvExpGained.visibility = View.VISIBLE
                
                // Re-enable input controls
                binding.tilReps.isEnabled = true
                binding.rgExerciseType.isEnabled = true
                binding.rgDifficulty.isEnabled = true
            }
        }
    }
    
    private fun startWorkout() {
        // Make sure exercise type is selected
        if (binding.rgExerciseType.checkedRadioButtonId == -1) {
            showError("Please select an exercise type")
            return
        }
        
        // Make sure difficulty is selected
        if (binding.rgDifficulty.checkedRadioButtonId == -1) {
            showError("Please select a difficulty level")
            return
        }
        
        // Get number of reps
        val repsStr = binding.etReps.text.toString().trim()
        if (repsStr.isEmpty()) {
            showError("Please enter the number of reps")
            return
        }
        
        val reps = repsStr.toIntOrNull()
        if (reps == null || reps <= 0) {
            showError("Please enter a valid number of reps")
            return
        }
        
        // Get selected exercise type
        val exerciseType = when (binding.rgExerciseType.checkedRadioButtonId) {
            R.id.rb_pushups -> Workout.EXERCISE_PUSHUPS
            R.id.rb_squats -> Workout.EXERCISE_SQUATS
            R.id.rb_running -> Workout.EXERCISE_RUNNING
            else -> Workout.EXERCISE_PUSHUPS
        }
        
        // Get selected difficulty
        val difficulty = when (binding.rgDifficulty.checkedRadioButtonId) {
            R.id.rb_easy -> Workout.DIFFICULTY_EASY
            R.id.rb_medium -> Workout.DIFFICULTY_MEDIUM
            R.id.rb_hard -> Workout.DIFFICULTY_HARD
            R.id.rb_boss -> Workout.DIFFICULTY_BOSS
            else -> Workout.DIFFICULTY_EASY
        }
        
        // Start workout
        viewModel.startWorkout(exerciseType, difficulty, reps)
    }
    
    private fun completeWorkout() {
        viewModel.completeWorkout()
    }
    
    private fun cancelWorkout() {
        viewModel.cancelWorkout()
    }
    
    private fun showWorkoutResult(workout: Workout) {
        binding.tvExpGained.text = getString(R.string.exp_gained, workout.expGained)
    }
    
    private fun showError(message: String) {
        // Could use Snackbar or Toast here
        binding.tvExpGained.text = message
        binding.tvExpGained.visibility = View.VISIBLE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
