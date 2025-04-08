package com.huntersascension.ui.achievement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.huntersascension.R

class AchievementFragment : Fragment() {
    
    private lateinit var achievementViewModel: AchievementViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        achievementViewModel = ViewModelProvider(this)[AchievementViewModel::class.java]
        return inflater.inflate(R.layout.fragment_achievement, container, false)
    }
}
