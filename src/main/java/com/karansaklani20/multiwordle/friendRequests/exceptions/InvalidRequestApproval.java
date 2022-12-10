package com.karansaklani20.multiwordle.friendRequests.exceptions;

import javax.management.relation.InvalidRoleValueException;

public class InvalidRequestApproval extends InvalidRoleValueException {
    public InvalidRequestApproval(Long requestId, String userEmail) {
        super(String.format("User cannot accept request with id=%s as user '%s' is not the recipient", requestId,
                userEmail));
    }
}
