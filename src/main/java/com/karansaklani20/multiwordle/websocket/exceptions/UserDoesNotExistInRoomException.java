package com.karansaklani20.multiwordle.websocket.exceptions;

import javax.persistence.EntityNotFoundException;

public class UserDoesNotExistInRoomException extends EntityNotFoundException {
    public UserDoesNotExistInRoomException(Long userId, Long roomId) {
        super(String.format("User with id=%d does not exist in room with id=%d", userId, roomId));
    }
}
