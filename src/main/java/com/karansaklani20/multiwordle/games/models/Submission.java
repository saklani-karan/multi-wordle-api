package com.karansaklani20.multiwordle.games.models;

import java.util.Date;
import java.util.List;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.karansaklani20.multiwordle.gameUsers.models.GameUser;
import com.karansaklani20.multiwordle.games.dto.SubmissionWithSubmissionMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table(name = "submissions")
@SqlResultSetMapping(name = "SubmissionsWithSubmissionMapMapping", entities = @EntityResult(entityClass = SubmissionWithSubmissionMap.class, fields = {
        @FieldResult(name = "id", column = "id"),
        @FieldResult(name = "isCorrect", column = "is_correct"),
        @FieldResult(name = "trial", column = "trial"),
        @FieldResult(name = "index", column = "map_index"),
        @FieldResult(name = "value", column = "map_value")
}))
@NamedNativeQuery(name = "getSubmissionsWithSubmissionMap", query = """
            SELECT submissions.*, submission_map_entities.index as map_index, submission_map_entities.value as map_value
            FROM submissions
            LEFT JOIN submission_map_entities
            ON submission_map_entities.submission_id = submissions.id
            WHERE submissions.game_user_id=:gameUserId AND submissions.round_id=:roundId
        """, resultSetMapping = "SubmissionsWithSubmissionMapMapping", resultClass = Submission.class)
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Round round;
    @OneToOne
    private GameUser gameUser;
    private Date submissionTime;
    private String trial;
    private Boolean isCorrect;
}
