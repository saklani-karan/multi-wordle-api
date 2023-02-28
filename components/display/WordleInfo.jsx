import { shuffleWord } from "../../utils";
import { BlocksWithChecks } from "../wordle/BlocksWithChecks";

export const WordleInfo = () => {
    const colorArray = ["correct", "close", "null"];

    const getBackgroundColor = () => {
        const randInt = Math.floor(3 * Math.random());
        return `bg-${colorArray[randInt]}`;
    };

    return (
        <div className="flex-col items-center justify-center w-full space-y-4">
            <div className="flex-col w-full  space-y-2">
                <p>
                    The same game, same rules and nearly the same user
                    experience of the New York Time's Wordle
                </p>
                <BlocksWithChecks
                    correctWord={"RULES"}
                    currentWord={shuffleWord("RULES")}
                />
            </div>
            <div className="flex-col w-full  space-y-2">
                <p>
                    But now you can also play with other players, simply create
                    a room and send the room link to your friends.
                </p>
                <BlocksWithChecks
                    correctWord={"MULTI"}
                    currentWord={shuffleWord("MULTI")}
                />
            </div>
            <div className="flex-col w-full  space-y-2">
                <p>
                    After each game, send friend requests or accept received
                    friend requests from players you have played with to track
                    progress and compete on the leaderboard.
                </p>
                <BlocksWithChecks
                    correctWord={"FRIEND"}
                    currentWord={shuffleWord("FRIEND")}
                />
            </div>
        </div>
    );
};
