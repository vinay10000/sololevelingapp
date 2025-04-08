package com.huntersascension.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.huntersascension.R

/**
 * Fragment showing list of available workouts
 */
class WorkoutListFragment : Fragment() {
    
    private lateinit var workoutViewModel: WorkoutViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        workoutViewModel = ViewModelProvider(this)[WorkoutViewModel::class.java]
        
        val root = inflater.inflate(R.layout.fragment_workout_list, container, false)
        
        // Setup RecyclerView and observe ViewModel data
        
        return root
    }
}
