package com.karansaklani20.multiwordle.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomGameCreatIonMessage {
    private Long gameId;
    private Long roomId;
}
