package com.karansaklani20.multiwordle.games.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.karansaklani20.multiwordle.gameUsers.models.GameUser;
import com.karansaklani20.multiwordle.games.models.Round;
import com.karansaklani20.multiwordle.games.models.Submission;
import com.karansaklani20.multiwordle.games.dto.SubmissionWithSubmissionMap;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    @Query(value = "SELECT COUNT(id) FROM submissions WHERE round_id=:roundId", nativeQuery = true)
    long getRoundSubmissionCount(@Param("roundId") Long roundId);

    @Query(value = "SELECT COUNT(id) FROM submissions WHERE round_id=:roundId AND game_user_id=:gameUserId", nativeQuery = true)
    long getRoundSubmissionCountForUser(@Param("roundId") Long roundId, @Param("gameUserId") Long gameUserId);

    List<Submission> findByIsCorrectAndRound(Boolean isCorrect, Round round);

    List<Submission> findByIsCorrectAndRoundAndGameUser(Boolean isCorrect, Round round, GameUser gameUser);

    List<Submission> findByRound(Round round);

    List<Submission> findByGameUserAndRound(GameUser gameUser, Round round);

    @Query(nativeQuery = true, name = "getSubmissionsWithSubmissionMap")
    List<SubmissionWithSubmissionMap> getSubmissionsWithSubmissionMap(@Param("roundId") Long roundId,
            @Param("gameUserId") Long gameUserId);
}
