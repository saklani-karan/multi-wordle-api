import { useEffect } from "react";
import { useRouter } from "next/router";
import { client } from "../utils";
import { toast } from "react-hot-toast";
import { useUserAuthContext } from "../context/UserAuthProvider";

export const AuthLayout = ({ children }) => {
    const router = useRouter();
    const userAuthContext = useUserAuthContext();

    useEffect(() => {
        // user has been authenticated and exists in context
        if (userAuthContext.getUser()) {
            return;
        }
        // landing first time on the page
        if (window !== undefined && router.pathname !== "/auth") {
            const token = localStorage.getItem("auth_token");
            if (!token || token === "undefined") {
                router.push("/auth");
            }
            authenticateUser(token);
        }
    }, [router.pathname]);

    const authenticateUser = async (token) => {
        let user;
        try {
            user = await client(`${process.env.NEXT_PUBLIC_API_URL}/users/me`, {
                mode: "cors",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            });
        } catch (err) {
            user = null;
        }

        if (user) {
            setCredentialsOnAuthentication(token, user);
            return;
        }

        removeCredentialsOnFailure();
        router.push("/auth");
    };

    const setCredentialsOnAuthentication = (token, user) => {
        userAuthContext.saveToken(token);
        userAuthContext.saveUser(user);
        localStorage.setItem("auth_token", token);
    };

    const removeCredentialsOnFailure = () => {
        userAuthContext.resetToken();
        userAuthContext.resetUser();
        if (window !== undefined && localStorage.getItem("auth_token")) {
            localStorage.removeItem("auth_token");
        }
    };

    return <main>{children}</main>;
};
