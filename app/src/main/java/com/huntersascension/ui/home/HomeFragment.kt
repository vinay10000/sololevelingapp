package com.huntersascension.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.huntersascension.R
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.UserRepository
import com.huntersascension.data.WorkoutRepository
import com.huntersascension.databinding.FragmentHomeBinding
import com.huntersascension.utils.ExpCalculator
import com.huntersascension.utils.PreferenceManager
import com.huntersascension.utils.RankManager
import com.huntersascension.utils.StatCalculator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels {
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
        
        HomeViewModel.HomeViewModelFactory(
            userRepository,
            workoutRepository,
            preferenceManager,
            rankManager
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up button click listeners
        binding.btnStartWorkout.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_workout)
        }
        
        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_profile)
        }
        
        binding.btnRankUp.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_rankUp)
        }
        
        // Observe the current user
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user == null) {
                // If no user is logged in, go back to login screen
                findNavController().navigate(R.id.action_home_to_login)
                return@observe
            }
            
            // Set welcome message
            binding.tvWelcome.text = getString(R.string.welcome_hunter, user.username)
            
            // Set rank data
            val rankColor = when (user.rank) {
                "E" -> R.color.rank_e
                "D" -> R.color.rank_d
                "C" -> R.color.rank_c
                "B" -> R.color.rank_b
                "A" -> R.color.rank_a
                "S" -> R.color.rank_s
                else -> R.color.rank_e
            }
            
            binding.tvCurrentRank.text = user.getRankDisplayName()
            binding.tvCurrentRank.setTextColor(resources.getColor(rankColor, null))
            
            // Set EXP to rank up
            val nextExpTarget = viewModel.getNextExpTarget()
            if (nextExpTarget != null) {
                binding.tvExpToRankup.text = getString(R.string.exp_to_rankup, nextExpTarget - user.exp)
                binding.progressRank.max = nextExpTarget
                binding.progressRank.progress = user.exp
            } else {
                // Max rank reached
                binding.tvExpToRankup.text = "Maximum rank achieved!"
                binding.progressRank.max = 100
                binding.progressRank.progress = 100
            }
            
            // Set stats
            binding.tvStatStr.text = user.strStat.toString()
            binding.tvStatAgi.text = user.agiStat.toString()
            binding.tvStatVit.text = user.vitStat.toString()
            binding.tvStatInt.text = user.intStat.toString()
            binding.tvStatLuk.text = user.lukStat.toString()
            
            // Set streak
            binding.tvStreak.text = getString(R.string.streak_counter, user.streak)
            
            // Show rank up button if eligible
            viewModel.checkRankUpEligibility()
        }
        
        // Observe rank up eligibility
        viewModel.isEligibleForRankUp.observe(viewLifecycleOwner) { isEligible ->
            binding.btnRankUp.visibility = if (isEligible) View.VISIBLE else View.GONE
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Reload user data when returning to this fragment
        viewModel.loadCurrentUser()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
