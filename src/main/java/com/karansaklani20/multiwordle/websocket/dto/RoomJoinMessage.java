package com.karansaklani20.multiwordle.websocket.dto;

import com.karansaklani20.multiwordle.rooms.dto.RoomEntityResponse;
import com.karansaklani20.multiwordle.websocket.models.RoomJoinStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomJoinMessage {
    private RoomEntityResponse room;
    private Long userId;
    @Builder.Default
    private RoomJoinStatus status = RoomJoinStatus.JOIN;
}
