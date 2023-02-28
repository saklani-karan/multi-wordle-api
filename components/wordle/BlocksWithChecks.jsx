import { useEffect, useState } from "react";
import { checkWord } from "./checkWord";

export const BlocksWithChecks = ({ currentWord, correctWord }) => {
    const [results, setResults] = useState(null);

    useEffect(() => {
        const result = checkWord(currentWord, correctWord);
        setResults(result);
    }, []);
    if (!results) {
        return <div></div>;
    }
    return (
        <div className="flex justify-center items-center space-x-1 font-semibold">
            {currentWord
                .toUpperCase()
                .split("")
                .map((text, idx) => {
                    return (
                        <div
                            className={`flex w-10 h-10 text-white border justify-center items-center sm:text-xl p-1 ${
                                results
                                    ? `wordle-${results[idx].result}`
                                    : "bg-null"
                            }`}
                        >
                            {text}
                        </div>
                    );
                })}
        </div>
    );
};
