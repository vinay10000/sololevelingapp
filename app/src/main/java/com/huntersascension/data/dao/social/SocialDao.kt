package com.huntersascension.data.dao.social

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.social.*

/**
 * Data Access Object for social interactions
 */
@Dao
interface SocialDao {
    // Share methods
    @Transaction
    @Query("SELECT * FROM social_shares WHERE id = :shareId")
    fun getShareById(shareId: Long): LiveData<SocialShare?>
    
    @Transaction
    @Query("SELECT * FROM social_shares " +
           "WHERE isPublic = 1 " +
           "ORDER BY createdDate DESC LIMIT :limit")
    fun getPublicShares(limit: Int = 50): LiveData<List<SocialShare>>
    
    @Transaction
    @Query("SELECT s.* FROM social_shares s " +
           "WHERE s.username = :username " +
           "ORDER BY s.createdDate DESC")
    fun getUserShares(username: String): LiveData<List<SocialShare>>
    
    @Transaction
    @Query("SELECT s.* FROM social_shares s " +
           "JOIN friend_relations fr ON s.username = fr.friendUsername " +
           "WHERE fr.userUsername = :username AND fr.status = 'ACCEPTED' AND s.isPublic = 1 " +
           "UNION " +
           "SELECT s.* FROM social_shares s " +
           "JOIN friend_relations fr ON s.username = fr.userUsername " +
           "WHERE fr.friendUsername = :username AND fr.status = 'ACCEPTED' AND s.isPublic = 1 " +
           "UNION " +
           "SELECT s.* FROM social_shares s " +
           "WHERE s.username = :username " +
           "ORDER BY createdDate DESC LIMIT :limit")
    fun getFriendFeed(username: String, limit: Int = 50): LiveData<List<SocialShare>>
    
    @Insert
    suspend fun insertShare(share: SocialShare): Long
    
    @Update
    suspend fun updateShare(share: SocialShare)
    
    @Delete
    suspend fun deleteShare(share: SocialShare)
    
    // Like methods
    @Transaction
    @Query("SELECT * FROM social_likes WHERE shareId = :shareId")
    fun getLikesForShare(shareId: Long): LiveData<List<SocialLike>>
    
    @Transaction
    @Query("SELECT * FROM social_likes WHERE shareId = :shareId AND username = :username")
    fun getUserLike(shareId: Long, username: String): LiveData<SocialLike?>
    
    @Transaction
    @Query("SELECT COUNT(*) FROM social_likes WHERE shareId = :shareId")
    fun getLikeCount(shareId: Long): LiveData<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: SocialLike)
    
    @Delete
    suspend fun deleteLike(like: SocialLike)
    
    @Transaction
    @Query("DELETE FROM social_likes WHERE shareId = :shareId AND username = :username")
    suspend fun deleteLikeByShareAndUser(shareId: Long, username: String)
    
    // Comment methods
    @Transaction
    @Query("SELECT * FROM social_comments WHERE id = :commentId")
    fun getCommentById(commentId: Long): LiveData<SocialComment?>
    
    @Transaction
    @Query("SELECT * FROM social_comments WHERE shareId = :shareId ORDER BY createdDate ASC")
    fun getCommentsForShare(shareId: Long): LiveData<List<SocialComment>>
    
    @Transaction
    @Query("SELECT COUNT(*) FROM social_comments WHERE shareId = :shareId")
    fun getCommentCount(shareId: Long): LiveData<Int>
    
    @Insert
    suspend fun insertComment(comment: SocialComment): Long
    
    @Update
    suspend fun updateComment(comment: SocialComment)
    
    @Delete
    suspend fun deleteComment(comment: SocialComment)
    
    @Transaction
    @Query("DELETE FROM social_comments WHERE id = :commentId AND username = :username")
    suspend fun deleteCommentByIdAndUser(commentId: Long, username: String)
    
    // Update counters (for efficient UI updates)
    @Transaction
    @Query("UPDATE social_shares SET likes = (SELECT COUNT(*) FROM social_likes WHERE shareId = :shareId) WHERE id = :shareId")
    suspend fun updateLikeCounter(shareId: Long)
    
    @Transaction
    @Query("UPDATE social_shares SET comments = (SELECT COUNT(*) FROM social_comments WHERE shareId = :shareId) WHERE id = :shareId")
    suspend fun updateCommentCounter(shareId: Long)
}
