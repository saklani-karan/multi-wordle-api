package com.karansaklani20.multiwordle.games.dto;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.karansaklani20.multiwordle.games.models.ResponseValue;
import com.karansaklani20.multiwordle.games.models.SubmissionMapEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class SubmissionWithSubmissionMap {
    @Id
    private Long id;
    private String trial;
    private Boolean isCorrect;
    private Integer index;
    @Enumerated(EnumType.STRING)
    private ResponseValue value;
}
