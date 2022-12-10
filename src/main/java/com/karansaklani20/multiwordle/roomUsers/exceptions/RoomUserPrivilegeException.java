package com.karansaklani20.multiwordle.roomUsers.exceptions;

import java.rmi.AccessException;

import com.karansaklani20.multiwordle.roomUsers.models.RoomUser;
import com.karansaklani20.multiwordle.roomUsers.models.RoomUserRole;

public class RoomUserPrivilegeException extends AccessException {
    public RoomUserPrivilegeException(RoomUser roomUser, RoomUserRole userRole) {
        super(String.format(
                "RoomUser with id=%d with userRole=%s does not have the priveleges for this action (Required userRole=%s)",
                roomUser.getId(), roomUser.getUserRole(), userRole));
    }

    public RoomUserPrivilegeException(RoomUser roomUser) {
        super(String.format("RoomUser with id=%d with userRole=%s does not have the priveleges for this action",
                roomUser.getId(), roomUser.getUserRole()));
    }
}
