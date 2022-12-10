package com.karansaklani20.multiwordle.games.exceptions;

import javax.persistence.EntityExistsException;

public class GameAlreadyExists extends EntityExistsException {
    public GameAlreadyExists(Long roomId) {
        super(String.format("Game already exists for room with id=%d", roomId));
    }
}
