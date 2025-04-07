package com.huntersascension.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.huntersascension.R
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.UserRepository
import com.huntersascension.data.WorkoutRepository
import com.huntersascension.databinding.FragmentProfileBinding
import com.huntersascension.utils.ExpCalculator
import com.huntersascension.utils.PreferenceManager
import com.huntersascension.utils.RankManager
import com.huntersascension.utils.StatCalculator
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var trophyAdapter: TrophyAdapter
    
    private val viewModel: ProfileViewModel by viewModels {
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
        
        ProfileViewModel.ProfileViewModelFactory(
            userRepository,
            workoutRepository,
            preferenceManager
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up back button
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Set up RecyclerView for trophies
        trophyAdapter = TrophyAdapter()
        binding.rvTrophies.apply {
            adapter = trophyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        
        // Observe current user
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user == null) {
                // If no user is logged in, go back to login screen
                findNavController().navigateUp()
                return@observe
            }
            
            // Set hunter name
            binding.tvHunterName.text = "Hunter: ${user.username}"
            
            // Set rank display
            val rankColor = when (user.rank) {
                "E" -> R.color.rank_e
                "D" -> R.color.rank_d
                "C" -> R.color.rank_c
                "B" -> R.color.rank_b
                "A" -> R.color.rank_a
                "S" -> R.color.rank_s
                else -> R.color.rank_e
            }
            
            binding.tvHunterRank.text = user.getRankDisplayName()
            binding.tvHunterRank.setTextColor(resources.getColor(rankColor, null))
            
            // Set exp display
            binding.tvExp.text = "EXP: ${user.exp}"
            
            // Set up radar chart for stats (using CircularProgressBar as a placeholder)
            setupStatChart(user.strStat)
            
            // Set individual stats
            binding.tvStatStr.text = user.strStat.toString()
            binding.tvStatAgi.text = user.agiStat.toString()
            binding.tvStatVit.text = user.vitStat.toString()
            binding.tvStatInt.text = user.intStat.toString()
            binding.tvStatLuk.text = user.lukStat.toString()
        }
        
        // Observe trophies
        viewModel.trophies.observe(viewLifecycleOwner) { trophies ->
            if (trophies.isEmpty()) {
                binding.tvNoTrophies.visibility = View.VISIBLE
                binding.rvTrophies.visibility = View.GONE
            } else {
                binding.tvNoTrophies.visibility = View.GONE
                binding.rvTrophies.visibility = View.VISIBLE
                trophyAdapter.submitList(trophies)
            }
        }
        
        // Load user data
        viewModel.loadUserData()
    }
    
    private fun setupStatChart(primaryStat: Int) {
        // This is a simplified implementation using CircularProgressBar
        // In a real implementation, you might want to use a radar chart
        // for better visualization of all stats
        binding.statChart.apply {
            // Set progress (0-100 scale)
            progress = minOf(primaryStat.toFloat(), 100f)
            
            // Set progress color based on rank
            progressBarColor = resources.getColor(R.color.purple_blue_primary, null)
            
            // Set background color
            backgroundProgressBarColor = resources.getColor(R.color.medium_gray, null)
            
            // Add animation
            setProgressWithAnimation(progress, 1000)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
