package com.karansaklani20.multiwordle.rooms.exceptions;

import javax.xml.bind.ValidationException;

public class UsersNotReadyForGame extends ValidationException {
    public UsersNotReadyForGame(Long roomId) {
        super(String.format("All users in room with id={} are not ready for the game", roomId));
    }
}
