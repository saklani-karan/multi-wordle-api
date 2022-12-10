package com.karansaklani20.multiwordle.utils;

import java.util.Comparator;

import com.karansaklani20.multiwordle.games.dto.GameScore;

public class ScoreComparator implements Comparator<GameScore> {
    public int compare(GameScore object1, GameScore object2) {
        if (object1.getScore() == null) {
            return -1;
        }
        if (object2.getScore() == null) {
            return 1;
        }
        return (object1.getScore() < object2.getScore() ? -1 : 1);
    }
}
