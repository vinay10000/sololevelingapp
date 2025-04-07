package com.huntersascension.ui.rankup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.huntersascension.R

class RankUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rank_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val newRank = arguments?.getString("newRank") ?: "E"
        
        view.findViewById<TextView>(R.id.rank_up_message)?.text = 
            "Congratulations! You've ranked up to $newRank-Rank!"
        
        view.findViewById<Button>(R.id.rank_up_continue)?.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
    }
}
