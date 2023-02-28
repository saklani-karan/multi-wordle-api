package com.karansaklani20.multiwordle.gameUsers.exceptions;

import javax.persistence.EntityExistsException;

import com.karansaklani20.multiwordle.games.models.Game;

public class RoundsNotCompleted extends EntityExistsException {
    public RoundsNotCompleted(Game game) {
        super(String.format("Game with id=%d still has incomplete rounds", game.getId()));
    }
}
