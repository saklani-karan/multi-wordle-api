package com.karansaklani20.multiwordle.games.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.karansaklani20.multiwordle.gameUsers.models.GameUser;
import com.karansaklani20.multiwordle.games.dto.AddSubmissionRequest;
import com.karansaklani20.multiwordle.games.dto.AddSubmissionResponse;
import com.karansaklani20.multiwordle.games.dto.CreateGameRequest;
import com.karansaklani20.multiwordle.games.dto.GetGameDataResponse;
import com.karansaklani20.multiwordle.games.dto.ListGamesFilters;
import com.karansaklani20.multiwordle.games.dto.ListGamesResponse;
import com.karansaklani20.multiwordle.games.dto.SubmissionAlgorithmResponse;
import com.karansaklani20.multiwordle.games.dto.SubmissionWithSubmissionMap;
import com.karansaklani20.multiwordle.games.models.Game;
import com.karansaklani20.multiwordle.games.models.Round;
import com.karansaklani20.multiwordle.games.models.Submission;
import com.karansaklani20.multiwordle.games.models.SubmissionMapEntity;
import com.karansaklani20.multiwordle.games.services.GameService;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/games")
@AllArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping("/create")
    public Game createGameForRoom(@RequestBody CreateGameRequest gameRequest) throws Exception {
        return this.gameService.createGameForRoom(gameRequest);
    }

    @PostMapping("/{gameId}/addRound")
    public Round addRoundForGame(@PathVariable("gameId") Long gameId) throws Exception {
        return this.gameService.addRoundToGame(gameId);
    }

    @PostMapping("/{gameId}/submit")
    public AddSubmissionResponse addSubmission(@PathVariable("gameId") Long gameId,
            @RequestBody AddSubmissionRequest submissionRequest) throws Exception {
        return this.gameService.addSubmission(gameId, submissionRequest);
    }

    @GetMapping(value = "/{trial}/{correctWord}")
    public SubmissionAlgorithmResponse testSubmissionAlgorithmResponse(@PathVariable("trial") String trial,
            @PathVariable("correctWord") String correctWord) throws Exception {
        return this.gameService.testSubmissionAlgorithmResponse(correctWord, trial);
    }

    @GetMapping(value = "/{gameId}/me")
    public GameUser getUserForGame(@PathVariable("gameId") Long gameId) throws Exception {
        return this.gameService.getAuthenticatedUserForGame(gameId);
    }

    @GetMapping(value = "/{gameId}")
    public GetGameDataResponse getGameData(@PathVariable("gameId") Long gameId) throws Exception {
        return this.gameService.fetchGameStatus(gameId);
    }

    @GetMapping(value = "/{gameId}/currentRoundSubmissions")
    public List<SubmissionMapEntity> getSubmissionsWithSubmissionMap(@PathVariable("gameId") Long gameId)
            throws Exception {
        return this.gameService.getSubmissionsWithSubmissionMap(gameId);
    }

    @GetMapping(value = "/list")
    public ListGamesResponse listGames(@RequestParam("isCompleted") Optional<Boolean> isCompleted,
            @RequestParam("pageSize") Optional<Integer> pageSize,
            @RequestParam("pageNumber") Optional<Integer> pageNumber)
            throws Exception {
        ListGamesFilters filters = new ListGamesFilters();
        if (isCompleted.isPresent()) {
            filters.setIsCompleted(isCompleted.get());
        }
        if (pageNumber.isPresent()) {
            filters.setPageNumber(pageNumber.get());
        }
        if (pageSize.isPresent()) {
            filters.setPageSize(pageSize.get());
        }
        return this.gameService.listGames(filters);
    }

}
