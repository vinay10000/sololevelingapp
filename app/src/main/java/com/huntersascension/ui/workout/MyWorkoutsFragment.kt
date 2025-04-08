package com.huntersascension.ui.workout

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.huntersascension.R
import com.huntersascension.data.model.Workout
import com.huntersascension.data.model.WorkoutExercise
import com.huntersascension.ui.adapter.WorkoutAdapter

/**
 * Fragment for displaying user's workouts
 */
class MyWorkoutsFragment : Fragment() {
    
    private val viewModel: WorkoutViewModel by activityViewModels()
    
    private lateinit var recyclerWorkouts: RecyclerView
    private lateinit var textNoWorkouts: TextView
    private lateinit var chipGroupFilter: ChipGroup
    private lateinit var fabAddWorkout: FloatingActionButton
    
    private lateinit var workoutAdapter: WorkoutAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_workouts, container, false)
        
        recyclerWorkouts = view.findViewById(R.id.recycler_workouts)
        textNoWorkouts = view.findViewById(R.id.text_no_workouts)
        chipGroupFilter = view.findViewById(R.id.chip_group_filter)
        fabAddWorkout = view.findViewById(R.id.fab_add_workout)
        
        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupFilterChips()
        setupFab()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        workoutAdapter = WorkoutAdapter(
            onStartClicked = { workout -> startWorkout(workout) },
            onEditClicked = { workout -> editWorkout(workout) },
            onDeleteClicked = { workout -> confirmDeleteWorkout(workout) }
        )
        
        recyclerWorkouts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = workoutAdapter
        }
    }
    
    private fun setupFilterChips() {
        chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedChipId = checkedIds[0]
                val selectedChip = group.findViewById<Chip>(selectedChipId)
                val filterType = selectedChip.text.toString()
                viewModel.filterWorkouts(filterType)
            } else {
                // If no chip is selected, select the "All" chip
                val chipAll = group.findViewById<Chip>(R.id.chip_all)
                chipAll.isChecked = true
            }
        }
    }
    
    private fun setupFab() {
        fabAddWorkout.setOnClickListener {
            // Navigate to the Create Workout tab
            val viewPager = requireParentFragment().view?.findViewById<androidx.viewpager2.widget.ViewPager2>(
                R.id.view_pager_workout
            )
            viewPager?.currentItem = 1
        }
    }
    
    private fun observeViewModel() {
        // Observe filtered workouts
        viewModel.filteredWorkouts.observe(viewLifecycleOwner) { workouts ->
            updateWorkoutsUI(workouts)
        }
        
        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide loading indicator if needed
        }
    }
    
    private fun updateWorkoutsUI(workouts: List<Workout>) {
        workoutAdapter.submitList(workouts)
        
        if (workouts.isEmpty()) {
            textNoWorkouts.visibility = View.VISIBLE
            recyclerWorkouts.visibility = View.GONE
        } else {
            textNoWorkouts.visibility = View.GONE
            recyclerWorkouts.visibility = View.VISIBLE
            
            // For each workout, get the exercise count
            workouts.forEachIndexed { index, workout ->
                viewModel.loadWorkoutExercises(workout.id)
                viewModel.workoutExercises.observe(viewLifecycleOwner) { exercises ->
                    workoutAdapter.setExerciseCount(index, exercises.size)
                }
            }
        }
    }
    
    private fun startWorkout(workout: Workout) {
        viewModel.startWorkout(workout.id)
        
        // Show active workout dialog
        val dialogFragment = ActiveWorkoutDialogFragment()
        dialogFragment.show(childFragmentManager, "active_workout")
    }
    
    private fun editWorkout(workout: Workout) {
        // Navigate to edit workout dialog
        val dialogFragment = EditWorkoutDialogFragment.newInstance(workout.id)
        dialogFragment.show(childFragmentManager, "edit_workout")
    }
    
    private fun confirmDeleteWorkout(workout: Workout) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Workout")
            .setMessage("Are you sure you want to delete '${workout.name}'? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteWorkout(workout)
                Snackbar.make(requireView(), "Workout deleted", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
