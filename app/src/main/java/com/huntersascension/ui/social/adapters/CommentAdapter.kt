package com.huntersascension.ui.social.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huntersascension.R
import com.huntersascension.data.dao.social.CommentWithUser
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying comments
 */
class CommentAdapter(
    private val onReplyClick: (CommentWithUser) -> Unit,
    private val onUserClick: (String) -> Unit,
    private val onDeleteClick: (CommentWithUser) -> Unit,
    private val currentUsername: String
) : ListAdapter<CommentWithUser, CommentAdapter.CommentViewHolder>(CommentDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textUsername: TextView = itemView.findViewById(R.id.text_username)
        private val textTimestamp: TextView = itemView.findViewById(R.id.text_timestamp)
        private val textContent: TextView = itemView.findViewById(R.id.text_content)
        private val buttonReply: Button = itemView.findViewById(R.id.button_reply)
        private val buttonDelete: Button = itemView.findViewById(R.id.button_delete)
        
        private val dateFormat = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
        
        fun bind(commentWithUser: CommentWithUser) {
            val comment = commentWithUser.comment
            val user = commentWithUser.user
            
            // Set user info
            textUsername.text = user.getDisplayName()
            textUsername.setOnClickListener { onUserClick(user.username) }
            
            // Set comment info
            textTimestamp.text = dateFormat.format(comment.commentDate)
            textContent.text = comment.content
            
            // Set visibility of delete button based on ownership
            buttonDelete.visibility = if (comment.username == currentUsername) View.VISIBLE else View.GONE
            
            // Set click listeners
            buttonReply.setOnClickListener { onReplyClick(commentWithUser) }
            buttonDelete.setOnClickListener { onDeleteClick(commentWithUser) }
        }
    }
    
    /**
     * DiffUtil.Callback for efficient RecyclerView updates
     */
    private class CommentDiffCallback : DiffUtil.ItemCallback<CommentWithUser>() {
        override fun areItemsTheSame(oldItem: CommentWithUser, newItem: CommentWithUser): Boolean {
            return oldItem.comment.commentId == newItem.comment.commentId
        }
        
        override fun areContentsTheSame(oldItem: CommentWithUser, newItem: CommentWithUser): Boolean {
            return oldItem.comment == newItem.comment && 
                   oldItem.isLikedByCurrentUser == newItem.isLikedByCurrentUser
        }
    }
}
