package com.huntersascension.data.dao.social

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huntersascension.data.model.User
import com.huntersascension.data.model.social.FriendRelation
import com.huntersascension.data.model.social.FriendRequestStatus

/**
 * Data Access Object for Friend relations
 */
@Dao
interface FriendDao {
    @Transaction
    @Query("SELECT * FROM friend_relations WHERE userUsername = :username OR friendUsername = :username")
    fun getAllFriendRelations(username: String): LiveData<List<FriendRelation>>
    
    @Transaction
    @Query("SELECT users.* FROM users " +
           "JOIN friend_relations ON users.username = friend_relations.friendUsername " +
           "WHERE friend_relations.userUsername = :username AND friend_relations.status = :status")
    fun getFriends(username: String, status: FriendRequestStatus = FriendRequestStatus.ACCEPTED): LiveData<List<User>>
    
    @Transaction
    @Query("SELECT users.* FROM users " +
           "JOIN friend_relations ON users.username = friend_relations.userUsername " +
           "WHERE friend_relations.friendUsername = :username AND friend_relations.status = :status")
    fun getFriendsReverse(username: String, status: FriendRequestStatus = FriendRequestStatus.ACCEPTED): LiveData<List<User>>
    
    @Transaction
    @Query("SELECT * FROM friend_relations " +
           "WHERE friendUsername = :username AND status = :status")
    fun getFriendRequests(username: String, status: FriendRequestStatus = FriendRequestStatus.PENDING): LiveData<List<FriendRelation>>
    
    @Transaction
    @Query("SELECT * FROM friend_relations " +
           "WHERE (userUsername = :user1 AND friendUsername = :user2) OR " +
           "(userUsername = :user2 AND friendUsername = :user1)")
    fun getFriendRelation(user1: String, user2: String): LiveData<FriendRelation?>
    
    @Transaction
    @Query("SELECT COUNT(*) FROM friend_relations " +
           "WHERE ((userUsername = :user1 AND friendUsername = :user2) OR " +
           "(userUsername = :user2 AND friendUsername = :user1)) AND " +
           "status = :status")
    suspend fun areFriends(user1: String, user2: String, status: FriendRequestStatus = FriendRequestStatus.ACCEPTED): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendRelation(relation: FriendRelation)
    
    @Update
    suspend fun updateFriendRelation(relation: FriendRelation)
    
    @Delete
    suspend fun deleteFriendRelation(relation: FriendRelation)
    
    @Transaction
    @Query("DELETE FROM friend_relations " +
           "WHERE (userUsername = :user1 AND friendUsername = :user2) OR " +
           "(userUsername = :user2 AND friendUsername = :user1)")
    suspend fun deleteFriendship(user1: String, user2: String)
    
    @Transaction
    @Query("UPDATE friend_relations SET status = :newStatus " +
           "WHERE userUsername = :requester AND friendUsername = :receiver")
    suspend fun updateFriendRequestStatus(
        requester: String, 
        receiver: String, 
        newStatus: FriendRequestStatus
    )
}
