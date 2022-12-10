package com.karansaklani20.multiwordle.games.exceptions;

import javax.persistence.EntityExistsException;

import com.karansaklani20.multiwordle.gameUsers.models.GameUser;
import com.karansaklani20.multiwordle.games.models.Game;

public class RoundAlreadyCompletedException extends EntityExistsException {
    public RoundAlreadyCompletedException(Game game) {
        super(String.format("Current round already completed for game = %d", game.getId()));
    }

    public RoundAlreadyCompletedException(Game game, GameUser gameUser) {
        super(String.format("Current round already completed for game = %d for gameUser = %d ", game.getId(),
                gameUser.getId()));
    }
}
