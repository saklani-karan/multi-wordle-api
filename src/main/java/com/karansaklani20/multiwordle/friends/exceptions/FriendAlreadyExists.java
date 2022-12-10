package com.karansaklani20.multiwordle.friends.exceptions;

import javax.persistence.EntityExistsException;

public class FriendAlreadyExists extends EntityExistsException {
    public FriendAlreadyExists(Long userId, Long friendId) {
        super(String.format("Users with id '%s' and id '%s' are already friends", userId, friendId));
    }
}
