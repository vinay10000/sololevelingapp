package com.huntersascension.ui.leaderboard

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.dao.social.LeaderboardUserWithRank
import com.huntersascension.data.model.social.LeaderboardType
import com.huntersascension.ui.leaderboard.adapters.AnimatedLeaderboardAdapter
import com.huntersascension.ui.util.ViewModelFactory

/**
 * Fragment for displaying animated friend leaderboard
 */
class FriendLeaderboardFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var viewModel: FriendLeaderboardViewModel
    
    // UI Components
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var spinner: Spinner
    private lateinit var textYourRank: TextView
    
    // Adapter
    private lateinit var leaderboardAdapter: AnimatedLeaderboardAdapter
    
    // Top 3 podium elements
    private lateinit var firstPlaceImage: ImageView
    private lateinit var firstPlaceName: TextView
    private lateinit var firstPlaceScore: TextView
    private lateinit var secondPlaceImage: ImageView
    private lateinit var secondPlaceName: TextView
    private lateinit var secondPlaceScore: TextView
    private lateinit var thirdPlaceImage: ImageView
    private lateinit var thirdPlaceName: TextView
    private lateinit var thirdPlaceScore: TextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friend_leaderboard, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireActivity().application)
        ).get(FriendLeaderboardViewModel::class.java)
        
        // Initialize UI components
        recyclerView = view.findViewById(R.id.recycler_friends_leaderboard)
        emptyText = view.findViewById(R.id.text_empty_friends)
        progressBar = view.findViewById(R.id.progress_bar)
        spinner = view.findViewById(R.id.spinner_leaderboard_type)
        textYourRank = view.findViewById(R.id.text_your_rank)
        
        // Initialize podium elements
        firstPlaceImage = view.findViewById(R.id.image_first_place)
        firstPlaceName = view.findViewById(R.id.text_first_name)
        firstPlaceScore = view.findViewById(R.id.text_first_score)
        secondPlaceImage = view.findViewById(R.id.image_second_place)
        secondPlaceName = view.findViewById(R.id.text_second_name)
        secondPlaceScore = view.findViewById(R.id.text_second_score)
        thirdPlaceImage = view.findViewById(R.id.image_third_place)
        thirdPlaceName = view.findViewById(R.id.text_third_name)
        thirdPlaceScore = view.findViewById(R.id.text_third_score)
        
        // Setup spinner
        setupSpinner()
        
        // Setup adapter
        setupAdapter()
        
        // Set current user from shared preferences or arguments
        viewModel.setCurrentUser("current_user_placeholder") // Replace with actual logic
        
        // Observe LiveData
        observeViewModel()
    }
    
    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.leaderboard_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }
    
    private fun setupAdapter() {
        leaderboardAdapter = AnimatedLeaderboardAdapter(
            onUserClick = { entry -> navigateToUserProfile(entry) },
            currentUsername = viewModel.currentUsername.value ?: ""
        )
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = leaderboardAdapter
    }
    
    private fun observeViewModel() {
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
        
        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner, { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        })
        
        // Observe current username
        viewModel.currentUsername.observe(viewLifecycleOwner, { username ->
            leaderboardAdapter.currentUsername = username
        })
        
        // Observe leaderboard entries
        viewModel.friendLeaderboardEntries.observe(viewLifecycleOwner, { entries ->
            if (entries.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyText.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyText.visibility = View.GONE
                
                // Update adapter with new entries
                leaderboardAdapter.submitList(entries)
                
                // Animate entries
                recyclerView.post {
                    leaderboardAdapter.animateItems(recyclerView)
                }
            }
        })
        
        // Observe top 3 friends
        viewModel.topFriends.observe(viewLifecycleOwner, { top3 ->
            updatePodium(top3)
        })
        
        // Observe current user's rank
        viewModel.currentUserRank.observe(viewLifecycleOwner, { userRank ->
            if (userRank != null) {
                textYourRank.text = "Your Rank: #${userRank.rank}"
            } else {
                textYourRank.text = "Your Rank: N/A"
            }
        })
    }
    
    /**
     * Update the podium with the top 3 users
     */
    private fun updatePodium(top3: List<LeaderboardUserWithRank>) {
        // Reset visibility
        firstPlaceImage.visibility = View.INVISIBLE
        firstPlaceName.visibility = View.INVISIBLE
        firstPlaceScore.visibility = View.INVISIBLE
        secondPlaceImage.visibility = View.INVISIBLE
        secondPlaceName.visibility = View.INVISIBLE
        secondPlaceScore.visibility = View.INVISIBLE
        thirdPlaceImage.visibility = View.INVISIBLE
        thirdPlaceName.visibility = View.INVISIBLE
        thirdPlaceScore.visibility = View.INVISIBLE
        
        // Update with available data
        for (entry in top3) {
            val user = entry.user
            when (entry.rank) {
                1 -> {
                    firstPlaceName.text = user.getDisplayName()
                    firstPlaceScore.text = "Score: ${entry.score}"
                    animatePodiumElement(firstPlaceImage, firstPlaceName, firstPlaceScore, 0)
                }
                2 -> {
                    secondPlaceName.text = user.getDisplayName()
                    secondPlaceScore.text = "Score: ${entry.score}"
                    animatePodiumElement(secondPlaceImage, secondPlaceName, secondPlaceScore, 100)
                }
                3 -> {
                    thirdPlaceName.text = user.getDisplayName()
                    thirdPlaceScore.text = "Score: ${entry.score}"
                    animatePodiumElement(thirdPlaceImage, thirdPlaceName, thirdPlaceScore, 200)
                }
            }
        }
    }
    
    /**
     * Animate a podium element with staggered entrance
     */
    private fun animatePodiumElement(
        image: ImageView,
        name: TextView,
        score: TextView,
        delay: Long
    ) {
        image.visibility = View.VISIBLE
        name.visibility = View.VISIBLE
        score.visibility = View.VISIBLE
        
        // Reset properties for animation
        image.scaleX = 0f
        image.scaleY = 0f
        image.alpha = 0f
        name.alpha = 0f
        score.alpha = 0f
        
        // Animate image with bounce effect
        image.postDelayed({
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f)
            val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
            
            val animator = ObjectAnimator.ofPropertyValuesHolder(image, scaleX, scaleY, alpha)
            animator.duration = 500
            animator.interpolator = OvershootInterpolator(1.5f)
            animator.start()
            
            // Animate name and score with fade-in
            name.animate().alpha(1f).setDuration(300).setStartDelay(200).start()
            score.animate().alpha(1f).setDuration(300).setStartDelay(300).start()
        }, delay)
    }
    
    /**
     * Handle navigation to user profile
     */
    private fun navigateToUserProfile(entry: LeaderboardUserWithRank) {
        // Implement navigation to user profile
        Toast.makeText(
            requireContext(),
            "Viewing ${entry.user.getDisplayName()}'s profile",
            Toast.LENGTH_SHORT
        ).show()
    }
    
    /**
     * Spinner item selection handler
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val leaderboardType = when (position) {
            0 -> LeaderboardType.TOTAL_EXP
            1 -> LeaderboardType.WEEKLY_EXP
            2 -> LeaderboardType.STREAK
            3 -> LeaderboardType.STRENGTH
            4 -> LeaderboardType.ENDURANCE
            5 -> LeaderboardType.AGILITY
            6 -> LeaderboardType.VITALITY
            7 -> LeaderboardType.INTELLIGENCE
            8 -> LeaderboardType.LUCK
            else -> LeaderboardType.TOTAL_EXP
        }
        
        // Update the selected leaderboard type
        viewModel.selectLeaderboardType(leaderboardType)
    }
    
    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing
    }
    
    companion object {
        fun newInstance() = FriendLeaderboardFragment()
    }
}
