package com.karansaklani20.multiwordle.games.dto;

import java.util.List;

import com.karansaklani20.multiwordle.games.models.SubmissionMapEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionAlgorithmResponse {

    private Boolean isCorrect;
    private List<SubmissionMapEntity> submissionMap;
}
