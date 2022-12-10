package com.karansaklani20.multiwordle.websocket.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.karansaklani20.multiwordle.websocket.dto.GameCompletedMessage;
import com.karansaklani20.multiwordle.websocket.dto.GameCompletedMessageResponse;
import com.karansaklani20.multiwordle.websocket.dto.GameWinnerMessage;
import com.karansaklani20.multiwordle.websocket.dto.GameWinnerMessageResponse;
import com.karansaklani20.multiwordle.websocket.dto.RoundCompletedMessage;
import com.karansaklani20.multiwordle.websocket.dto.RoundCompletedMessageResponse;
import com.karansaklani20.multiwordle.websocket.dto.RoundCreationMessage;
import com.karansaklani20.multiwordle.websocket.dto.RoundCreationMessageResponse;
import com.karansaklani20.multiwordle.websocket.dto.RoundWinnerMessage;
import com.karansaklani20.multiwordle.websocket.dto.RoundWinnerMessageResponse;
import com.karansaklani20.multiwordle.websocket.service.GameSocketService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@AllArgsConstructor
public class GameSocketController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final GameSocketService gameSocketService;

    @MessageMapping("/game/roundWinner")
    public void receiveJoinMessage(@Payload RoundWinnerMessage message) throws Exception {
        log.info("receiveMessage: message received", message.toString());
        RoundWinnerMessageResponse response = this.gameSocketService.handleRoundWinnerMessage(message);
        simpMessagingTemplate.convertAndSend(String.format("/game/%d/round_winner", message.getGameId()),
                response);
    }

    @MessageMapping("/game/nextRound")
    public void receiveNewRoundMessage(@Payload RoundCreationMessage message) throws Exception {
        log.info("receiveNewRoundMessage: message received", message.toString());
        simpMessagingTemplate.convertAndSend(String.format("/game/%d/new_round", message.getGameId()),
                RoundCreationMessageResponse.builder().gameId(message.getGameId()).build());
    }

    @MessageMapping("/game/gameWinner")
    public void receiveGameWinnerMessage(@Payload GameWinnerMessage message) throws Exception {
        log.info("receiveNewRoundMessage: message received", message.toString());
        GameWinnerMessageResponse response = this.gameSocketService.handleGameWinnerMessage(message);
        simpMessagingTemplate.convertAndSend(String.format("/game/%d/game_winner", message.getGameId()),
                response);
    }

    @MessageMapping("/game/gameCompleted")
    public void receiveGameCompletedMessage(@Payload GameCompletedMessage message) throws Exception {
        log.info("receiveGameCompletedMessage: message received");
        GameCompletedMessageResponse response = this.gameSocketService.handleGameCompletedMessage(message);
        simpMessagingTemplate.convertAndSend(String.format("/game/%d/game_completed", message.getGameId()),
                response);
    }

    @MessageMapping("game/roundCompleted")
    public void receiveRoundCompletedMessaage(@Payload RoundCompletedMessage message) throws Exception {
        log.info("receiveRoundCompletedMessage: message received");
        RoundCompletedMessageResponse response = this.gameSocketService.handleRoundCompletedMessage(message);
        simpMessagingTemplate.convertAndSend(String.format("/game/%d/round_completed", message.getGameId()),
                response);
    }
}
