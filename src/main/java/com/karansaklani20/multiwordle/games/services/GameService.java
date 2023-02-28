package com.karansaklani20.multiwordle.games.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.karansaklani20.multiwordle.gameUsers.exceptions.RoundsNotCompleted;
import com.karansaklani20.multiwordle.gameUsers.models.GameUser;
import com.karansaklani20.multiwordle.gameUsers.services.GameUserService;
import com.karansaklani20.multiwordle.games.dto.AddSubmissionRequest;
import com.karansaklani20.multiwordle.games.dto.AddSubmissionResponse;
import com.karansaklani20.multiwordle.games.dto.CheckRoundOutcomeResponse;
import com.karansaklani20.multiwordle.games.dto.CreateGameRequest;
import com.karansaklani20.multiwordle.games.dto.GameInfo;
import com.karansaklani20.multiwordle.games.dto.GameScore;
import com.karansaklani20.multiwordle.games.dto.GameWinnerAndCorrectWordResponse;
import com.karansaklani20.multiwordle.games.dto.GetGameDataResponse;
import com.karansaklani20.multiwordle.games.dto.GetGameScoreResponse;
import com.karansaklani20.multiwordle.games.dto.ListGamesFilters;
import com.karansaklani20.multiwordle.games.dto.ListGamesResponse;
import com.karansaklani20.multiwordle.games.dto.RoundStateResponse;
import com.karansaklani20.multiwordle.games.dto.RoundWinnerAndCorrectWordResponse;
import com.karansaklani20.multiwordle.games.dto.SubmissionAlgorithmResponse;
import com.karansaklani20.multiwordle.games.exceptions.CurrentRoundNotFound;
import com.karansaklani20.multiwordle.games.exceptions.GameAlreadyExists;
import com.karansaklani20.multiwordle.games.exceptions.GameDoesNotExist;
import com.karansaklani20.multiwordle.games.exceptions.GameNotCompletedException;
import com.karansaklani20.multiwordle.games.exceptions.InvalidTrialException;
import com.karansaklani20.multiwordle.games.exceptions.MaxRoundsAlreadyReached;
import com.karansaklani20.multiwordle.games.exceptions.MaxSubmissionsReached;
import com.karansaklani20.multiwordle.games.exceptions.RoundAlreadyCompletedException;
import com.karansaklani20.multiwordle.games.models.Game;
import com.karansaklani20.multiwordle.games.models.ResponseValue;
import com.karansaklani20.multiwordle.games.models.Round;
import com.karansaklani20.multiwordle.games.models.RoundMode;
import com.karansaklani20.multiwordle.games.models.Submission;
import com.karansaklani20.multiwordle.games.models.SubmissionMapEntity;
import com.karansaklani20.multiwordle.games.repository.GameRepository;
import com.karansaklani20.multiwordle.games.repository.RoundRepository;
import com.karansaklani20.multiwordle.games.repository.SubmissionMapEntitiesRepository;
import com.karansaklani20.multiwordle.games.repository.SubmissionRepository;
import com.karansaklani20.multiwordle.rooms.model.Room;
import com.karansaklani20.multiwordle.rooms.services.RoomService;
import com.karansaklani20.multiwordle.users.models.User;
import com.karansaklani20.multiwordle.users.services.UserService;
import com.karansaklani20.multiwordle.words.models.Word;
import com.karansaklani20.multiwordle.words.services.WordService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class GameService {
    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionMapEntitiesRepository submissionMapEntitiesRepository;
    private final RoomService roomService;
    private final GameUserService gameUserService;
    private final WordService wordService;
    private final UserService userService;

    @Transactional(rollbackFor = { Exception.class })
    public Game createGameForRoom(CreateGameRequest gameRequest) throws Exception {
        Long roomId = gameRequest.getRoomId();
        log.info("createGameForRoom: roomId={}", roomId);

        System.out.println(gameRequest);

        Game prevGame = this.gameRepository.findGameByRoomId(roomId);
        if (prevGame != null) {
            log.error("createGameForRoom: previous game already exists with id={}", prevGame.getId());
            throw new GameAlreadyExists(roomId);
        }

        Room room = this.roomService.validateRoomForGameCreation(roomId);
        log.info("createGameForRoom: room found with roomId={}", room.getId());

        Game game = this.gameRepository.save(Game.builder().nRounds(gameRequest.getNumberRounds())
                .roundMode(gameRequest.getRoundMode()).roomId(roomId).gameCompleted(false).build());
        log.info("createGameForRoom: new game saved with id={}", game.getId());

        try {
            Round round = this.createNewRoundForGame(game);
            log.info("createGameForRoom: new round created with id={}", round.getId());
        } catch (Exception exception) {
            log.error("createGameForRoom: an exception occured on round creation with message={}",
                    exception.getMessage());
            throw exception;
        }

        List<GameUser> gameUsers = this.gameUserService.createGameUsersForRoomAndGame(room, game);
        log.info("createGameForRoom: gameUsers created = {}", gameUsers.size());

        return game;
    }

    public GetGameDataResponse fetchGameStatus(Long gameId) throws Exception {
        log.info("getGameData: request received with gameId", gameId);

        Game game = this.getGameById(gameId);
        log.info("getGameData: game found with id={}", game.getId());

        GameUser gameUser = this.gameUserService.getAuthenticatedGameUser(game);
        log.info("getGameData: gameUser found with id={}", gameUser.getId());

        List<Round> round = this.roundRepository.findByGameAndCurrentRound(game, true);
        if (round.size() == 0) {
            log.error("getGameData: no current round for game with id={}", game.getId());
            throw new CurrentRoundNotFound(gameId);
        }
        Round currentRound = round.get(0);

        List<Submission> submissions = this.submissionRepository.findByGameUserAndRound(gameUser, currentRound);
        log.info("getGameData: submissions found = {}", submissions.size());
        List<SubmissionMapEntity> submissionMapEntities = this.submissionMapEntitiesRepository
                .getSubmissionMapEntitiesForRoundAndGameUser(gameUser.getId(), currentRound.getId());
        log.info("getGameData: submission entities found={}", submissionMapEntities.size());

        Map<Long, List<SubmissionMapEntity>> submissionMap = new HashMap<>();

        for (int i = 0; i < submissionMapEntities.size(); i++) {
            SubmissionMapEntity submissionMapEntity = submissionMapEntities.get(i);
            Submission submission = submissionMapEntity.getSubmission();

            if (submissionMap.containsKey(submission.getId())) {
                List<SubmissionMapEntity> temp = submissionMap.get(submission.getId());
                temp.add(submissionMapEntity);
                submissionMap.put(submission.getId(), temp);
                continue;
            }
            List<SubmissionMapEntity> temp = new ArrayList<>();
            temp.add(submissionMapEntity);
            submissionMap.put(submission.getId(), temp);
        }

        List<AddSubmissionResponse> submissionResponses = new ArrayList<>();

        for (int i = 0; i < submissions.size(); i++) {
            Submission submission = submissions.get(i);
            AddSubmissionResponse submissionResponse = AddSubmissionResponse.builder().id(submission.getId())
                    .isCorrect(submission.getIsCorrect()).trial(submission.getTrial()).build();
            if (!submissionMap.containsKey(submission.getId())) {
                continue;
            }
            List<SubmissionMapEntity> entities = submissionMap.get(submission.getId());
            Map<Integer, ResponseValue> responseMap = this.createResponseMapFromEntities(entities);
            submissionResponse.setResponseMap(responseMap);
            submissionResponses.add(submissionResponse);
        }

        RoundStateResponse roundState = this.getStateForRound(currentRound, game, gameUser);
        GetGameScoreResponse gameScore = this.getGameScore(game);

        GetGameDataResponse gameDataResponse = GetGameDataResponse.builder().game(game)
                .currentRound(currentRound.getNRound())
                .completedForUser(roundState.getCompletedForUser())
                .roundCompleted(roundState.getRoundCompleted())
                .submissionResponses(submissionResponses)
                .roundWinner(currentRound.getWinner())
                .build();

        if (game.getGameCompleted() || (game.getWinner() != null)) {
            gameDataResponse.setScore(gameScore.getScores());
        }

        return gameDataResponse;
    }

    public GameUser getAuthenticatedUserForGame(Long gameId) throws Exception {
        log.info("getAuthenticatedUserForGame: request received for gameId={}", gameId);

        Game game = this.getGameById(gameId);
        log.info("getAuthenticatedUserForGame: game found with id={}", game.getId());

        return this.gameUserService.getAuthenticatedGameUser(game);

    }

    public Round addRoundToGame(Long gameId) throws Exception {
        log.info("addRoundToGame: adding a new round to game with id={}", gameId);

        Game game = this.getGameById(gameId);
        log.info("addRoundToGame: game found with id={}", gameId);

        return this.createNewRoundForGame(game);
    }

    @Transactional(rollbackFor = { Exception.class })
    private Round createNewRoundForGame(Game game) throws Exception {
        log.info("createNewRoundForGame: gameId={} and nRound={}", game.getId());

        List<Round> rounds = this.roundRepository.findByGame(game);
        log.info("createNewRoundForGame: number of previous rounds found={}", rounds.size());
        Integer maxRounds = 0;
        List<Long> wordIds = new ArrayList<>();

        for (int i = 0; i < rounds.size(); i++) {
            Round round = rounds.get(i);
            if (((round.getCompleted() != null) && !round.getCompleted()) && (round.getWinner() == null)) {
                log.info("createNewRoundForGame: round has not been completed yet with id={}", round.getId());
                throw new RoundsNotCompleted(game);
            }
            Word word = round.getWord();
            maxRounds = Math.max(maxRounds, round.getNRound());
            round.setCurrentRound(false);
            wordIds.add(word.getId());
        }
        log.info("createNewRoundForGame: maxRounds={} and wordsIds={}", maxRounds, wordIds);

        if (maxRounds >= game.getNRounds()) {
            log.error("createNewRoundForGame: maxRounds already reached for game with id={}", game.getId());
            throw new MaxRoundsAlreadyReached(game.getId(), maxRounds);
        }

        if (rounds.size() > 0) {
            this.roundRepository.saveAll(rounds);
        }

        Word word = this.wordService.getRandomUnusedWord(wordIds);
        log.info("createNewRoundForGame: random unused word found with id={}", word.getId());

        return this.roundRepository
                .save(Round.builder().game(game).word(word).nRound(maxRounds + 1).currentRound(true).build());
    }

    public Game getGameById(Long gameId) {
        Optional<Game> game = this.gameRepository.findById(gameId);
        if (game.isEmpty()) {
            log.error("addRoundToGame: no game found with gameId={}", gameId);
            throw new GameDoesNotExist(gameId);
        }
        return game.get();
    }

    public List<SubmissionMapEntity> getSubmissionsWithSubmissionMap(Long gameId) throws Exception {
        Game game = this.getGameById(gameId);
        log.info("getGameData: game found with id={}", game.getId());

        GameUser gameUser = this.gameUserService.getAuthenticatedGameUser(game);
        log.info("getGameData: gameUser found with id={}", gameUser.getId());

        List<Round> round = this.roundRepository.findByGameAndCurrentRound(game, true);
        if (round.size() == 0) {
            log.error("getGameData: no current round for game with id={}", game.getId());
            throw new CurrentRoundNotFound(gameId);
        }
        Round currentRound = round.get(0);

        return this.submissionMapEntitiesRepository.getSubmissionMapEntitiesForRoundAndGameUser(gameUser.getId(),
                currentRound.getId());
    }

    @Transactional(rollbackFor = { Exception.class })
    public AddSubmissionResponse addSubmission(Long gameId, AddSubmissionRequest submissionRequest) throws Exception {
        log.info("addSubmission: submission request received with trial={}", submissionRequest.getTrial());
        String trial = submissionRequest.getTrial().toLowerCase();

        Game game = this.getGameById(gameId);
        log.info("addSubmission: game found with id={}", game.getId());

        List<Round> round = this.roundRepository.findByGameAndCurrentRound(game, true);
        if (round.size() == 0) {
            throw new CurrentRoundNotFound(gameId);
        }
        Round currentRound = round.get(0);
        Word correctWord = round.get(0).getWord();

        GameUser gameUser = this.gameUserService.getAuthenticatedGameUser(game);
        log.info("addSubmission: game user found with id={}", gameUser.getId());

        if (currentRound.getWinner() != null) {
            log.error("addSubmission: current round already has a winner={}", currentRound.getWinner().getId());
            throw new RoundAlreadyCompletedException(game);
        }

        List<Submission> correctSubmissions = this.submissionRepository.findByIsCorrectAndRoundAndGameUser(true,
                currentRound,
                gameUser);
        if (correctSubmissions.size() > 0) {
            log.error("addSubmission: current round already has a correct submission from user={}",
                    gameUser.getId());
            throw new RoundAlreadyCompletedException(game, gameUser);
        }

        Long nSubmissions = this.submissionRepository.getRoundSubmissionCountForUser(currentRound.getId(),
                gameUser.getId());
        log.info("addSubmission: total number of submission = {}", nSubmissions);

        if ((game.getMaxTrials() != null) && (game.getMaxTrials() <= nSubmissions)) {
            log.info("addSubmission: maximum number of submissions reached for game with gameId={} and nSubmissions={}",
                    gameId, nSubmissions);
            throw new MaxSubmissionsReached(game.getMaxTrials(), gameId);
        }

        Submission submission = this.submissionRepository
                .save(Submission.builder().round(round.get(0)).trial(trial)
                        .isCorrect(correctWord.getValue().equals(trial)).gameUser(gameUser)
                        .submissionTime(new Date())
                        .build());
        log.info("addSubmission: submission created with id={}", submission.getId());

        SubmissionAlgorithmResponse algorithmResponse = submissionAlgorithm(correctWord.getValue(),
                trial,
                submission);
        log.info("addSubmission: algorithmResponse returned with isCorrect={}", algorithmResponse.getIsCorrect());

        Map<Integer, ResponseValue> responseMap = this
                .createResponseMapFromEntities(algorithmResponse.getSubmissionMap());
        this.submissionMapEntitiesRepository.saveAll(algorithmResponse.getSubmissionMap());
        log.info("addSubmission: converted to response map and saved entities");

        CheckRoundOutcomeResponse roundOutcomeResponse = this.checkRoundOutcome(round.get(0), game, submission);
        RoundStateResponse roundState = this.getStateForRound(currentRound, game, gameUser);

        if ((roundOutcomeResponse.getWinner() != null) || (roundState.getRoundCompleted())) {
            currentRound.setCompleted(true);
            if (roundOutcomeResponse.getWinner() != null) {
                log.info("addSubmission: round outcome found with a winner = {}",
                        roundOutcomeResponse.getWinner().getId());
                currentRound.setWinner(roundOutcomeResponse.getWinner());
            }
            this.roundRepository.save(currentRound);
        }

        GetGameScoreResponse score = this.getGameScore(game);
        if (score.getGameCompleted() || (score.getWinner() != null)) {
            game.setGameCompleted(true);
            if (score.getWinner() != null) {
                game.setWinner(score.getWinner());
                this.gameRepository.save(game);
            }
        }

        AddSubmissionResponse submissionResponse = AddSubmissionResponse.builder()
                .isCorrect(algorithmResponse.getIsCorrect()).responseMap(responseMap)
                .completedForUser(roundState.getCompletedForUser())
                .roundCompleted(roundState.getRoundCompleted() || (roundOutcomeResponse.getWinner() != null))
                .winnerUser(roundOutcomeResponse.getWinner())
                .trial(submission.getTrial())
                .gameWinner(score.getWinner())
                .gameCompleted(score.getGameCompleted())
                .build();

        if (score.getGameCompleted() || (score.getWinner() != null)) {
            submissionResponse.setScore(score.getScores());
        }

        return submissionResponse;

    }

    public GameWinnerAndCorrectWordResponse getGameWinnerAndCorrectWord(Long gameId) throws Exception {
        Game game = this.getGameById(gameId);
        log.info("getGameData: game found with id={}", game.getId());

        List<Round> round = this.roundRepository.findByGameAndCurrentRound(game, true);
        if (round.size() == 0) {
            log.error("getGameData: no current round for game with id={}", game.getId());
            throw new CurrentRoundNotFound(gameId);
        }
        Round currentRound = round.get(0);

        GetGameScoreResponse gameScore = this.getGameScore(game);
        if (!gameScore.getGameCompleted() || (gameScore.getWinner() == null)) {
            throw new GameNotCompletedException(game);
        }

        return GameWinnerAndCorrectWordResponse.builder().winner(game.getWinner()).score(gameScore.getScores())
                .correctWord(currentRound.getWord().getValue()).build();
    }

    public RoundWinnerAndCorrectWordResponse getRoundWinnerAndCorrectWord(Long gameId) throws Exception {
        Game game = this.getGameById(gameId);
        log.info("getGameData: game found with id={}", game.getId());

        List<Round> round = this.roundRepository.findByGameAndCurrentRound(game, true);
        if (round.size() == 0) {
            log.error("getGameData: no current round for game with id={}", game.getId());
            throw new CurrentRoundNotFound(gameId);
        }
        Round currentRound = round.get(0);

        return RoundWinnerAndCorrectWordResponse.builder().winner(currentRound.getWinner())
                .correctWord(currentRound.getWord().getValue()).build();
    }

    private GetGameScoreResponse getGameScore(Game game) throws Exception {
        log.info("getGameScore: getting score for game = {}", game.getId());

        List<Round> rounds = this.roundRepository.findByGameAndCompleted(game, true);
        List<GameUser> gameUsers = this.gameUserService.getGameUsersForGame(game);
        GameUser winner = null;
        Map<Long, Integer> scoreMap = new HashMap<>();

        for (int i = 0; i < rounds.size(); i++) {
            Round round = rounds.get(i);
            if (round.getWinner() == null) {
                continue;
            }
            GameUser roundWinner = round.getWinner();
            Integer score = 0;
            if (scoreMap.containsKey(roundWinner.getId())) {
                score = scoreMap.get(roundWinner.getId());
            }
            score++;
            if (score > (game.getNRounds() / 2)) {
                winner = roundWinner;
            }
            scoreMap.put(roundWinner.getId(), score);
        }

        List<GameScore> gameScores = new ArrayList<>();
        for (int i = 0; i < gameUsers.size(); i++) {
            GameUser gameUser = gameUsers.get(i);
            Integer score = 0;
            if (scoreMap.containsKey(gameUser.getId())) {
                score = scoreMap.get(gameUser.getId());
            }
            gameScores.add(GameScore.builder().user(gameUser.getUser()).score(score).build());
        }

        // Collections.sort(gameScores, new ScoreComparator());
        gameScores.sort((o1, o2) -> o1.getScore() > o2.getScore() ? -1 : 1);

        return GetGameScoreResponse.builder().scores(gameScores).winner(winner != null ? winner.getUser() : null)
                .gameCompleted((winner != null) || (rounds.size() == game.getNRounds())).build();
    }

    private RoundStateResponse getStateForRound(Round round, Game game, GameUser gameUser) throws Exception {
        log.info("getStateForRound: getting state for round={} and game={}", round.getId(), game.getId());

        List<Submission> submissions = this.submissionRepository.findByRound(round);
        List<GameUser> gameUsers = this.gameUserService.getGameUsersForGame(game);
        log.info("getStateForRound: found submissions={} and gameUsers={}", submissions.size(), gameUsers.size());

        Map<Long, Integer> userSubmissionCountMap = new HashMap<>();
        List<GameUser> completedGameUsers = new ArrayList<>();
        Boolean completedForUser = false;
        for (int i = 0; i < submissions.size(); i++) {
            Submission submission = submissions.get(i);
            GameUser subGameUser = submission.getGameUser();
            Integer userSubmissionCount = 0;
            if (userSubmissionCountMap.containsKey(subGameUser.getId())) {
                userSubmissionCount = userSubmissionCountMap.get(subGameUser.getId());
            }
            userSubmissionCount += 1;
            userSubmissionCountMap.put(subGameUser.getId(), userSubmissionCount);
            if ((userSubmissionCount == game.getMaxTrials()) || (submission.getIsCorrect())) {
                completedGameUsers.add(subGameUser);
                if (subGameUser.getId() == gameUser.getId()) {
                    completedForUser = true;
                }
            }
        }

        return RoundStateResponse.builder().completedForUser(completedForUser)
                .roundCompleted((gameUsers.size() == completedGameUsers.size()) || (round.getWinner() != null)).build();

    }

    public SubmissionAlgorithmResponse testSubmissionAlgorithmResponse(String correctWord, String trial)
            throws Exception {
        return this.submissionAlgorithm(correctWord, trial, new Submission());
    }

    public ListGamesResponse listGames(ListGamesFilters filters) throws Exception {
        User user = this.userService.getUserFromAuthContext();

        Page<Game> page = this.gameRepository.findGamesForUser(user.getId(), filters.getIsCompleted(),
                PageRequest.of(filters.getPageNumber(), filters.getPageSize()));
        List<Game> games = page.getContent();

        List<Long> gameIds = new ArrayList<>();
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            gameIds.add(game.getId());
        }

        List<GameUser> gameUsers = this.gameUserService.getGameUsersForGames(gameIds);
        Map<Long, List<User>> gameUserMap = new HashMap<>();
        for (int i = 0; i < gameUsers.size(); i++) {
            GameUser gameUser = gameUsers.get(i);
            List<User> users = new ArrayList<>();
            Game game = gameUser.getGame();
            if (gameUserMap.containsKey(game.getId())) {
                users = gameUserMap.get(game.getId());
            }
            users.add(gameUser.getUser());
            gameUserMap.put(game.getId(), users);
        }

        List<GameInfo> aggGames = new ArrayList<>();
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            List<User> users = new ArrayList<>();
            if (gameUserMap.containsKey(game.getId())) {
                users = gameUserMap.get(game.getId());
            }
            aggGames.add(GameInfo.builder().game(game).users(users).build());
        }
        return ListGamesResponse.builder().gamesData(aggGames).pageInfo(page.getPageable())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).build();
    }

    private CheckRoundOutcomeResponse checkRoundOutcome(Round round, Game game, Submission submission)
            throws Exception {
        log.info("checkRoundOutcome: checking for round = {} and game = {}");

        if (game.getRoundMode() == RoundMode.TIME_BASED) {
            List<Submission> correctSubmissions = this.submissionRepository.findByIsCorrectAndRound(true, round);
            log.info("checkRoundOutcome: correct submissions = {}", correctSubmissions.size());

            GameUser firstUser = null;
            Long firstSubmissionTime = Long.MAX_VALUE;
            for (int i = 0; i < correctSubmissions.size(); i++) {
                Submission correctSubmission = correctSubmissions.get(i);
                if (correctSubmission.getSubmissionTime() == null) {
                    continue;
                }
                if (correctSubmission.getSubmissionTime().getTime() < firstSubmissionTime) {
                    firstUser = correctSubmission.getGameUser();
                    firstSubmissionTime = correctSubmission.getSubmissionTime().getTime();
                }
            }
            return CheckRoundOutcomeResponse.builder().winner(firstUser)
                    .build();
        }

        List<GameUser> gameUsers = this.gameUserService.getGameUsersForGame(game);
        log.info("checkRoundOutcome: game users found = {}", gameUsers.size());

        List<Submission> submissions = this.submissionRepository.findByRound(round);
        log.info("checkRoundOutcome: submissions found = {}", submissions.size());

        System.out.println(submissions);

        Set<Long> gameCompletedUserSet = new HashSet<>();
        Map<Long, Integer> trialMap = new HashMap<>();
        List<GameUser> correctGameUsers = new ArrayList<>();

        for (int i = 0; i < submissions.size(); i++) {
            Submission roundSubmission = submissions.get(i);
            GameUser subGameUser = roundSubmission.getGameUser();

            Integer tries = 0;
            if (trialMap.containsKey(subGameUser.getId())) {
                tries = trialMap.get(subGameUser.getId());
            }
            tries++;
            trialMap.put(subGameUser.getId(), tries);

            if (roundSubmission.getIsCorrect() || (tries == game.getMaxTrials())) {
                gameCompletedUserSet.add(subGameUser.getId());
                if (roundSubmission.getIsCorrect()) {
                    correctGameUsers.add(subGameUser);
                }
            }

        }

        if (gameCompletedUserSet.size() != gameUsers.size()) {
            return CheckRoundOutcomeResponse.builder().winner(null)
                    .build();
        }

        GameUser winnerUser = null;
        Integer minTries = Integer.MAX_VALUE;
        for (int i = 0; i < correctGameUsers.size(); i++) {
            GameUser correctUser = correctGameUsers.get(i);
            if (!trialMap.containsKey(correctUser.getId())) {
                System.out.println(correctUser.getId());
                continue;
            }
            Integer tries = trialMap.get(correctUser.getId());
            System.out.println(tries);
            if (tries < minTries) {
                winnerUser = correctUser;
                minTries = tries;
            } else if (tries == minTries) {
                winnerUser = null;
            }
        }

        return CheckRoundOutcomeResponse.builder().winner(winnerUser)
                .build();

    }

    private SubmissionAlgorithmResponse submissionAlgorithm(String correctWord, String trial, Submission submission)
            throws Exception {
        Map<Character, Integer> freqMap = new HashMap<>();
        List<SubmissionMapEntity> entities = new ArrayList<>();
        Map<Integer, Boolean> correctWordMap = new HashMap<>();

        if (correctWord.length() != trial.length()) {
            throw new InvalidTrialException(trial);
        }

        for (int i = 0; i < correctWord.length(); i++) {
            if (trial.charAt(i) == correctWord.charAt(i)) {
                correctWordMap.put(i, true);
                entities.add(SubmissionMapEntity
                        .builder()
                        .index(i)
                        .value(ResponseValue.CORRECT)
                        .letter(trial.charAt(i))
                        .submission(submission)
                        .build());
                continue;
            }
            Character letter = correctWord.charAt(i);
            if (freqMap.containsKey(letter)) {
                Integer freq = freqMap.get(letter);
                freqMap.put(letter, freq + 1);
                continue;
            }
            freqMap.put(letter, 1);
        }

        for (int i = 0; i < trial.length(); i++) {
            Character letter = trial.charAt(i);
            if (correctWordMap.containsKey(i)) {
                continue;
            }
            if (!freqMap.containsKey(letter)) {
                entities.add(SubmissionMapEntity
                        .builder()
                        .index(i)
                        .value(ResponseValue.WRONG)
                        .letter(trial.charAt(i))
                        .submission(submission)
                        .build());
                continue;
            }
            Integer freq = freqMap.get(letter);
            if (freq > 0) {
                entities.add(SubmissionMapEntity.builder()
                        .index(i)
                        .value(ResponseValue.PRESENT)
                        .letter(trial.charAt(i))
                        .submission(submission)
                        .build());
                freqMap.put(letter, freq - 1);
                continue;
            }
            entities.add(
                    SubmissionMapEntity
                            .builder()
                            .index(i)
                            .value(ResponseValue.WRONG)
                            .letter(trial.charAt(i))
                            .submission(submission)
                            .build());
        }

        return SubmissionAlgorithmResponse.builder().isCorrect(trial.equals(correctWord)).submissionMap(entities)
                .build();
    }

    private Map<Integer, ResponseValue> createResponseMapFromEntities(List<SubmissionMapEntity> entities) {
        Map<Integer, ResponseValue> responseMap = new HashMap<>();
        for (int i = 0; i < entities.size(); i++) {
            SubmissionMapEntity entity = entities.get(i);
            responseMap.put(entity.getIndex(), entity.getValue());
        }
        return responseMap;
    }

}
