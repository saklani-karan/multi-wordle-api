package com.karansaklani20.multiwordle.rooms.exceptions;

import javax.persistence.EntityNotFoundException;

public class RoomNotFoundException extends EntityNotFoundException {
    public RoomNotFoundException(Long id) {
        super(String.format("Room not found with id = %d", id));
    }
}
