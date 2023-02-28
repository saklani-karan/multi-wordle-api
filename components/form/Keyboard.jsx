import { useState } from "react";

const getDefaultRowFromString = (string) => {
    return string?.split("")?.map((char) => {
        return {
            letter: char.toUpperCase(),
            color: 0,
        };
    });
};

export const Keyboard = ({ handleKeyPress }) => {
    const [row1, setRow1] = useState(getDefaultRowFromString("qwertyuiop"));
    const [row2, setRow2] = useState(getDefaultRowFromString("asdfghjkl"));
    const [row3, setRow3] = useState([
        {
            letter: "ENTER",
            color: 0,
            flex: true,
        },
        ...getDefaultRowFromString("zxcvbnm"),
        {
            letter: "BACK",
            color: 0,
            flex: true,
        },
    ]);

    const Key = ({ letter, color, flex }) => {
        return (
            <div
                className={`flex justify-center items-center ${
                    color == 0
                        ? "bg-secondary"
                        : color == -1
                        ? "bg-gray-700 text-gray-600"
                        : color == 1
                        ? "bg-correct"
                        : "bg-close"
                } rounded-sm font-bold ${
                    flex
                        ? "flex justify-center text-sm px-1"
                        : "w-6 h-6 xs:w-8 xs:h-8 md:w-12 md:h-12 text-md xs:text-lg"
                }`}
                onClick={() => {
                    handleKeyPress(letter);
                }}
            >
                {letter}
            </div>
        );
    };

    return (
        <div className="flex-col flex items-center justify-center space-y-1">
            <div className="flex justify-center space-x-1">
                {row1.map(({ letter, color }) => {
                    return <Key letter={letter} color={color} />;
                })}
            </div>
            <div className="flex justify-center space-x-1">
                {row2.map(({ letter, color }) => {
                    return <Key letter={letter} color={color} />;
                })}
            </div>
            <div className="flex justify-center space-x-1">
                {row3.map(({ letter, color, flex }) => {
                    return <Key letter={letter} color={color} flex={flex} />;
                })}
            </div>
        </div>
    );
};
