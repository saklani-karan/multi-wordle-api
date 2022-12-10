package com.karansaklani20.multiwordle.roomUsers.exceptions;

import javax.persistence.EntityNotFoundException;

public class RoomUserNotFound extends EntityNotFoundException {
    public RoomUserNotFound(Long roomId, Long userId) {
        super(String.format("User with id=%d does not exists in room with id=%d", userId, roomId));
    }
}
