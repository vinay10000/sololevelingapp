package com.huntersascension.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.huntersascension.R
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.repository.UserRepository
import com.huntersascension.ui.auth.AuthViewModel

class ProfileFragment : Fragment() {
    
    private lateinit var authViewModel: AuthViewModel
    private lateinit var usernameTextView: TextView
    private lateinit var rankValueTextView: TextView
    private lateinit var signOutButton: Button
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        usernameTextView = view.findViewById(R.id.text_username)
        rankValueTextView = view.findViewById(R.id.text_rank_value)
        signOutButton = view.findViewById(R.id.button_sign_out)
        
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
                usernameTextView.text = user.username
                rankValueTextView.text = user.rank
                
                // Update rank color based on rank
                val rankColor = when (user.rank) {
                    "E" -> R.color.rank_e
                    "D" -> R.color.rank_d
                    "C" -> R.color.rank_c
                    "B" -> R.color.rank_b
                    "A" -> R.color.rank_a
                    "S" -> R.color.rank_s
                    else -> R.color.rank_e
                }
                rankValueTextView.setTextColor(resources.getColor(rankColor, null))
            }
        })
        
        signOutButton.setOnClickListener {
            authViewModel.logout()
            findNavController().navigate(R.id.navigation_login)
        }
    }
}
