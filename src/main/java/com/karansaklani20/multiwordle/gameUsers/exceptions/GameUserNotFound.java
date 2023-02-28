package com.karansaklani20.multiwordle.gameUsers.exceptions;

import javax.persistence.EntityNotFoundException;

public class GameUserNotFound extends EntityNotFoundException {
    public GameUserNotFound(Long gameId, Long userId) {
        super(String.format("No user with id=%d found in game with id=%d", gameId, userId));
    }
}
