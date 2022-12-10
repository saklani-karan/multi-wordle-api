package com.karansaklani20.multiwordle.friendRequests.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.karansaklani20.multiwordle.friendRequests.dto.NewFriendRequestResponse;
import com.karansaklani20.multiwordle.friendRequests.exceptions.InvalidRequestApproval;
import com.karansaklani20.multiwordle.friendRequests.exceptions.RequestAlreadySent;
import com.karansaklani20.multiwordle.friendRequests.exceptions.RequestNotFound;
import com.karansaklani20.multiwordle.friendRequests.models.FriendRequest;
import com.karansaklani20.multiwordle.friendRequests.repository.FriendRequestRepository;
import com.karansaklani20.multiwordle.friends.exceptions.FriendAlreadyExists;
import com.karansaklani20.multiwordle.friends.exceptions.FriendDoesNotExist;
import com.karansaklani20.multiwordle.friends.models.Friend;
import com.karansaklani20.multiwordle.friends.services.FriendsService;
import com.karansaklani20.multiwordle.users.models.User;
import com.karansaklani20.multiwordle.users.services.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class FriendRequestService {
    private final FriendRequestRepository requestRepository;
    private final UserService userService;
    private final FriendsService friendsService;

    public List<FriendRequest> getSentRequestsForUser() throws Exception {
        log.info("getRequestForUser: request received");

        User user = this.userService.getUserFromAuthContext();
        log.info("getRequestForUser: user found with id={}", user.getId());

        return this.requestRepository.findBySenderUser(user);
    }

    public List<FriendRequest> getReceivedRequestsForUser() throws Exception {
        log.info("getRequestForUser: request received");

        User user = this.userService.getUserFromAuthContext();
        log.info("getRequestForUser: user found with id={}", user.getId());

        return this.requestRepository.findByRecipientUser(user);
    }

    public NewFriendRequestResponse sendFriendRequest(Long userId) throws Exception {
        log.info("sendFriendRequest: request received");

        User senderUser = this.userService.getUserFromAuthContext();
        log.info("sendFriendRequest: senderUser found with id={}", senderUser.getId());

        User recipientUser = this.userService.getUserForId(userId);
        log.info("sendFriendRequest: recipientUser found with id={}", recipientUser.getId());

        FriendRequest prevRequests = this.requestRepository.findByRecipientUserAndSenderUser(recipientUser, senderUser);
        if (prevRequests != null) {
            log.error("sendFriendRequest: request already sent for senderId={} and recipientId={}", senderUser.getId(),
                    recipientUser.getId());
            throw new RequestAlreadySent(senderUser.getId(), recipientUser.getId());
        }

        Friend prevFriendship = null;
        try {
            prevFriendship = this.friendsService.findFriendByUserIds(senderUser.getId(), recipientUser.getId());
        } catch (FriendDoesNotExist exception) {
            log.info("sendFriendRequest: no previous friends found");
        } catch (Exception exception) {
            log.error("sendFriendRequest: findFriendByUserIds threw error with message: {}",
                    exception.getMessage());
            throw exception;
        }

        if (prevFriendship != null) {
            log.error("sendFriendRequest: friend already exists between userId={} and friendId={}",
                    senderUser.getId(), recipientUser.getId());
            throw new FriendAlreadyExists(senderUser.getId(), recipientUser.getId());
        }

        FriendRequest request = this.requestRepository
                .save(FriendRequest
                        .builder()
                        .senderUser(senderUser)
                        .recipientUser(recipientUser)
                        .build());
        log.info("sendFriendRequest: new request created with id={}", request.getId());

        return NewFriendRequestResponse
                .builder()
                .id(request.getId())
                .senderEmail(senderUser.getEmail())
                .recipientEmail(recipientUser.getEmail())
                .build();
    }

    public Friend acceptFriendRequest(Long id) throws Exception {
        log.info("acceptFriendRequest: request received");

        Optional<FriendRequest> request = this.requestRepository.findById(id);

        if (request.isEmpty()) {
            log.error("acceptFriendRequest: request not found for id={}", id);
            throw new RequestNotFound(id);
        }
        log.info("acceptFriendRequest: request found with id={} for recipient user={}", id,
                request.get().getRecipientUser().getId());

        User authUser = this.userService.getUserFromAuthContext();

        if (request.get().getRecipientUser().getId() != authUser.getId()) {
            log.error("acceptFriendRequest: authenticated user={} and recipient={} are not the same", authUser.getId(),
                    request.get().getRecipientUser().getId());
            throw new InvalidRequestApproval(id, authUser.getEmail());
        }
        log.info("acceptFriendRequest: authenticated user={} == recipient={}", authUser.getId(),
                request.get().getRecipientUser().getId());

        Friend friend = this.friendsService.addFriendToUser(authUser.getId(),
                request.get().getSenderUser().getId());
        log.info("acceptFriendRequest: friendship created userId={} and friendId+{}", friend.getPrimaryUser().getId(),
                friend.getSecondaryUser().getId());

        this.requestRepository.deleteById(id);
        return friend;
    }

}
