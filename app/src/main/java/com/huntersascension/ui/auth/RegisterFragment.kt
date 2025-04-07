package com.huntersascension.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.huntersascension.R
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.repository.UserRepository

class RegisterFragment : Fragment() {
    
    private lateinit var authViewModel: AuthViewModel
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginLink: TextView
    private lateinit var loadingProgressBar: ProgressBar
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        
        usernameEditText = view.findViewById(R.id.username)
        emailEditText = view.findViewById(R.id.email)
        passwordEditText = view.findViewById(R.id.password)
        confirmPasswordEditText = view.findViewById(R.id.confirm_password)
        registerButton = view.findViewById(R.id.register)
        loginLink = view.findViewById(R.id.login_link)
        loadingProgressBar = view.findViewById(R.id.loading)
        
        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val userDao = AppDatabase.getDatabase(requireContext()).userDao()
        val userRepository = UserRepository(userDao)
        
        // Create AuthViewModel (in a real app, use ViewModelFactory)
        authViewModel = AuthViewModel(userRepository)
        
        authViewModel.registrationStatus.observe(viewLifecycleOwner, Observer { result ->
            loadingProgressBar.visibility = View.GONE
            
            when (result) {
                is AuthViewModel.RegistrationResult.Success -> {
                    // Navigate to home screen
                    findNavController().navigate(R.id.action_register_to_home)
                }
                is AuthViewModel.RegistrationResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        
        registerButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                loadingProgressBar.visibility = View.GONE
                return@setOnClickListener
            }
            
            authViewModel.register(email, username, password, confirmPassword)
        }
        
        loginLink.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
    }
}
