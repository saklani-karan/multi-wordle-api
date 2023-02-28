package com.karansaklani20.multiwordle.games.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.karansaklani20.multiwordle.games.models.SubmissionMapEntity;

public interface SubmissionMapEntitiesRepository extends JpaRepository<SubmissionMapEntity, Long> {
    @Query(value = """
            SELECT submission_map_entities.*
            FROM submissions
            LEFT JOIN submission_map_entities
            ON submission_map_entities.submission_id = submissions.id
            WHERE submissions.game_user_id=:gameUserId AND submissions.round_id=:roundId
                """, nativeQuery = true)
    List<SubmissionMapEntity> getSubmissionMapEntitiesForRoundAndGameUser(@Param("gameUserId") Long gameUserId,
            @Param("roundId") Long roundId);
}
