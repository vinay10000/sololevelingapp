package com.huntersascension.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.huntersascension.R
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.repository.UserRepository
import com.huntersascension.ui.auth.AuthViewModel

class HomeFragment : Fragment() {
    
    private lateinit var authViewModel: AuthViewModel
    private lateinit var welcomeTextView: TextView
    private lateinit var levelTextView: TextView
    private lateinit var rankTextView: TextView
    private lateinit var expProgressBar: ProgressBar
    private lateinit var expTextView: TextView
    private lateinit var startWorkoutButton: Button
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        
        welcomeTextView = view.findViewById(R.id.text_welcome)
        levelTextView = view.findViewById(R.id.text_level)
        rankTextView = view.findViewById(R.id.text_rank)
        expProgressBar = view.findViewById(R.id.progress_exp)
        expTextView = view.findViewById(R.id.text_exp)
        startWorkoutButton = view.findViewById(R.id.button_start_workout)
        
        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val userDao = AppDatabase.getDatabase(requireContext()).userDao()
        val userRepository = UserRepository(userDao)
        
        // Create AuthViewModel (in a real app, use ViewModelFactory)
        authViewModel = AuthViewModel(userRepository)
        
        authViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                welcomeTextView.text = "Welcome, ${user.username}!"
                levelTextView.text = "Level: ${user.level}"
                rankTextView.text = "Rank: ${user.rank}"
                
                // Calculate required XP for next level (simple formula)
                val requiredExp = user.level * 100
                expProgressBar.max = requiredExp
                expProgressBar.progress = user.experience
                expTextView.text = "EXP: ${user.experience}/${requiredExp}"
            } else {
                // User not logged in, redirect to login
                findNavController().navigate(R.id.navigation_login)
            }
        })
        
        startWorkoutButton.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_workout)
        }
    }
}
