package com.karansaklani20.multiwordle.games.dto;

import java.util.List;

import com.karansaklani20.multiwordle.games.models.Game;
import com.karansaklani20.multiwordle.users.models.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameInfo {
    private Game game;
    private List<User> users;
}
