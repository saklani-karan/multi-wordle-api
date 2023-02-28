export const WordleBlocks = () => {
    return (
        <div className="flex w-full justify-center items-center space-x-1 font-semibold">
            {["W", "O", "R", "D", "L", "E"].map((text, idx) => {
                return (
                    <div className="flex w-1/5 text-white wordle border justify-center items-center sm:text-xl p-1">
                        {text}
                    </div>
                );
            })}
        </div>
    );
};
