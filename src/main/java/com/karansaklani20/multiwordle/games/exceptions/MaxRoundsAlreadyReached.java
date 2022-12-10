package com.karansaklani20.multiwordle.games.exceptions;

import javax.naming.LimitExceededException;

public class MaxRoundsAlreadyReached extends LimitExceededException {
    public MaxRoundsAlreadyReached(Long gameId, Integer maxRounds) {
        super(String.format("Game with gameId=%d has already reached maximum round limit of %d", gameId, maxRounds));
    }
}
