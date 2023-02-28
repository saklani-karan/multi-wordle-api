package com.karansaklani20.multiwordle.games.dto;

import java.util.List;

import com.karansaklani20.multiwordle.gameUsers.models.GameUser;
import com.karansaklani20.multiwordle.games.models.Game;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetGameDataResponse {
    private Game game;
    private Integer currentRound;
    private List<AddSubmissionResponse> submissionResponses;
    private Boolean completedForUser;
    private Boolean roundCompleted;
    private GameUser roundWinner;
    private List<GameScore> score;
}
