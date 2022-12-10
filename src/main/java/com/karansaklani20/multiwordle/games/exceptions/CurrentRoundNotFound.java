package com.karansaklani20.multiwordle.games.exceptions;

import javax.persistence.EntityNotFoundException;

public class CurrentRoundNotFound extends EntityNotFoundException {
    public CurrentRoundNotFound(Long gameId) {
        super(String.format("Current round not found for game with gameId=%d", gameId));
    }
}
