import { createContext, useContext, useState } from "react";

const UserContext = createContext();

export function UserAuthWrapper({ children }) {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(null);

    const saveUser = (newUser) => {
        setUser(newUser);
    };

    const getUser = () => {
        return user;
    };

    const saveToken = (newToken) => {
        setToken(newToken);
    };

    const getToken = () => {
        return token;
    };

    const state = {
        saveUser,
        getUser,
        saveToken,
        getToken,
        resetToken: () => {
            setToken(null);
        },
        resetUser: () => {
            setUser(null);
        },
    };

    return (
        <UserContext.Provider value={state}>{children}</UserContext.Provider>
    );
}

export function useUserAuthContext() {
    const state = useContext(UserContext);
    return state;
}
