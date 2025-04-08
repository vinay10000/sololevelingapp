package com.huntersascension.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.huntersascension.R

/**
 * Fragment for the workout section with tabs
 */
class WorkoutFragment : Fragment() {
    
    private val viewModel: WorkoutViewModel by viewModels()
    
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout, container, false)
        
        tabLayout = view.findViewById(R.id.tabs_workout)
        viewPager = view.findViewById(R.id.view_pager_workout)
        
        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Create ViewPager adapter
        val adapter = WorkoutPagerAdapter(this)
        viewPager.adapter = adapter
        
        // Connect TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.my_workouts)
                1 -> getString(R.string.create_workout)
                2 -> getString(R.string.workout_stats)
                else -> ""
            }
        }.attach()
        
        // Load initial data
        // Normally we would get the current user ID from a UserManager or similar
        val currentUserId = 1L
        viewModel.loadUserWorkouts(currentUserId)
        viewModel.loadRecentWorkoutHistory(currentUserId)
        viewModel.loadWorkoutStats(currentUserId)
    }
    
    /**
     * ViewPager adapter for the workout tabs
     */
    private inner class WorkoutPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        
        override fun getItemCount(): Int = 3
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MyWorkoutsFragment()
                1 -> CreateWorkoutFragment()
                2 -> WorkoutStatsFragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}
