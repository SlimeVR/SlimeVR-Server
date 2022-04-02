import classNames from "classnames";
import React, { ReactChild } from "react";

export function BigButton({ text, icon, disabled, onClick, ...props }: { text: string, icon: ReactChild } & React.AllHTMLAttributes<HTMLButtonElement>) {
    return (
        <button disabled={disabled} onClick={onClick} {...props} type="button" className={classNames("flex w-full flex-col rounded-md hover:bg-primary-5 bg-primary-4 py-10 gap-5 cursor-pointer items-center", { 'bg-gray-500 hover:bg-gray-500 cursor-not-allowed': disabled}, props.className)}>
            <div className="flex justify-around">{icon}</div>
            <div className="flex justify-around text-2xl text-white font-bold">{text}</div>
        </button>
    )
}