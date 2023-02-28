export const checkWord = (currentWord, correctWord) => {
    const letterFreqMap = new Map();

    const resultMap = new Map(); // <Char,Map<resultType,index>>
    const indexResult = new Map();
    for (let i = 0; i < correctWord.length; i++) {
        const letter = correctWord[i];
        if (letterFreqMap.has(letter)) {
            letterFreqMap.set(letter, letterFreqMap.get(letter) + 1);
        } else {
            letterFreqMap.set(letter, 1);
        }
    }
    for (let i = 0; i < currentWord.length; i++) {
        const letter = currentWord[i];
        let result = "null";
        if (letter === correctWord[i]) {
            result = "correct";
            if (letterFreqMap.has(letter)) {
                letterFreqMap.set(letter, letterFreqMap.get(letter) - 1);
            }
        } else if (letterFreqMap.has(letter)) {
            result = "close";
        }
        resultMap.set(i, result);
    }
    let results = [];
    for (let i = 0; i < currentWord.length; i++) {
        const letter = currentWord[i];
        let result = resultMap.get(i);

        if (result === "correct" || result === "null") {
            results.push({
                letter,
                index: i,
                result,
            });
            continue;
        }

        if (result === "close" && letterFreqMap.get(letter) > 0) {
            results.push({
                letter,
                index: i,
                result,
            });
            letterFreqMap.set(letter, letterFreqMap.get(letter) - 1);
            continue;
        }

        results.push({
            letter,
            index: i,
            result: "null",
        });
    }
    return results;
};
