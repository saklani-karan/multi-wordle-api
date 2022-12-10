package com.karansaklani20.multiwordle.roomUsers.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.karansaklani20.multiwordle.roomUsers.dto.RoomUserData;
import com.karansaklani20.multiwordle.roomUsers.exceptions.RoomUserAlreadyExists;
import com.karansaklani20.multiwordle.roomUsers.exceptions.RoomUserNotFound;
import com.karansaklani20.multiwordle.roomUsers.models.RoomUser;
import com.karansaklani20.multiwordle.roomUsers.models.RoomUserRole;
import com.karansaklani20.multiwordle.roomUsers.models.RoomUserStatus;
import com.karansaklani20.multiwordle.roomUsers.repository.RoomUserRepository;
import com.karansaklani20.multiwordle.rooms.model.Room;
import com.karansaklani20.multiwordle.users.models.User;
import com.karansaklani20.multiwordle.users.services.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class RoomUserService {
    private final RoomUserRepository roomUserRepository;
    private final UserService userService;

    public RoomUser createRoomUser(Room room, User user, RoomUserRole userRole) throws Exception {
        log.info("createRoomUser: request received for roomId={} and userId={} with userRole={}", room.getId(),
                user.getId(), userRole);

        RoomUser prevRoomUsers = this.roomUserRepository.findByUserAndRoom(user, room);
        if (prevRoomUsers != null) {
            log.error("createRoomUser: user with id={} already exists in roomm with id={}", user.getId(), room.getId());
            throw new RoomUserAlreadyExists(room.getId(), user.getId());
        }
        log.info("createRoomUser: no user with id={} exists in roomm with id={}", user.getId(), room.getId());

        return this.roomUserRepository.save(RoomUser.builder()
                .room(room)
                .user(user)
                .userRole(userRole)
                .status(RoomUserStatus.NOT_READY)
                .build());
    }

    public RoomUser createRoomUser(Room room, User user) throws Exception {
        log.info("createRoomUser: request received for roomId={} and userId={}", room.getId(), user.getId());

        RoomUser prevRoomUsers = this.roomUserRepository.findByUserAndRoom(user, room);
        if (prevRoomUsers != null) {
            log.error("createRoomUser: user with id={} already exists in roomm with id={}", user.getId(), room.getId());
            throw new RoomUserAlreadyExists(room.getId(), user.getId());
        }
        log.info("createRoomUser: no user with id={} exists in roomm with id={}", user.getId(), room.getId());

        return this.roomUserRepository.save(RoomUser.builder()
                .room(room)
                .user(user)
                .userRole(RoomUserRole.USER)

                .build());
    }

    public List<User> getUsersForRoom(Long roomId) {
        log.info("getUsersForRoom: request received for roomId={}", roomId);
        return this.roomUserRepository.getRoomUserDataForRoom(roomId);
    }

    public List<RoomUserData> getUserDataForRoom(Room room) throws Exception {
        log.info("getUserDataForRoom: fetching user data with roomId={}", room.getId());

        List<RoomUser> roomUsers = this.roomUserRepository.findByRoom(room);

        return roomUsers.stream().map(this::convertToRoomUserData).toList();
    }

    private RoomUserData convertToRoomUserData(RoomUser roomUser) {
        return RoomUserData.builder().user(roomUser.getUser()).status(roomUser.getStatus())
                .userRole(roomUser.getUserRole()).build();
    }

    public RoomUser updateRoomUserStatus(User user, Room room, RoomUserStatus status) throws Exception {
        log.info("updateRoomUserStatus: updating status to {} for user with id = {} and roomm = {}", user.getId(),
                room.getId());

        RoomUser roomUser = this.roomUserRepository.findByUserAndRoom(user, room);

        if (roomUser == null) {
            log.error("updateRoomUserStatus: roomUser not found for user with id = {} and roomm = {}", user.getId(),
                    room.getId());
            throw new RoomUserNotFound(room.getId(), user.getId());
        }
        log.error("updateRoomUserStatus: roomUser with id={} found for user with id = {} and roomm = {}",
                roomUser.getId(), user.getId(),
                room.getId());

        roomUser.setStatus(status);
        return this.roomUserRepository.save(roomUser);
    }

    public Long getUserStatusCount(Long roomId, RoomUserStatus status) throws Exception {
        log.info("getUserStatusCount: roomId={} and status={}", roomId, status);

        return this.roomUserRepository.getUserStatusCount(roomId, status);
    }

    public Long getUserCount(Long roomId) throws Exception {
        log.info("getUserCount: roomId={}", roomId);

        return this.roomUserRepository.getUserCount(roomId);
    }

    public RoomUser getAuthenticatedRoomUser(Room room) throws Exception {
        log.info("getAuthenticatedRoomUser: fetching authenticated game user for room={}", room.getId());

        User user = this.userService.getUserFromAuthContext();
        log.info("getAuthenticatedRoomUser: authenticated user with id={}", user.getId());

        RoomUser roomUser = this.roomUserRepository.findByUserAndRoom(user, room);

        if (roomUser == null) {
            throw new RoomUserNotFound(room.getId(), user.getId());
        }
        return roomUser;
    }
}
