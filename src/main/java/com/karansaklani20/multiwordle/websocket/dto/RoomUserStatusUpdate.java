package com.karansaklani20.multiwordle.websocket.dto;

import com.karansaklani20.multiwordle.roomUsers.models.RoomUserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomUserStatusUpdate {
    private Long roomId;
    private Long userId;
    private RoomUserStatus status;
}
