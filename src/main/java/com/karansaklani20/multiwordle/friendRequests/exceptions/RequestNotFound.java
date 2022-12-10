package com.karansaklani20.multiwordle.friendRequests.exceptions;

import javax.persistence.EntityNotFoundException;

public class RequestNotFound extends EntityNotFoundException {
    public RequestNotFound(Long id) {
        super(String.format("Friend request not found with id=%d", id));
    }
}
