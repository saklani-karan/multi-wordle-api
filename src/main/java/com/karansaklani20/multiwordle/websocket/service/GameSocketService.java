package com.karansaklani20.multiwordle.websocket.service;

import org.springframework.stereotype.Service;

import com.karansaklani20.multiwordle.games.dto.GameWinnerAndCorrectWordResponse;
import com.karansaklani20.multiwordle.games.dto.RoundWinnerAndCorrectWordResponse;
import com.karansaklani20.multiwordle.games.exceptions.GameNotCompletedException;
import com.karansaklani20.multiwordle.games.models.Game;
import com.karansaklani20.multiwordle.games.services.GameService;
import com.karansaklani20.multiwordle.websocket.dto.GameCompletedMessage;
import com.karansaklani20.multiwordle.websocket.dto.GameCompletedMessageResponse;
import com.karansaklani20.multiwordle.websocket.dto.GameWinnerMessage;
import com.karansaklani20.multiwordle.websocket.dto.GameWinnerMessageResponse;
import com.karansaklani20.multiwordle.websocket.dto.RoundCompletedMessage;
import com.karansaklani20.multiwordle.websocket.dto.RoundCompletedMessageResponse;
import com.karansaklani20.multiwordle.websocket.dto.RoundWinnerMessage;
import com.karansaklani20.multiwordle.websocket.dto.RoundWinnerMessageResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class GameSocketService {
    private final GameService gameService;

    public RoundWinnerMessageResponse handleRoundWinnerMessage(RoundWinnerMessage message) throws Exception {
        log.info("handleRoundWinnerMessage: message received for gameId={}", message.getGameId());

        RoundWinnerAndCorrectWordResponse response = this.gameService.getRoundWinnerAndCorrectWord(message.getGameId());
        log.info("handleRoundWinnerMessage: correctWord={}", response.getCorrectWord());

        return RoundWinnerMessageResponse.builder()
                .correctWord(response.getWinner() != null ? response.getCorrectWord() : null)
                .winner(response.getWinner())
                .build();
    }

    public GameWinnerMessageResponse handleGameWinnerMessage(GameWinnerMessage message) throws Exception {
        log.info("handleGameWinnerMessage: message received for gameId={}", message.getGameId());

        GameWinnerAndCorrectWordResponse response = this.gameService.getGameWinnerAndCorrectWord(message.getGameId());
        log.info("handleGameWinnerMessage: correctWord={}", response.getCorrectWord());

        return GameWinnerMessageResponse.builder()
                .correctWord(response.getWinner() != null ? response.getCorrectWord() : null)
                .winner(response.getWinner())
                .score(response.getScore())
                .build();
    }

    public GameCompletedMessageResponse handleGameCompletedMessage(GameCompletedMessage message) throws Exception {
        log.info("handleGameCompletedMessage: message received for gameId={}", message.getGameId());

        Game game = this.gameService.getGameById(message.getGameId());
        log.info("handleGameCompletedMessage: game found with id={}", game.getId());

        if (!game.getGameCompleted()) {
            log.error("handleGameCompletedMessage: game not completed with id={}", game.getId());
            throw new GameNotCompletedException(game);
        }

        return GameCompletedMessageResponse.builder().gameId(game.getId()).gameCompleted(game.getGameCompleted())
                .build();

    }

    public RoundCompletedMessageResponse handleRoundCompletedMessage(RoundCompletedMessage message) throws Exception {
        log.info("handleRoundCompletedMessage: message received for gameId={}", message.getGameId());

        Game game = this.gameService.getGameById(message.getGameId());
        log.info("handleGameCompletedMessage: game found with id={}", game.getId());

        return RoundCompletedMessageResponse.builder().gameId(game.getId()).roundCompleted(true).build();

    }
}
