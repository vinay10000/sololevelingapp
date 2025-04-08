package com.huntersascension.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.model.social.LeaderboardType
import com.huntersascension.ui.leaderboard.adapters.LeaderboardAdapter
import com.huntersascension.ui.leaderboard.adapters.LeaderboardListAdapter
import com.huntersascension.ui.util.ViewModelFactory

/**
 * Fragment for displaying global leaderboards
 */
class LeaderboardFragment : Fragment() {

    private lateinit var viewModel: LeaderboardViewModel
    
    // UI Components
    private lateinit var recyclerViewLeaderboards: RecyclerView
    private lateinit var recyclerViewRankings: RecyclerView
    private lateinit var buttonFriendRankings: Button
    private lateinit var textCurrentRank: TextView
    
    // Adapters
    private lateinit var leaderboardListAdapter: LeaderboardListAdapter
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireActivity().application)
        ).get(LeaderboardViewModel::class.java)
        
        // Initialize UI components
        recyclerViewLeaderboards = view.findViewById(R.id.recycler_leaderboards)
        recyclerViewRankings = view.findViewById(R.id.recycler_rankings)
        buttonFriendRankings = view.findViewById(R.id.button_friend_rankings)
        textCurrentRank = view.findViewById(R.id.text_current_rank)
        
        // Setup adapters
        setupAdapters()
        
        // Set current user from shared preferences or arguments
        viewModel.setCurrentUser("current_user_placeholder") // Replace with actual logic
        
        // Setup button listeners
        setupListeners()
        
        // Observe LiveData
        observeViewModel()
    }
    
    private fun setupAdapters() {
        // Setup leaderboard list adapter
        leaderboardListAdapter = LeaderboardListAdapter { leaderboard ->
            viewModel.loadLeaderboard(leaderboard.leaderboardId)
        }
        
        recyclerViewLeaderboards.layoutManager = 
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewLeaderboards.adapter = leaderboardListAdapter
        
        // Setup leaderboard adapter
        leaderboardAdapter = LeaderboardAdapter(
            onUserClick = { entry -> navigateToUserProfile(entry.user.username) },
            currentUsername = viewModel.currentUsername.value ?: "",
            highlightTop3 = true
        )
        
        recyclerViewRankings.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewRankings.adapter = leaderboardAdapter
    }
    
    private fun setupListeners() {
        // Friend rankings button
        buttonFriendRankings.setOnClickListener {
            // Navigate to friend leaderboard fragment
            findNavController().navigate(R.id.navigation_friend_leaderboard)
        }
    }
    
    private fun observeViewModel() {
        // Observe available leaderboards
        viewModel.availableLeaderboards.observe(viewLifecycleOwner, { leaderboards ->
            leaderboardListAdapter.submitList(leaderboards)
            
            // If we have leaderboards and none are selected, select the first one
            if (leaderboards.isNotEmpty() && viewModel.selectedLeaderboard.value == null) {
                viewModel.loadLeaderboard(leaderboards[0].leaderboardId)
            }
        })
        
        // Observe top users
        viewModel.leaderboardTopUsers.observe(viewLifecycleOwner, { users ->
            leaderboardAdapter.submitList(users)
        })
        
        // Observe current user's rank
        viewModel.currentUserRank.observe(viewLifecycleOwner, { userRank ->
            if (userRank != null) {
                textCurrentRank.text = "Your Rank: #${userRank.rank} (Score: ${userRank.score})"
                textCurrentRank.visibility = View.VISIBLE
            } else {
                textCurrentRank.visibility = View.GONE
            }
        })
        
        // Observe selected leaderboard
        viewModel.selectedLeaderboard.observe(viewLifecycleOwner, { leaderboard ->
            // Update UI for selected leaderboard
        })
    }
    
    /**
     * Navigate to user profile
     */
    private fun navigateToUserProfile(username: String) {
        // Implement navigation to user profile
    }
    
    companion object {
        fun newInstance() = LeaderboardFragment()
    }
}
