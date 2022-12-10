package com.karansaklani20.multiwordle.websocket.dto;

import com.karansaklani20.multiwordle.websocket.models.RoomJoinStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomJoin {
    private Long userId;
    private Long roomId;
    private RoomJoinStatus status;
}
