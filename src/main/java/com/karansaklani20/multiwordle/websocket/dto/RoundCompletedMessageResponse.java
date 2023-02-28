package com.karansaklani20.multiwordle.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoundCompletedMessageResponse {
    private Long gameId;
    private Boolean roundCompleted;
}
