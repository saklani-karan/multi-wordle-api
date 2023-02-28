package com.karansaklani20.multiwordle.friends.services;

import org.springframework.stereotype.Service;

import com.karansaklani20.multiwordle.friends.exceptions.FriendAlreadyExists;
import com.karansaklani20.multiwordle.friends.exceptions.FriendDoesNotExist;
import com.karansaklani20.multiwordle.friends.models.Friend;
import com.karansaklani20.multiwordle.friends.repository.FriendsRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class FriendsService {
    private final FriendsRepository friendsRepository;

    public Friend findFriendByUserIds(Long userId, Long friendId) {
        log.info("findFriendByUserIds: fetching friend by userId={} and friendId={}", userId, friendId);

        Friend friend = this.friendsRepository.findFriendByPrimaryAndSecondaryUserId(userId, friendId);

        if (friend == null) {
            log.error("findFriendByUserIds: no friend found for userId={} and friendId={}", userId, friendId);
            throw new FriendDoesNotExist(userId, friendId);
        }

        log.info("findFriendByUserIds: friend found with id={} for userId={} and friendId={}", friend.getId(), userId,
                friendId);
        return friend;
    }

    public Friend addFriendToUser(Long userId, Long friendId) throws Exception {
        log.info("addFriendToUser: creating friend by userId={} and friendId={}", userId, friendId);

        Friend prevFriend = null;
        try {
            prevFriend = this.findFriendByUserIds(userId, friendId);
        } catch (FriendDoesNotExist exception) {
            log.info("addFriendToUser: no previous friend exists for userId={} and friendId={}", userId, friendId);
        } catch (Exception exception) {
            log.error("addFriendToUser: findFriendByUserIds threw an unhandled exception: {}", exception.getMessage());
            throw exception;
        }

        if (prevFriend != null) {
            log.info("addFriendToUser previous friend exists for userId={} and friendId={} with id={}", userId,
                    friendId, prevFriend.getId());
            throw new FriendAlreadyExists(userId, friendId);
        }

        this.friendsRepository.saveFriendByUserId(userId, friendId);

        Friend newFriend = null;
        try {
            newFriend = this.findFriendByUserIds(userId, friendId);
        } catch (FriendDoesNotExist exception) {
            log.error("addFriendToUser: newly created friend not found for userId={} and friendId={}", userId,
                    friendId);
            throw new Exception(String.format("An unknown error occured while fetching created friend: %s",
                    exception.getMessage()));

        } catch (Exception exception) {
            log.error("addFriendToUser: findFriendByUserIds threw an unhandled exception: {}", exception.getMessage());
            throw exception;
        }

        return newFriend;
    }
}
