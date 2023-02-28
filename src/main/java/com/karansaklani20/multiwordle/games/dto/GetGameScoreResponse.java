package com.karansaklani20.multiwordle.games.dto;

import java.util.List;

import com.karansaklani20.multiwordle.gameUsers.models.GameUser;
import com.karansaklani20.multiwordle.users.models.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetGameScoreResponse {
    private List<GameScore> scores;
    private User winner;
    @Builder.Default
    private Boolean gameCompleted = false;
}
