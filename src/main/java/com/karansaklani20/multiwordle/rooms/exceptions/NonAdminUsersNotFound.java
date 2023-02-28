package com.karansaklani20.multiwordle.rooms.exceptions;

import javax.persistence.EntityNotFoundException;

import com.karansaklani20.multiwordle.rooms.model.Room;

public class NonAdminUsersNotFound extends EntityNotFoundException {
    public NonAdminUsersNotFound(Room room) {
        super(String.format("Non admin users not found for room with id=%d", room.getId()));
    }
}
