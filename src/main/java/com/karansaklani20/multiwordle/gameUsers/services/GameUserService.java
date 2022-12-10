package com.karansaklani20.multiwordle.gameUsers.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.karansaklani20.multiwordle.gameUsers.exceptions.GameUserNotFound;
import com.karansaklani20.multiwordle.gameUsers.models.GameUser;
import com.karansaklani20.multiwordle.gameUsers.repository.GameUserRepository;
import com.karansaklani20.multiwordle.games.models.Game;
import com.karansaklani20.multiwordle.roomUsers.dto.RoomUserData;
import com.karansaklani20.multiwordle.roomUsers.services.RoomUserService;
import com.karansaklani20.multiwordle.rooms.model.Room;
import com.karansaklani20.multiwordle.users.models.User;
import com.karansaklani20.multiwordle.users.services.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class GameUserService {
    private final RoomUserService roomUserService;
    private final GameUserRepository gameUserRepository;
    private final UserService userService;

    public List<GameUser> createGameUsersForRoomAndGame(Room room, Game game) throws Exception {
        log.info("createGameUsersForRoom: roomId={}", room.getId());

        List<RoomUserData> userData = this.roomUserService.getUserDataForRoom(room);
        log.info("createGameUsersForRoom: users found={}", userData.size());

        List<GameUser> gameUsers = new ArrayList<>();

        for (int i = 0; i < userData.size(); i++) {
            User user = userData.get(i).getUser();
            gameUsers.add(GameUser.builder().user(user).game(game).build());
        }

        if (gameUsers.size() == 0) {
            return gameUsers;
        }

        return this.gameUserRepository.saveAll(gameUsers);
    }

    public GameUser getAuthenticatedGameUser(Game game) throws Exception {
        log.info("getAuthenticatedGameUser: fetching authenticated game user for game={}", game);

        User user = this.userService.getUserFromAuthContext();
        log.info("getAuthenticatedGameUser: authenticated user with id={}", user.getId());

        GameUser gameUser = this.gameUserRepository.findByGameAndUser(game, user);

        if (gameUser == null) {
            throw new GameUserNotFound(game.getId(), user.getId());
        }
        return gameUser;
    }

    public List<GameUser> getGameUsersForGame(Game game) throws Exception {
        log.info("getGameUsersForGame: gameId={}", game.getId());

        return this.gameUserRepository.findByGame(game);
    }

    public List<GameUser> getGameUsersForGames(List<Long> gameIds) throws Exception {
        if ((gameIds == null) || (gameIds.size() == 0)) {
            return new ArrayList<>();
        }
        return this.gameUserRepository.getGameUsersForGames(gameIds);
    }
}
