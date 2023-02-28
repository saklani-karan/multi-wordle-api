package com.karansaklani20.multiwordle.rooms.exceptions;

import javax.persistence.EntityExistsException;

public class UserAlreadyAdmittedException extends EntityExistsException {
    public UserAlreadyAdmittedException(Long roomId, Long userId) {
        super(String.format("User with id=%d already exists in the room with id=%d", roomId, userId));
    }
}
