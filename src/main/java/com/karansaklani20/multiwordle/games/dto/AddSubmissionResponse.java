package com.karansaklani20.multiwordle.games.dto;

import java.util.List;
import java.util.Map;

import com.karansaklani20.multiwordle.gameUsers.models.GameUser;
import com.karansaklani20.multiwordle.games.models.ResponseValue;
import com.karansaklani20.multiwordle.users.models.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddSubmissionResponse {
    private Long id;
    private Boolean isCorrect;
    private Map<Integer, ResponseValue> responseMap;
    private String trial;
    private Boolean completedForUser;
    private Boolean roundCompleted;
    private Boolean gameCompleted;
    private User gameWinner;
    private GameUser winnerUser;
    private List<GameScore> score;
}
