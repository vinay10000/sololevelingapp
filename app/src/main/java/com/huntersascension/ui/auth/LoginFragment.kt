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

class LoginFragment : Fragment() {
    
    private lateinit var authViewModel: AuthViewModel
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var loadingProgressBar: ProgressBar
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        
        emailEditText = view.findViewById(R.id.username)
        passwordEditText = view.findViewById(R.id.password)
        loginButton = view.findViewById(R.id.login)
        registerLink = view.findViewById(R.id.register_link)
        loadingProgressBar = view.findViewById(R.id.loading)
        
        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val userDao = AppDatabase.getDatabase(requireContext()).userDao()
        val userRepository = UserRepository(userDao)
        
        // Create AuthViewModel (in a real app, use ViewModelFactory)
        authViewModel = AuthViewModel(userRepository)
        
        authViewModel.loginStatus.observe(viewLifecycleOwner, Observer { result ->
            loadingProgressBar.visibility = View.GONE
            
            when (result) {
                is AuthViewModel.LoginResult.Success -> {
                    // Navigate to home screen
                    findNavController().navigate(R.id.action_login_to_home)
                }
                is AuthViewModel.LoginResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        
        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            authViewModel.login(email, password)
        }
        
        registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }
}
