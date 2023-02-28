package com.karansaklani20.multiwordle.websocket.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.karansaklani20.multiwordle.roomUsers.dto.RoomUserData;
import com.karansaklani20.multiwordle.roomUsers.models.RoomUserStatus;
import com.karansaklani20.multiwordle.rooms.dto.RoomEntityResponse;
import com.karansaklani20.multiwordle.rooms.services.RoomService;
import com.karansaklani20.multiwordle.users.models.User;
import com.karansaklani20.multiwordle.users.services.UserService;
import com.karansaklani20.multiwordle.websocket.exceptions.UserDoesNotExistInRoomException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class RoomSocketService {
    private final RoomService roomService;
    private final UserService userService;

    public RoomEntityResponse checkUserForRoom(Long roomId, Long userId) throws Exception {
        log.info("checkUserForRoom: request received for roomId={} and userId={}", roomId, userId);

        User user = userService.getUserForId(userId);
        log.info("checkUserForRoom: user found");

        RoomEntityResponse room = this.roomService.getRoomWithUsersById(roomId, userId);
        log.info("checkUserForRoom: room found");

        List<RoomUserData> userData = room.getUsers();
        for (int i = 0; i < userData.size(); i++) {
            if (user.getId() == userData.get(i).getUser().getId()) {
                return room;
            }
        }
        throw new UserDoesNotExistInRoomException(userId, roomId);
    }

    public RoomEntityResponse updateRoomUserStatus(Long roomId, Long userId, RoomUserStatus status) throws Exception {
        log.info("updateRoomUserStatus: request received for roomId={} and userId={}", roomId, userId);

        return this.roomService.updateRoomUserStatus(roomId, userId, status);
    }
}
