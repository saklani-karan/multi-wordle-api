package com.karansaklani20.multiwordle.roomUsers.dto;

import com.karansaklani20.multiwordle.roomUsers.models.RoomUserRole;
import com.karansaklani20.multiwordle.roomUsers.models.RoomUserStatus;
import com.karansaklani20.multiwordle.users.models.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomUserData {
    private User user;
    private RoomUserRole userRole;
    private RoomUserStatus status;
}
