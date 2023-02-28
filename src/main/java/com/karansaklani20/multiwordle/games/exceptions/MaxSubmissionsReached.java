package com.karansaklani20.multiwordle.games.exceptions;

import javax.naming.LimitExceededException;

public class MaxSubmissionsReached extends LimitExceededException {
    public MaxSubmissionsReached(Integer maxSubmissions, Long gameId) {
        super(String.format("Total submissions of %d of game with id=%d already reached", maxSubmissions, gameId));
    }
}
