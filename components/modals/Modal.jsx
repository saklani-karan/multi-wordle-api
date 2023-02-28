import { Cross } from "../../icons";

export const Modal = ({ children, onClose, title }) => {
    return (
        <div className="fixed h-screen w-full z-10 flex items-center justify-center left-0 top-0">
            <div className="w-11/12 sm:w-1/2 flex-col space-y-2 rounded-lg shadow-lg p-4 bg-secondary">
                <div className="flex justify-between items-center text-whitel">
                    <p>{title}</p>
                    <button
                        className="bg-null flex w-5 h-5 rounded-full text-white"
                        onClick={onClose}
                    >
                        <Cross />
                    </button>
                </div>
                {children}
            </div>
        </div>
    );
};
