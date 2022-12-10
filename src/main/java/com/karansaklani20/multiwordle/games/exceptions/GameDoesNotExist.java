package com.karansaklani20.multiwordle.games.exceptions;

import javax.persistence.EntityNotFoundException;

public class GameDoesNotExist extends EntityNotFoundException {
    public GameDoesNotExist(Long gameId) {
        super(String.format("Game with id=%d does not exist", gameId));
    }
}
