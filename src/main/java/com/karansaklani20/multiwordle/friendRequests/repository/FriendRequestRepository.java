package com.karansaklani20.multiwordle.friendRequests.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.karansaklani20.multiwordle.friendRequests.models.FriendRequest;
import com.karansaklani20.multiwordle.users.models.User;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findBySenderUser(User user);

    List<FriendRequest> findByRecipientUser(User user);

    FriendRequest findByRecipientUserAndSenderUser(User recipientUser, User senderUser);
}
