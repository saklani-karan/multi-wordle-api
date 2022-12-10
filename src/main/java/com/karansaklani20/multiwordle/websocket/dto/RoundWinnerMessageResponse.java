package com.karansaklani20.multiwordle.websocket.dto;

import com.karansaklani20.multiwordle.gameUsers.models.GameUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoundWinnerMessageResponse {
    private GameUser winner;
    private String correctWord;
}
