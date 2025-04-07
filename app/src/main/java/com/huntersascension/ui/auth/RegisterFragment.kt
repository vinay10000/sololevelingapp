package com.huntersascension.ui.auth

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
import com.huntersascension.databinding.FragmentRegisterBinding
import com.huntersascension.utils.ExpCalculator
import com.huntersascension.utils.PreferenceManager
import com.huntersascension.utils.RankManager
import com.huntersascension.utils.StatCalculator

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AuthViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val userDao = database.userDao()
        val workoutDao = database.workoutDao()
        val trophyDao = database.trophyDao()
        val rankManager = RankManager()
        val statCalculator = StatCalculator()
        val expCalculator = ExpCalculator()
        val preferenceManager = PreferenceManager(requireContext())
        
        AuthViewModel.AuthViewModelFactory(
            UserRepository(userDao, workoutDao, trophyDao, rankManager, statCalculator, expCalculator),
            preferenceManager
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up click listeners
        binding.btnRegister.setOnClickListener {
            attemptRegistration()
        }
        
        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
        
        // Observe registration result
        viewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            
            if (result.isSuccess) {
                // Auto-login and navigate to home screen
                val username = binding.etUsername.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                viewModel.login(username, password)
            } else {
                // Show error
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = result.exceptionOrNull()?.message ?: getString(R.string.error_register)
            }
        }
        
        // Observe login result after registration
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                // Navigate to home screen
                findNavController().navigate(R.id.action_register_to_home)
            }
        }
    }
    
    private fun attemptRegistration() {
        // Hide error if visible
        binding.tvError.visibility = View.GONE
        
        // Get input values
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = "Please enter both username and password"
            return
        }
        
        // Password strength validation
        if (password.length < 6) {
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = "Password must be at least 6 characters long"
            return
        }
        
        // Show loading indicator
        binding.progressBar.visibility = View.VISIBLE
        
        // Attempt registration
        viewModel.register(username, password)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
