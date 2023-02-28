package com.karansaklani20.multiwordle.games.exceptions;

import javax.persistence.EntityExistsException;

import com.karansaklani20.multiwordle.games.models.Game;

public class GameNotCompletedException extends EntityExistsException {
    public GameNotCompletedException(Game game) {
        super(String.format("The action is  not supported for game with id=%d as it is not completed", game.getId()));
    }
}
