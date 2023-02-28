package com.karansaklani20.multiwordle.rooms.dto;

import com.karansaklani20.multiwordle.roomUsers.models.RoomUserRole;
import com.karansaklani20.multiwordle.roomUsers.models.RoomUserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAuthenticatedRoomUserData {
    private Long id;
    private Long roomId;
    private Long userId;
    private Boolean isReady;
    private Boolean isAdmin;
}
