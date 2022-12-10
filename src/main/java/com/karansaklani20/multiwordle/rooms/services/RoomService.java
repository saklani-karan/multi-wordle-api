package com.karansaklani20.multiwordle.rooms.services;

import java.util.List;

import org.springframework.core.style.ValueStyler;
import org.springframework.stereotype.Service;

import com.karansaklani20.multiwordle.roomUsers.dto.RoomUserData;
import com.karansaklani20.multiwordle.roomUsers.exceptions.RoomUserPrivilegeException;
import com.karansaklani20.multiwordle.roomUsers.models.RoomUser;
import com.karansaklani20.multiwordle.roomUsers.models.RoomUserRole;
import com.karansaklani20.multiwordle.roomUsers.models.RoomUserStatus;
import com.karansaklani20.multiwordle.roomUsers.services.RoomUserService;
import com.karansaklani20.multiwordle.rooms.dto.AdmitOrCreateUserResponse;
import com.karansaklani20.multiwordle.rooms.dto.GetAuthenticatedRoomUserData;
import com.karansaklani20.multiwordle.rooms.dto.RoomEntityResponse;
import com.karansaklani20.multiwordle.rooms.dto.GameCreationValidationResponse;
import com.karansaklani20.multiwordle.rooms.exceptions.NonAdminUsersNotFound;
import com.karansaklani20.multiwordle.rooms.exceptions.RoomNotFoundException;
import com.karansaklani20.multiwordle.rooms.exceptions.UserNotFoundInRoom;
import com.karansaklani20.multiwordle.rooms.exceptions.UsersNotReadyForGame;
import com.karansaklani20.multiwordle.rooms.model.Room;
import com.karansaklani20.multiwordle.rooms.repository.RoomRepository;
import com.karansaklani20.multiwordle.users.models.User;
import com.karansaklani20.multiwordle.users.services.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserService userService;
    private final RoomUserService roomUserService;

    public RoomEntityResponse getRoomWithUsersById(Long id) throws Exception {
        log.info("getRoomWithUsersById: request received");

        Room room = this.roomRepository.getReferenceById(id);
        if (room == null) {
            log.error("getRoomWithUsersById: no room found for id={}", id);
            throw new RoomNotFoundException(id);
        }
        log.info("getRoomWithUsersById: room found for id={}", room.getId());

        List<RoomUserData> userData = this.roomUserService.getUserDataForRoom(room);
        log.info("getRoomWithUsersById: number of users found={}", userData.size());

        RoomUser roomUser = this.roomUserService.getAuthenticatedRoomUser(room);
        return RoomEntityResponse.builder().id(room.getId()).users(userData)
                .isAdmin(roomUser.getUserRole() == RoomUserRole.ADMIN).build();

    }

    public RoomEntityResponse getRoomWithUsersById(Long id, Long userId) throws Exception {
        log.info("getRoomWithUsersById: request received");

        Room room = this.roomRepository.getReferenceById(id);
        if (room == null) {
            log.error("getRoomWithUsersById: no room found for id={}", id);
            throw new RoomNotFoundException(id);
        }
        log.info("getRoomWithUsersById: room found for id={}", room.getId());

        List<RoomUserData> userData = this.roomUserService.getUserDataForRoom(room);
        log.info("getRoomWithUsersById: number of users found={}", userData.size());
        User user = this.userService.getUserForId(userId);
        log.info("getRoomWithUsersById: user found with id={}", user.getId());

        for (int i = 0; i < userData.size(); i++) {
            RoomUserData roomUserData = userData.get(i);
            if (roomUserData.getUser().getId() == user.getId()) {
                log.info("getRoomWithUsersById: user  with id={} found in room with id={}", user.getId(), id);
                Boolean isAdmin = roomUserData.getUserRole() == RoomUserRole.ADMIN;
                return RoomEntityResponse.builder().id(room.getId()).users(userData).isAdmin(isAdmin).build();
            }
        }
        log.error("getRoomWithUsersById: user with id={} not found in room with id={}", user.getId(), id);
        throw new UserNotFoundInRoom(id, user.getId());

    }

    public AdmitOrCreateUserResponse createRoomForUser() throws Exception {
        log.info("createRoomForUser: request received");

        User user = this.userService.getUserFromAuthContext();
        log.info("createRoomForUser: user found with id={}", user.getId());

        Room room = this.roomRepository.save(Room.builder().build());
        log.info("createRoomForUser: room created with roomId={}", room.getId());

        RoomUser roomUser = this.roomUserService.createRoomUser(room, user, RoomUserRole.ADMIN);
        log.info("createRoomForUser: room user created with id={}", roomUser.getId());

        return AdmitOrCreateUserResponse.builder().roomId(room.getId()).userId(user.getId())
                .roomUserId(roomUser.getId()).isAdmin(true).build();
    }

    public AdmitOrCreateUserResponse addUserToRoom(Long id) throws Exception {
        log.info("addUserToRoom: request received");

        Room room = this.roomRepository.getReferenceById(id);
        if (room == null) {
            throw new RoomNotFoundException(id);
        }

        User user = this.userService.getUserFromAuthContext();
        log.info("addUserToRoom: user found with id={}", user.getId());

        RoomUser roomUser = this.roomUserService.createRoomUser(room, user, RoomUserRole.USER);
        log.info("createRoomForUser: room user created with id={}", roomUser.getId());

        return AdmitOrCreateUserResponse.builder().roomId(room.getId()).userId(user.getId())
                .roomUserId(roomUser.getId()).isAdmin(false).build();
    }

    public RoomEntityResponse updateRoomUserStatus(Long roomId, Long userId, RoomUserStatus status) throws Exception {
        log.info("updateRoomUserStatus: updating user status for roomId={}, userId={} with status={}", roomId, userId,
                status);

        Room room = this.roomRepository.getReferenceById(roomId);
        if (room == null) {
            log.error("updateRoomUserStatus: no room found with id={}", roomId);
            throw new RoomNotFoundException(roomId);
        }
        log.info("updateRoomUserStatus: room found with id={}", roomId);

        User user = this.userService.getUserForId(userId);
        log.info("updateRoomUserStatus: user found with id={}", userId);

        RoomUser roomUser = this.roomUserService.updateRoomUserStatus(user, room, status);
        log.info("updateRoomUserStatus: roomUser updated with id={}", roomUser.getId());

        return this.getRoomWithUsersById(roomId, userId);
    }

    public Room validateRoomForGameCreation(Long roomId) throws Exception {
        log.info("validateRoomForGameCreation: roomId={}", roomId);

        Room room = this.roomRepository.getReferenceById(roomId);

        if (room == null) {
            log.error("validateRoomForGameCreation: no room found with roomId={}");
            throw new RoomNotFoundException(roomId);
        }

        RoomUser roomUser = this.roomUserService.getAuthenticatedRoomUser(room);
        log.info("validateRoomForGameCreation: roomUser found = {}", roomUser.getId());

        if (roomUser.getUserRole() != RoomUserRole.ADMIN) {
            log.error("validateRoomForGameCreation: user is not an admin");
            throw new RoomUserPrivilegeException(roomUser);
        }

        Long roomUserCount = this.roomUserService.getUserCount(roomId);
        if (roomUserCount == 1) {
            log.error("validateRoomForGameCreation: cannot create game with only one user found with number={}",
                    roomUserCount);
            throw new NonAdminUsersNotFound(room);
        }

        log.info("validateRoomForGameCreation: room found with roomId={}");
        Long nonReadyUsers = this.roomUserService.getUserStatusCount(roomId, RoomUserStatus.NOT_READY);
        log.info("validateRoomForGameCreation: nonReadyUsers={}", nonReadyUsers);
        if (nonReadyUsers != 0) {
            log.error("validateRoomForGameCreation: non ready users found with number={}", nonReadyUsers);
            throw new UsersNotReadyForGame(roomId);
        }

        return room;
    }

    public GetAuthenticatedRoomUserData getAuthenticatedRoomUserData(Long roomId) throws Exception {
        log.info("getAuthenticatedRoomUserData: fetching for roomId={}", roomId);

        Room room = this.roomRepository.getReferenceById(roomId);
        if (room == null) {
            log.error("getAuthenticatedRoomUserData: no room found with id={}", roomId);
            throw new RoomNotFoundException(roomId);
        }

        RoomUser roomUser = this.roomUserService.getAuthenticatedRoomUser(room);
        log.info("getAuthenticatedRoomUserData: roomUser found with id={}", roomUser.getId());

        System.out.println(roomUser);
        return GetAuthenticatedRoomUserData.builder().id(room.getId()).roomId(roomUser.getRoom().getId())
                .userId(roomUser.getUser().getId()).isAdmin(roomUser.getUserRole() == RoomUserRole.ADMIN)
                .isReady(roomUser.getStatus() == RoomUserStatus.READY).build();

    }

    public GameCreationValidationResponse gameCreationValidation(Long roomId) throws Exception {
        log.info("validateGameCreationValidation: request received with roomId={}", roomId);

        GameCreationValidationResponse response = new GameCreationValidationResponse();

        try {
            Room room = this.validateRoomForGameCreation(roomId);
            log.info("validateGameCreationValidation: room validation success for id={}", room.getId());
            return response;
        } catch (Exception exception) {
            log.error("validateGameCreationValidation: room validation failure with message={}",
                    exception.getMessage());
            response.setSuccess(false);
            response.setReason(exception.getMessage());
            return response;
        }
    }

}
