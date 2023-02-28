package com.karansaklani20.multiwordle.rooms.dto;

import java.util.List;

import com.karansaklani20.multiwordle.roomUsers.dto.RoomUserData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomEntityResponse {
    private Long id;
    private List<RoomUserData> users;
    private Boolean isAdmin;
}
