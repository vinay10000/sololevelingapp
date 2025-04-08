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
import com.huntersascension.data.dao.social.SocialShareWithUser
import com.huntersascension.data.model.social.SharedContentType
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying social feed items
 */
class SocialFeedAdapter(
    private val onShareClick: (SocialShareWithUser) -> Unit,
    private val onLikeClick: (SocialShareWithUser) -> Unit,
    private val onCommentClick: (SocialShareWithUser) -> Unit,
    private val onUserClick: (String) -> Unit,
    private val onDeleteClick: (SocialShareWithUser) -> Unit,
    private val currentUsername: String
) : ListAdapter<SocialShareWithUser, SocialFeedAdapter.SocialShareViewHolder>(SocialShareDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialShareViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_social_post, parent, false)
        return SocialShareViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: SocialShareViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class SocialShareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textUsername: TextView = itemView.findViewById(R.id.text_username)
        private val textTimestamp: TextView = itemView.findViewById(R.id.text_timestamp)
        private val textContent: TextView = itemView.findViewById(R.id.text_content)
        private val textLikeCount: TextView = itemView.findViewById(R.id.text_like_count)
        private val textCommentCount: TextView = itemView.findViewById(R.id.text_comment_count)
        private val buttonLike: Button = itemView.findViewById(R.id.button_like)
        private val buttonComment: Button = itemView.findViewById(R.id.button_comment)
        private val imageShare: ImageView = itemView.findViewById(R.id.image_share_content)
        private val buttonDelete: Button = itemView.findViewById(R.id.button_delete)
        
        private val dateFormat = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
        
        fun bind(socialShareWithUser: SocialShareWithUser) {
            val share = socialShareWithUser.share
            val user = socialShareWithUser.user
            val isLiked = socialShareWithUser.isLikedByCurrentUser
            
            // Set user info
            textUsername.text = user.getDisplayName()
            textUsername.setOnClickListener { onUserClick(user.username) }
            
            // Set share info
            textTimestamp.text = dateFormat.format(share.shareDate)
            textContent.text = share.message
            textLikeCount.text = share.likeCount.toString()
            textCommentCount.text = share.commentCount.toString()
            
            // Set like button state
            buttonLike.isSelected = isLiked
            buttonLike.text = if (isLiked) "Liked" else "Like"
            
            // Set visibility of delete button based on ownership
            buttonDelete.visibility = if (share.username == currentUsername) View.VISIBLE else View.GONE
            
            // Set visibility of share image and content type indicator
            when (share.contentType) {
                SharedContentType.ACHIEVEMENT_UNLOCKED -> {
                    imageShare.visibility = View.VISIBLE
                    imageShare.setImageResource(R.drawable.ic_achievement)
                }
                SharedContentType.WORKOUT_COMPLETE -> {
                    imageShare.visibility = View.VISIBLE
                    imageShare.setImageResource(R.drawable.ic_workout)
                }
                SharedContentType.RANK_UP -> {
                    imageShare.visibility = View.VISIBLE
                    imageShare.setImageResource(R.drawable.ic_rank_up)
                }
                SharedContentType.LEVEL_UP -> {
                    imageShare.visibility = View.VISIBLE
                    imageShare.setImageResource(R.drawable.ic_level_up)
                }
                else -> {
                    imageShare.visibility = View.GONE
                }
            }
            
            // Set click listeners
            itemView.setOnClickListener { onShareClick(socialShareWithUser) }
            buttonLike.setOnClickListener { onLikeClick(socialShareWithUser) }
            buttonComment.setOnClickListener { onCommentClick(socialShareWithUser) }
            buttonDelete.setOnClickListener { onDeleteClick(socialShareWithUser) }
        }
    }
    
    /**
     * DiffUtil.Callback for efficient RecyclerView updates
     */
    private class SocialShareDiffCallback : DiffUtil.ItemCallback<SocialShareWithUser>() {
        override fun areItemsTheSame(oldItem: SocialShareWithUser, newItem: SocialShareWithUser): Boolean {
            return oldItem.share.shareId == newItem.share.shareId
        }
        
        override fun areContentsTheSame(oldItem: SocialShareWithUser, newItem: SocialShareWithUser): Boolean {
            return oldItem.share == newItem.share && 
                   oldItem.isLikedByCurrentUser == newItem.isLikedByCurrentUser &&
                   oldItem.share.likeCount == newItem.share.likeCount &&
                   oldItem.share.commentCount == newItem.share.commentCount
        }
    }
}
