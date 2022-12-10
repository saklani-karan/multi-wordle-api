package com.karansaklani20.multiwordle.friends.exceptions;

import javax.persistence.EntityNotFoundException;

public class FriendDoesNotExist extends EntityNotFoundException {
    public FriendDoesNotExist(Long userId, Long friendId) {
        super(String.format("Users with id '%s' and id '%s' are not friends", userId, friendId));
    }
}
