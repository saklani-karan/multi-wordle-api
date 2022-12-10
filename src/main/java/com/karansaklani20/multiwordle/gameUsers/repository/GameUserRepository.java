package com.karansaklani20.multiwordle.gameUsers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.karansaklani20.multiwordle.gameUsers.models.GameUser;
import com.karansaklani20.multiwordle.games.models.Game;
import com.karansaklani20.multiwordle.users.models.User;

public interface GameUserRepository extends JpaRepository<GameUser, Long> {
    GameUser findByGameAndUser(Game game, User user);

    List<GameUser> findByGame(Game game);

    @Query(value = """
            SELECT * FROM game_user
            WHERE game_id IN :gameIds""", nativeQuery = true)
    List<GameUser> getGameUsersForGames(List<Long> gameIds);
}
