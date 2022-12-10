package com.karansaklani20.multiwordle.games.dto;

import java.util.Map;

import com.karansaklani20.multiwordle.games.models.ResponseValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionsWithAlgorithmResponse {
    private Long id;
    private String trial;
    private Boolean isCorrect;
    private Map<Integer, ResponseValue> responseMap;
}
