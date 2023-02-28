import { useState } from "react";

export const BlockOptions = ({
    options,
    defaultValue,
    label,
    handleFormEntry,
}) => {
    const [selected, setSelected] = useState(
        defaultValue || options ? options[0]?.key : null
    );

    const handleSelect = (option) => {
        const { key, value } = option;
        handleFormEntry(label, key);
        setSelected(key);
    };

    return (
        <div className="flex space-x-2 justify-center items-center">
            {options?.map(({ key, value }) => {
                return (
                    <div
                        className={`flex justify-center items-center p-4 px-6 rounded-lg cursor-pointer ${
                            key === selected
                                ? "bg-correct font-semibold"
                                : "bg-secondary"
                        }`}
                        onClick={() => {
                            handleSelect({ key, value });
                        }}
                    >
                        {value}
                    </div>
                );
            })}
        </div>
    );
};
