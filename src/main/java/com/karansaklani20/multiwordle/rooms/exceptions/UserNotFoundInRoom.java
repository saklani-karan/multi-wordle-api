package com.karansaklani20.multiwordle.rooms.exceptions;

import javax.persistence.EntityNotFoundException;

public class UserNotFoundInRoom extends EntityNotFoundException {
    public UserNotFoundInRoom(Long roomId, Long userId) {
        super(String.format("User with id={} not found in room with id+{}", userId, roomId));
    }
}
