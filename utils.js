export const client = async (url, options) => {
    return fetch(url, options).then((response) => {
        console.log(response);
        if (!response || !response.ok) {
            response?.json().then((err) => {
                throw err;
            });
            throw new Error();
        }
        return response.json();
    });
};

export const shuffleWord = (str) =>
    [...str].sort(() => Math.random() - 0.5).join("");
