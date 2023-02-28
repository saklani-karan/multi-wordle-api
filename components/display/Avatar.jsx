import { User } from "../../icons";

export const Avatar = ({ user, large }) => {
    if (user?.picture) {
        return (
            <img
                src={user.picture}
                className={`${large ? "w-10 h-10" : "w-6 h-6"} rounded-full`}
            />
        );
    }

    return (
        <span className="flex w-4 h-4 sm:w-5 sm:h-5 text-white justify-end">
            <User />
        </span>
    );
};
