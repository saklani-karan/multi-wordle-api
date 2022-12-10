package com.karansaklani20.multiwordle.roomUsers.exceptions;

import javax.persistence.EntityExistsException;

public class RoomUserAlreadyExists extends EntityExistsException {
    public RoomUserAlreadyExists(Long roomId, Long userId) {
        super(String.format("User with id=%d already exists in room with id=%d", userId, roomId));
    }
}
