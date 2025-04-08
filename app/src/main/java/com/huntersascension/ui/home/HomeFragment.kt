package com.huntersascension.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.huntersascension.R

/**
 * Fragment showing the home screen with stats, streaks, and recent activity
 */
class HomeFragment : Fragment() {
    
    private lateinit var homeViewModel: HomeViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        
        // Setup views and observe ViewModel data
        
        return root
    }
}
