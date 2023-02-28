import { useRouter } from "next/router";
import { useEffect, useState } from "react";
import { MainLayout } from "../../../layouts/MainLayout";
import { useUserAuthContext } from "../../../context/UserAuthProvider";
import { client } from "../../../utils";
import { Check, Pin } from "../../../icons";
import { WorldeInput } from "../../../components/form/WordleInput";
import { Keyboard } from "../../../components/form/Keyboard";
import { over } from "stompjs";
import Image from "next/image";
import SockJS from "sockjs-client";
import { Dice } from "../../../icons/Dice";
import { Avatar } from "../../../components/display/Avatar";

let stompClient = null;
export default function Game() {
    const router = useRouter();
    const authContext = useUserAuthContext();
    const [gameInfo, setGameInfo] = useState({
        game: null,
        currentRound: null,
        completedForUser: false,
        roundCompleted: false,
        gameWinner: null,
        correctWord: null,
        score: null,
    });
    const [gameUser, setGameUser] = useState(null);
    const [word, setWord] = useState(["", "", "", "", ""]);
    const [showWinnerDialog, setShowWinnerDialog] = useState(false);
    const [showCorrectDialog, setShowCorrectDialog] = useState(false);
    const [showGameWinnerDialog, setShowGameWinnerDialog] = useState(false);
    const [isConnected, setIsConnected] = useState(false);
    const [submissions, setSubmissions] = useState([]);

    const RoundWinnerDialog = ({ winnerUser, correctWord }) => {
        if (winnerUser?.id == gameUser?.id) {
            return (
                <div className="flex flex-col bg-secondary p-10 rounded-lg space-y-4 justify-center items-center">
                    <img src="/poppers-gif.gif" className="w-20 h-20" />

                    <div className="flex flex-col space-y-1">
                        <p className="text-center font-bold text-xl">
                            Congratulations
                        </p>
                        <p className="text-center font-semibold text-base">
                            You've won the round
                        </p>
                    </div>
                    <div className="flex flex-col space-y-1">
                        <p className="text-center text-base  font-light">
                            The correct word is:
                        </p>
                        <p className="text-center text-lg font-bold">
                            {correctWord?.toUpperCase()}
                        </p>
                    </div>
                </div>
            );
        }
        return (
            <div className="flex flex-col bg-secondary p-10 rounded-lg space-y-4 justify-center items-center">
                <div className="flex flex-col space-y-1 justify-center items-center">
                    <span className="w-10 h-10">
                        <Dice />
                    </span>
                    <p className="text-center font-bold text-xl">
                        Better luck next time
                    </p>
                    <p className="text-center font-semibold text-base">
                        {winnerUser?.user?.name} has won the round
                    </p>
                </div>
                <div className="flex flex-col space-y-1">
                    <p className="text-center text-base  font-light">
                        The correct word is:
                    </p>
                    <p className="text-center text-lg font-bold">
                        {correctWord?.toUpperCase()}
                    </p>
                </div>
            </div>
        );
    };

    const GameWinnerDialog = ({ gameWinner, correctWord }) => {
        if (gameWinner?.id == gameUser?.user?.id) {
            return (
                <div className="flex flex-col bg-secondary p-10 rounded-lg space-y-4 justify-center items-center">
                    <img src="/poppers-gif.gif" className="w-20 h-20" />

                    <div className="flex flex-col space-y-1">
                        <p className="text-center font-bold text-xl">
                            Congratulations
                        </p>
                        <p className="text-center font-semibold text-base">
                            You've won the game!!
                        </p>
                    </div>
                    <div className="flex flex-col space-y-1">
                        <p className="text-center text-base  font-light">
                            The correct word for the last round is:
                        </p>
                        <p className="text-center text-lg font-bold">
                            {correctWord?.toUpperCase()}
                        </p>
                    </div>
                </div>
            );
        }
        return (
            <div className="flex flex-col bg-secondary p-10 rounded-lg space-y-4 justify-center items-center">
                <div className="flex flex-col space-y-1 justify-center items-center">
                    <span className="w-10 h-10">
                        <Dice />
                    </span>
                    <p className="text-center font-bold text-xl">
                        Better luck next time
                    </p>
                    <p className="text-center font-semibold text-base">
                        {gameWinner?.name} has won the game!
                    </p>
                </div>
                <div className="flex flex-col space-y-1">
                    <p className="text-center text-base  font-light">
                        The correct word for the last is:
                    </p>
                    <p className="text-center text-lg font-bold">
                        {correctWord?.toUpperCase()}
                    </p>
                </div>
            </div>
        );
    };

    const getGame = async () => {
        const query = router.query;
        if (query?.id && authContext.getToken()) {
            try {
                const [currGameInfo, currGameUser] = await Promise.all([
                    client(
                        `${process.env.NEXT_PUBLIC_API_URL}/games/${query.id}`,
                        {
                            mode: "cors",
                            headers: {
                                "Content-Type": "application/json",
                                Authorization: `Bearer ${authContext.getToken()}`,
                            },
                        }
                    ),
                    client(
                        `${process.env.NEXT_PUBLIC_API_URL}/games/${query.id}/me`,
                        {
                            mode: "cors",
                            headers: {
                                "Content-Type": "application/json",
                                Authorization: `Bearer ${authContext.getToken()}`,
                            },
                        }
                    ),
                ]);
                setGameInfo({
                    game: currGameInfo?.game,
                    currentRound: currGameInfo?.currentRound,
                    completedForUser: currGameInfo?.completedForUser,
                    roundCompleted: currGameInfo?.roundCompleted,
                    roundWinner: currGameInfo?.roundWinner,
                    gameWinner: currGameInfo?.game?.winner,
                    gameCompleted: currGameInfo?.game?.gameCompleted,
                    score: currGameInfo?.score,
                });
                setSubmissions(currGameInfo?.submissionResponses || []);
                setGameUser(currGameUser);
            } catch (err) {}
        }
    };

    const handleNextRoundCreation = async () => {
        const query = router.query;
        try {
            await client(
                `${process.env.NEXT_PUBLIC_API_URL}/games/${query.id}/addRound`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${authContext.getToken()}`,
                    },
                }
            );
            stompClient.send(
                "/app/game/nextRound",
                {},
                JSON.stringify({ gameId: gameInfo?.game?.id })
            );
        } catch (err) {}
    };

    const connect = () => {
        let Sock = new SockJS(process.env.NEXT_PUBLIC_WS_URL);
        stompClient = over(Sock);
        stompClient.connect({}, onConnected, onError);
    };

    const onConnected = () => {
        setTimeout(function () {
            stompClient.subscribe(
                "/game/" + gameInfo?.game?.id + "/round_winner",
                onRoundWinnerMessage
            );
            stompClient.subscribe(
                "/game/" + gameInfo?.game?.id + "/new_round",
                onNewRoundCreated
            );
            stompClient.subscribe(
                "/game/" + gameInfo?.game?.id + "/game_winner",
                onGameWinnerMessage
            );
            stompClient.subscribe(
                "/game/" + gameInfo?.game?.id + "/game_completed",
                onGameCompletedMessage
            );
            stompClient.subscribe(
                "/game/" + gameInfo?.game?.id + "/round_completed",
                onRoundCompletedMessage
            );
            setIsConnected(true);
        }, 500);
    };

    const onGameCompletedMessage = (payload) => {
        getGame();
    };

    const onRoundCompletedMessage = (payload) => {
        getGame();
    };

    const onRoundWinnerMessage = (payload) => {
        let body = null;
        try {
            body = JSON.parse(payload?.body);
        } catch (err) {
            console.log(err);
        }
        if (!body) {
            return;
        }
        const { correctWord, winner } = body;
        if (correctWord && winner) {
            setGameInfo({
                ...gameInfo,
                correctWord,
                roundWinner: winner,
                roundCompleted: true,
                completedForUser: true,
            });
            setShowWinnerDialog(true);
        }
    };

    const onGameWinnerMessage = (payload) => {
        let body = null;
        try {
            body = JSON.parse(payload?.body);
        } catch (err) {
            console.log(err);
        }
        if (!body) {
            return;
        }
        const { correctWord, winner, score } = body;
        if (correctWord && winner) {
            setGameInfo({
                ...gameInfo,
                correctWord,
                roundCompleted: true,
                completedForUser: true,
                gameCompleted: true,
                gameWinner: winner,
                score,
            });
            setShowGameWinnerDialog(true);
        }
    };

    const onNewRoundCreated = (payload) => {
        let body = null;
        try {
            body = JSON.parse(payload?.body);
        } catch (err) {
            console.log(err);
        }
        if (!body) {
            return;
        }
        getGame();
    };

    const handleRoundWinner = (winnerUser) => {
        if (!winnerUser) return;
        stompClient.send(
            "/app/game/roundWinner",
            {},
            JSON.stringify({ gameId: gameInfo?.game?.id })
        );
    };

    const onError = (err) => {
        console.log(err);
    };

    const handleGameWin = (gameWinner) => {
        if (!gameWinner) {
            return;
        }
        stompClient.send(
            "/app/game/gameWinner",
            {},
            JSON.stringify({ gameId: gameInfo?.game?.id })
        );
    };

    const handleGameCompleted = (gameCompleted) => {
        if (!gameCompleted) {
            return;
        }
        stompClient.send(
            "/app/game/gameCompleted",
            {},
            JSON.stringify({ gameId: gameInfo?.game?.id })
        );
    };

    const handleRoundCompleted = (roundCompleted) => {
        if (!roundCompleted) {
            return;
        }
        stompClient.send(
            "/app/game/roundCompleted",
            {},
            JSON.stringify({ gameId: gameInfo?.game?.id })
        );
    };

    const handleKeyPress = async (letter) => {
        const query = router.query;
        if (submissions.length >= 5) {
            return;
        }
        let insertIndex = word.length;
        for (let i = 0; i < word.length; i++) {
            if (word[i] === "") {
                insertIndex = i;
                break;
            }
        }
        if (letter === "ENTER") {
            if (insertIndex == word.length) {
                try {
                    const newSubmission = await client(
                        `${process.env.NEXT_PUBLIC_API_URL}/games/${query.id}/submit`,
                        {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                                Authorization: `Bearer ${authContext.getToken()}`,
                            },
                            body: JSON.stringify({
                                trial: word?.join(""),
                            }),
                        }
                    );
                    const {
                        trial,
                        responseMap,
                        isCorrect,
                        winnerUser,
                        roundCompleted,
                        completedForUser,
                        gameCompleted,
                        gameWinner,
                        score,
                    } = newSubmission;

                    setSubmissions([
                        ...submissions,
                        { trial: trial, responseMap: responseMap },
                    ]);
                    setGameInfo({
                        ...gameInfo,
                        completedForUser: isCorrect,
                        roundCompleted,
                        completedForUser,
                        roundWinner: winnerUser,
                        gameWinner,
                        score,
                        gameCompleted: gameCompleted || gameWinner,
                    });
                    if (gameWinner) {
                        handleGameWin(gameWinner);
                        setWord(["", "", "", "", ""]);
                        return;
                    }
                    if (winnerUser) {
                        if (
                            winnerUser?.id &&
                            gameUser?.id &&
                            winnerUser?.id === gameUser?.id
                        ) {
                            setShowWinnerDialog(true);
                        }
                        handleRoundWinner(winnerUser);
                    }
                    if (gameCompleted) {
                        handleGameCompleted(gameCompleted);
                    } else if (roundCompleted) {
                        handleRoundCompleted(roundCompleted);
                    }
                    setWord(["", "", "", "", ""]);
                } catch (err) {
                    console.log(err);
                }
            }
        }
        if (letter === "BACK") {
            if (insertIndex <= 0) {
                return;
            }
            const prevWord = [...word];
            prevWord[insertIndex - 1] = "";
            setWord(prevWord);
            return;
        }
        if (insertIndex >= word.length) {
            return;
        }
        const prevWord = [...word];
        prevWord[insertIndex] = letter;

        setWord(prevWord);
    };

    const handleReturnToMainMenu = () => {
        router.push("/");
    };

    useEffect(() => {
        if (!isConnected) {
            getGame();
        }
    }, [router.query, authContext.getToken()]);

    useEffect(() => {
        if (gameInfo && gameUser && !isConnected) {
            connect();
        }
    }, [gameInfo, gameUser]);

    console.log(gameInfo);

    return (
        <MainLayout>
            {showWinnerDialog && gameInfo?.roundWinner ? (
                <div
                    className="fixed h-full z-10 w-full justify-center flex items-center bg-black bg-opacity-50"
                    onClick={() => {
                        setShowWinnerDialog(false);
                    }}
                >
                    <RoundWinnerDialog
                        winnerUser={gameInfo.roundWinner}
                        correctWord={gameInfo.correctWord}
                    />
                </div>
            ) : null}
            {showGameWinnerDialog && gameInfo?.gameWinner ? (
                <div
                    className="fixed h-full z-10 w-full justify-center flex items-center bg-black bg-opacity-50"
                    onClick={() => {
                        setShowGameWinnerDialog(false);
                    }}
                >
                    <GameWinnerDialog
                        gameWinner={gameInfo.gameWinner}
                        correctWord={gameInfo.correctWord}
                    />
                </div>
            ) : null}

            {gameInfo.game ? (
                gameInfo?.gameCompleted ? (
                    <div className="flex flex-col space-y-2 justify-center items-center">
                        <div className="flex flex-col p-4 justify-center items-center space-y-0">
                            <img src="/check.gif" className="w-20 h-20" />
                            <p className="text-base font-bold text-center text-lg">
                                GAME COMPLETED
                            </p>
                        </div>
                        {gameInfo?.gameWinner ? (
                            <div className="flex flex-col p-4 py-2 justify-center items-center space-y-2">
                                <p className="text-lg font-bold">Winner</p>
                                <div className="flex flex-col justify-center items-center space-y-2">
                                    <Avatar
                                        user={gameInfo?.gameWinner}
                                        large={true}
                                    />
                                    <p className="text-base font-bold">
                                        {gameInfo?.gameWinner?.name}
                                    </p>
                                </div>
                            </div>
                        ) : (
                            <div className="flex flex-col p-4 py-2 justify-center items-center space-y-2">
                                <p className="text-lg font-bold">
                                    {" "}
                                    The game has been drawn
                                </p>
                            </div>
                        )}
                        {gameInfo?.score ? (
                            <div className="flex flex-col justify-center item-center p-4">
                                <p className="text-lg font-bold text-center">
                                    Score Board
                                </p>
                                <table className="flex-col space-y-2">
                                    <th className="flex space-x-4 justify-center items-center justify-between">
                                        <td> USER </td>
                                        <td> SCORE </td>
                                    </th>
                                    {gameInfo?.score?.map((row) => {
                                        return (
                                            <tr className="flex space-x-4 justify-center items-center">
                                                <td className="flex space-x-2 items-center">
                                                    <img
                                                        src={row?.user?.picture}
                                                        className="w-7 h-7 rounded-sm"
                                                    />
                                                    <p className="text-sm font-bold">
                                                        {row?.user?.name}
                                                    </p>
                                                </td>
                                                <td className="flex space-x-2">
                                                    <p className="text-sm font-bold">
                                                        {row?.score}
                                                    </p>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </table>
                            </div>
                        ) : null}
                        <button
                            className="p-4 bg-secondary rounded-lg font-bold hover:bg-correct"
                            onClick={handleReturnToMainMenu}
                        >
                            Exit To Main Menu
                        </button>
                    </div>
                ) : (
                    <div className="flex flex-col space-y-4 relative">
                        <div className="flex justify-center items-center space-x-2">
                            <span className="flex w-3 h-3 justify-center items-center">
                                <Pin />
                            </span>
                            <span className="text-base font-bold">
                                Round {gameInfo?.currentRound}
                            </span>
                        </div>
                        {submissions.map((submission) => {
                            return (
                                <div className="flex flex-col">
                                    <WorldeInput
                                        values={submission?.trial?.split("")}
                                        submission={submission.responseMap}
                                    />
                                </div>
                            );
                        })}
                        {5 - submissions?.length ? (
                            <div className="flex flex-col">
                                <WorldeInput values={word} />
                            </div>
                        ) : null}

                        {Array.from(
                            { length: 5 - 1 - submissions.length },
                            (_, i) => ["", "", "", "", ""]
                        ).map((entry) => {
                            return (
                                <div className="flex flex-col">
                                    <WorldeInput values={entry} />
                                </div>
                            );
                        })}
                        {gameInfo?.roundCompleted ? (
                            <div className="flex flex-col items-center justify-center space-y-2">
                                <div className="flex-col flex items-center justify-center">
                                    <span className="w-6 h-6 text-green-500">
                                        <Check />
                                    </span>
                                    <p className="text-lg font-bold">
                                        Round has been completed
                                    </p>
                                </div>
                                {gameInfo?.roundWinner ? (
                                    <div className="flex-col flex items-center justify-center space-y-3">
                                        <p className="text-lg font-semibold text-center">
                                            Round has been won by:
                                        </p>
                                        <div className="flex flex-col justify-center items-center">
                                            <Avatar
                                                user={
                                                    gameInfo?.roundWinner?.user
                                                }
                                            />
                                            <p className="text-base font-bold">
                                                {
                                                    gameInfo?.roundWinner?.user
                                                        ?.name
                                                }
                                            </p>
                                        </div>
                                    </div>
                                ) : (
                                    <div className="flex-col flex">
                                        <p className="text-lg font-bold">
                                            The round has been drawn!
                                        </p>
                                    </div>
                                )}
                                <div className="flex justify-center items-center">
                                    <button
                                        className="rounded-lg p-4 bg-correct font-bold text-sm"
                                        onClick={handleNextRoundCreation}
                                    >
                                        Next Round
                                    </button>
                                </div>
                            </div>
                        ) : gameInfo?.completedForUser ? (
                            <div className="flex flex-col p-4 justify-center items-center space-y-4">
                                <span className="w-6 h-6 text-green-500">
                                    <Check />
                                </span>
                                <p className="text-base font-bold text-center">
                                    You have completed the round.
                                </p>
                                <p className="text-sm font-semibold text-center">
                                    Please wait for other users to finish the
                                    round.
                                </p>
                            </div>
                        ) : (
                            <div className="flex items-center justify-center fixed bottom-4 w-full">
                                <Keyboard handleKeyPress={handleKeyPress} />
                            </div>
                        )}
                    </div>
                )
            ) : null}
        </MainLayout>
    );
}
