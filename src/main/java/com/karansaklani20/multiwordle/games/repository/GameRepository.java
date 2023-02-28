package com.karansaklani20.multiwordle.games.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.karansaklani20.multiwordle.games.models.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Game findGameByRoomId(Long roomId);

    // @Query(value = """
    // SELECT games.* FROM game_user
    // LEFT JOIN games
    // ON games.id=game_user.game_id
    // WHERE game_user.user_id=:userId
    // AND games.game_completed=:isCompleted
    // """, nativeQuery = true)
    // List<Game> findGamesForUser(@Param("userId") Long userId,
    // @Param("isCommpleted") Boolean isCompleted);

    @Query(value = """
            SELECT games.* FROM game_user
            LEFT JOIN games
            ON games.id=game_user.game_id
            WHERE game_user.user_id=?1
            AND ( ?2 IS NULL OR games.game_completed=?2)""", countQuery = """
            SELECT COUNT(games.id) FROM game_user
            LEFT JOIN games
            ON games.id=game_user.game_id
            WHERE game_user.user_id=?1
            AND ( ?2 IS NULL OR games.game_completed=?2)
            """, nativeQuery = true)
    Page<Game> findGamesForUser(Long userId, Boolean isCompleted,
            Pageable pageable);
}
