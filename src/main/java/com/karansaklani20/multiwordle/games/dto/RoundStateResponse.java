package com.karansaklani20.multiwordle.games.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoundStateResponse {
    private Boolean completedForUser;
    private Boolean roundCompleted;
}
