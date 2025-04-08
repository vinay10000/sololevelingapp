package com.huntersascension.ui.quest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.huntersascension.R

class QuestFragment : Fragment() {
    
    private lateinit var questViewModel: QuestViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        questViewModel = ViewModelProvider(this)[QuestViewModel::class.java]
        return inflater.inflate(R.layout.fragment_quest, container, false)
    }
}
