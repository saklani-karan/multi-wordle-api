package com.karansaklani20.multiwordle.games.dto;

import com.karansaklani20.multiwordle.gameUsers.models.GameUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoundWinnerAndCorrectWordResponse {
    private GameUser winner;
    private String correctWord;
}
