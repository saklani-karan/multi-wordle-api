package com.karansaklani20.multiwordle.friendRequests.exceptions;

import javax.persistence.EntityExistsException;

public class RequestAlreadySent extends EntityExistsException {
    public RequestAlreadySent(Long senderId, Long recipientId) {
        super(String.format("Request already sent fromm user with id=%d to user with id=%d", senderId, recipientId));
    }
}
