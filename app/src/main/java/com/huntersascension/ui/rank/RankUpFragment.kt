package com.huntersascension.ui.rank

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
import com.huntersascension.databinding.FragmentRankUpBinding
import com.huntersascension.utils.ExpCalculator
import com.huntersascension.utils.PreferenceManager
import com.huntersascension.utils.RankManager
import com.huntersascension.utils.StatCalculator

class RankUpFragment : Fragment() {

    private var _binding: FragmentRankUpBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: RankUpViewModel by viewModels {
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
        
        RankUpViewModel.RankUpViewModelFactory(
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
        _binding = FragmentRankUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up button click listeners
        binding.btnStartChallenge.setOnClickListener {
            viewModel.attemptRankUp()
        }
        
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Observe current user
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user == null) {
                // If no user is logged in, go back to home screen
                findNavController().navigateUp()
                return@observe
            }
            
            // Set current rank display
            val currentRankColor = when (user.rank) {
                "E" -> R.color.rank_e
                "D" -> R.color.rank_d
                "C" -> R.color.rank_c
                "B" -> R.color.rank_b
                "A" -> R.color.rank_a
                "S" -> R.color.rank_s
                else -> R.color.rank_e
            }
            
            binding.tvCurrentRank.text = user.getRankDisplayName()
            binding.tvCurrentRank.setTextColor(resources.getColor(currentRankColor, null))
            
            // Set next rank display if available
            val nextRank = user.getNextRank()
            if (nextRank != null) {
                val nextRankColor = when (nextRank) {
                    "D" -> R.color.rank_d
                    "C" -> R.color.rank_c
                    "B" -> R.color.rank_b
                    "A" -> R.color.rank_a
                    "S" -> R.color.rank_s
                    else -> R.color.rank_e
                }
                
                binding.tvNextRank.text = "→ ${user.getNextRankDisplayName()}"
                binding.tvNextRank.setTextColor(resources.getColor(nextRankColor, null))
                
                // Display rank up challenge info
                setupRankUpChallenge(user.rank, nextRank)
            } else {
                // No next rank available (S-rank already)
                binding.tvNextRank.text = "Maximum rank achieved!"
                binding.cardChallenge.visibility = View.GONE
                binding.btnStartChallenge.visibility = View.GONE
            }
        }
        
        // Observe rank up result
        viewModel.rankUpResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                if (result.isSuccess) {
                    // Show success UI
                    val user = result.getOrNull()
                    binding.tvCongrats.visibility = View.VISIBLE
                    binding.tvCongrats.text = getString(
                        R.string.rank_up_congrats,
                        user?.getRankDisplayName() ?: "Next Rank"
                    )
                    
                    // Disable start challenge button
                    binding.btnStartChallenge.visibility = View.GONE
                } else {
                    // Show failure UI
                    val errorMessage = result.exceptionOrNull()?.message ?: "Failed to rank up"
                    binding.tvCongrats.visibility = View.VISIBLE
                    binding.tvCongrats.text = errorMessage
                }
            }
        }
        
        // Load user data
        viewModel.loadUserData()
    }
    
    private fun setupRankUpChallenge(currentRank: String, nextRank: String) {
        // Set challenge title
        binding.tvChallengeTitle.text = "$nextRank-Rank Challenge"
        
        // Set challenge description
        val challengeDescription = viewModel.getRankUpChallengeDescription(nextRank)
        binding.tvChallengeDescription.text = challengeDescription
        
        // Set requirements
        val requirements = viewModel.getRankRequirements(nextRank)
        val currentExp = viewModel.currentUser.value?.exp ?: 0
        val expNeeded = requirements["EXP"] ?: 0
        
        val requirementsText = buildString {
            append("Requirements:\n")
            append("- Minimum ${expNeeded} EXP")
            if (currentExp < expNeeded) {
                append(" (need ${expNeeded - currentExp} more)")
            }
            append("\n")
            
            // Add stat requirements
            for ((stat, value) in requirements) {
                if (stat != "EXP") {
                    append("- $stat: $value")
                    
                    // Check if user meets this requirement
                    val userStat = when (stat) {
                        "STR" -> viewModel.currentUser.value?.strStat ?: 0
                        "AGI" -> viewModel.currentUser.value?.agiStat ?: 0
                        "VIT" -> viewModel.currentUser.value?.vitStat ?: 0
                        "INT" -> viewModel.currentUser.value?.intStat ?: 0
                        "LUK" -> viewModel.currentUser.value?.lukStat ?: 0
                        else -> 0
                    }
                    
                    if (userStat < value) {
                        append(" (need ${value - userStat} more)")
                    }
                    
                    append("\n")
                }
            }
        }
        
        binding.tvChallengeRequirements.text = requirementsText
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
