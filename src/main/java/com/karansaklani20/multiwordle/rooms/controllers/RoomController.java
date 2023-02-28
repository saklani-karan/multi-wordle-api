package com.karansaklani20.multiwordle.rooms.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.karansaklani20.multiwordle.rooms.dto.AdmitOrCreateUserResponse;
import com.karansaklani20.multiwordle.rooms.dto.GameCreationValidationResponse;
import com.karansaklani20.multiwordle.rooms.dto.GetAuthenticatedRoomUserData;
import com.karansaklani20.multiwordle.rooms.dto.RoomEntityResponse;
import com.karansaklani20.multiwordle.rooms.services.RoomService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/api/rooms")
@AllArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/create")
    public AdmitOrCreateUserResponse createRoomForUser() throws Exception {
        return this.roomService.createRoomForUser();
    }

    @PostMapping("/{id}/admit")
    public AdmitOrCreateUserResponse addUserToRoom(@PathVariable(name = "id") Long id) throws Exception {
        return this.roomService.addUserToRoom(id);
    }

    @GetMapping("/{id}")
    public RoomEntityResponse getRoomById(@PathVariable(name = "id") Long id) throws Exception {
        return this.roomService.getRoomWithUsersById(id);
    }

    @GetMapping(value = "/{roomId}/me")
    public GetAuthenticatedRoomUserData getGameData(@PathVariable(name = "roomId") Long roomId) throws Exception {
        return this.roomService.getAuthenticatedRoomUserData(roomId);
    }

    @GetMapping(value = "/{roomId}/validateGameCreation")
    public GameCreationValidationResponse gameCreationValidationResponse(@PathVariable(name = "roomId") Long roomId)
            throws Exception {
        return this.roomService.gameCreationValidation(roomId);
    }

}
