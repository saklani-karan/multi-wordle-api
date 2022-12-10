package com.karansaklani20.multiwordle.games.models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "submission_map_entities")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionMapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer index;

    private Character letter;

    @Enumerated(EnumType.STRING)
    private ResponseValue value;

    @OneToOne
    private Submission submission;
}
