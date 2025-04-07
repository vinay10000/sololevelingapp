package com.huntersascension.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.huntersascension.R
import com.huntersascension.data.AppDatabase
import com.huntersascension.data.repository.UserRepository
import com.huntersascension.ui.auth.AuthViewModel

class StatsFragment : Fragment() {
    
    private lateinit var authViewModel: AuthViewModel
    private lateinit var strengthTextView: TextView
    private lateinit var agilityTextView: TextView
    private lateinit var vitalityTextView: TextView
    private lateinit var intelligenceTextView: TextView
    private lateinit var luckTextView: TextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        
        strengthTextView = view.findViewById(R.id.text_strength)
        agilityTextView = view.findViewById(R.id.text_agility)
        vitalityTextView = view.findViewById(R.id.text_vitality)
        intelligenceTextView = view.findViewById(R.id.text_intelligence)
        luckTextView = view.findViewById(R.id.text_luck)
        
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
                strengthTextView.text = user.strength.toString()
                agilityTextView.text = user.agility.toString()
                vitalityTextView.text = user.vitality.toString()
                intelligenceTextView.text = user.intelligence.toString()
                luckTextView.text = user.luck.toString()
            }
        })
    }
}
