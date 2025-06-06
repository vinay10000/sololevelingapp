package com.huntersascension.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.huntersascension.R

class ProfileFragment : Fragment() {
    
    private lateinit var profileViewModel: ProfileViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
}
