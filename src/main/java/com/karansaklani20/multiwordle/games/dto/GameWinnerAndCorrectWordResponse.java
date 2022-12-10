package com.karansaklani20.multiwordle.games.dto;

import java.util.List;

import com.karansaklani20.multiwordle.users.models.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameWinnerAndCorrectWordResponse {
    private User winner;
    private String correctWord;
    private List<GameScore> score;
}
