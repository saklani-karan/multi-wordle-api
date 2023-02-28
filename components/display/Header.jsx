import { useState } from "react";
import { Info, User } from "../../icons";
import { Dev } from "../../icons/Dev";
import { Modal } from "../modals/Modal";
import { BlocksWithChecks } from "../wordle/BlocksWithChecks";
import { WordleBlocks } from "./WordleBlocks";
import { WordleInfo } from "./WordleInfo";
import { useUserAuthContext } from "../../context/UserAuthProvider";
import { Avatar } from "./Avatar";

export const Header = () => {
    const [infoVisible, setInfoVisible] = useState(false);
    const authContext = useUserAuthContext();
    const openInfoModal = () => {
        setInfoVisible(true);
    };
    const closeInfoModal = () => {
        setInfoVisible(false);
    };
    return (
        <div className="flex w-full p-2 px-4 sm:p-4 sm:px-8 items-center justify-between">
            {/* info */}
            <div className="w-1/4 flex justify-start">
                {infoVisible ? (
                    <Modal title={"Learn more"} onClose={closeInfoModal}>
                        <WordleInfo />
                    </Modal>
                ) : (
                    <span
                        className="flex w-4 h-4 sm:w-5 sm:h-5 text-white justify-start"
                        onClick={openInfoModal}
                    >
                        <Info />
                    </span>
                )}
            </div>
            {/* logo */}
            <div className="w-1/2 sm:w-1/4">
                <WordleBlocks />
            </div>
            {/* profile */}
            <div className="w-1/4 flex justify-end space-x-3 sm:space-x-5 items-center">
                <span className="flex w-4 h-4 sm:w-5 sm:h-5 text-white justify-end">
                    <Dev />
                </span>
                <Avatar user={authContext.getUser()} />
            </div>
        </div>
    );
};
