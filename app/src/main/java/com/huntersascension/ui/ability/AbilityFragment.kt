package com.huntersascension.ui.ability

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.huntersascension.R

class AbilityFragment : Fragment() {
    
    private lateinit var abilityViewModel: AbilityViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        abilityViewModel = ViewModelProvider(this)[AbilityViewModel::class.java]
        return inflater.inflate(R.layout.fragment_ability, container, false)
    }
}
