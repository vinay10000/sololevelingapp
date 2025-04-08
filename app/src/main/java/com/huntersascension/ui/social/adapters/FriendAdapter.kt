package com.huntersascension.ui.social.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.model.User

/**
 * Adapter for displaying friends and friend requests
 */
class FriendAdapter(
    private val isRequestList: Boolean = false,
    private val onFriendClick: (User) -> Unit,
    private val onPrimaryActionClick: (User) -> Unit,
    private val onSecondaryActionClick: (User) -> Unit
) : ListAdapter<User, FriendAdapter.FriendViewHolder>(FriendDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageAvatar: ImageView = itemView.findViewById(R.id.image_avatar)
        private val textUsername: TextView = itemView.findViewById(R.id.text_username)
        private val textRank: TextView = itemView.findViewById(R.id.text_rank)
        private val textLevel: TextView = itemView.findViewById(R.id.text_level)
        private val primaryButton: Button = itemView.findViewById(R.id.button_primary)
        private val secondaryButton: Button = itemView.findViewById(R.id.button_secondary)
        
        fun bind(user: User) {
            // Set user info
            textUsername.text = user.getDisplayName()
            textRank.text = "${user.rank}-Rank"
            textLevel.text = "Level ${user.level}"
            
            // Set button text based on whether this is a friend or a request
            if (isRequestList) {
                primaryButton.text = "Accept"
                secondaryButton.text = "Reject"
                secondaryButton.visibility = View.VISIBLE
            } else {
                primaryButton.text = "Remove"
                secondaryButton.text = "Block"
                secondaryButton.visibility = View.VISIBLE
            }
            
            // Set avatar if available
            // if (user.avatarPath != null) {
            //     Glide.with(itemView.context).load(user.avatarPath).into(imageAvatar)
            // }
            
            // Set click listeners
            itemView.setOnClickListener { onFriendClick(user) }
            primaryButton.setOnClickListener { onPrimaryActionClick(user) }
            secondaryButton.setOnClickListener { onSecondaryActionClick(user) }
        }
    }
    
    /**
     * DiffUtil.Callback for efficient RecyclerView updates
     */
    private class FriendDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.username == newItem.username
        }
        
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
