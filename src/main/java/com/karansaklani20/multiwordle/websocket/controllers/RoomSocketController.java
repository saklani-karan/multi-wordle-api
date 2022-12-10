package com.karansaklani20.multiwordle.websocket.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.karansaklani20.multiwordle.rooms.dto.RoomEntityResponse;
import com.karansaklani20.multiwordle.websocket.dto.RoomGameCreatIonMessage;
import com.karansaklani20.multiwordle.websocket.dto.RoomJoin;
import com.karansaklani20.multiwordle.websocket.dto.RoomJoinMessage;
import com.karansaklani20.multiwordle.websocket.dto.RoomUserStatusUpdate;
import com.karansaklani20.multiwordle.websocket.service.RoomSocketService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@AllArgsConstructor
public class RoomSocketController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final RoomSocketService roomSocketService;

    @MessageMapping("/room/join")
    public void receiveJoinMessage(@Payload RoomJoin message) throws Exception {
        log.info("receiveMessage: message received", message.toString());

        RoomEntityResponse room = this.roomSocketService.checkUserForRoom(message.getRoomId(), message.getUserId());
        log.info("receiveMessage: user found in room with id={}", room.getId());

        simpMessagingTemplate.convertAndSend(String.format("/room/%d/user_join", room.getId()),
                RoomJoinMessage.builder().room(room).userId(message.getUserId()).build());
    }

    @MessageMapping("room/statusUpdate")
    public void receiveStatusUpdateMessage(@Payload RoomUserStatusUpdate message) throws Exception {
        log.info("receiveStatusUpdateMessage: message received", message.toString());

        RoomEntityResponse room = this.roomSocketService.updateRoomUserStatus(message.getRoomId(), message.getUserId(),
                message.getStatus());
        log.info("receiveStatusUpdateMessage: user found in room with id={}", room.getId());

        simpMessagingTemplate.convertAndSend(String.format("/room/%d/user_status_update", room.getId()),
                RoomJoinMessage.builder().room(room).userId(message.getUserId()).build());
    }

    @MessageMapping("room/gameCreated")
    public void receiveGameCreatedMessage(@Payload RoomGameCreatIonMessage message) throws Exception {
        log.info("receiveGameCreationMessage: received gameId={} and roomId={}", message.getGameId(),
                message.getRoomId());

        simpMessagingTemplate.convertAndSend(String.format("/room/%d/game_created", message.getRoomId()),
                message);
    }
}
