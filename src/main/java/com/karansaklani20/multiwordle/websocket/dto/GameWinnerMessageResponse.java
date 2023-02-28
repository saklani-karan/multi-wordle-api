package com.karansaklani20.multiwordle.websocket.dto;

import java.util.List;

import com.karansaklani20.multiwordle.games.dto.GameScore;
import com.karansaklani20.multiwordle.users.models.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameWinnerMessageResponse {
    private User winner;
    private String correctWord;
    private List<GameScore> score;
}
