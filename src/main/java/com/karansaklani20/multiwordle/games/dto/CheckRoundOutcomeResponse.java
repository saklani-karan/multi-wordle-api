package com.karansaklani20.multiwordle.games.dto;

import com.karansaklani20.multiwordle.gameUsers.models.GameUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckRoundOutcomeResponse {
    private GameUser winner;
    private Boolean gameCompleted;
}
