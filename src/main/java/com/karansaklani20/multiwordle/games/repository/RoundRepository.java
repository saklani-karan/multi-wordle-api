package com.karansaklani20.multiwordle.games.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.karansaklani20.multiwordle.games.models.Game;
import com.karansaklani20.multiwordle.games.models.Round;

public interface RoundRepository extends JpaRepository<Round, Long> {
    List<Round> findByGame(Game game);

    List<Round> findByGameAndCompleted(Game game, Boolean completed);

    List<Round> findByGameAndCurrentRound(Game game, Boolean currentRound);
}
