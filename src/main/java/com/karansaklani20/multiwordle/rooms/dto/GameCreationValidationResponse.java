package com.karansaklani20.multiwordle.rooms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameCreationValidationResponse {
    @Builder.Default
    private Boolean success = true;
    private String reason;
}
