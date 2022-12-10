package com.karansaklani20.multiwordle.games.dto;

import com.karansaklani20.multiwordle.games.models.RoundMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateGameRequest {
    private RoundMode roundMode;
    private Integer numberRounds;
    private Long roomId;
}
