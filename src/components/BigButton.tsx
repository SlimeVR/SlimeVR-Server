import { ReactChild } from "react";



export function BigButton({ text, icon }: { text: string, icon: ReactChild }) {

    return (
        <div className="flex flex-col rounded-md hover:bg-primary-4 bg-primary-3 py-10 gap-8 cursor-pointer">
            <div className="flex justify-around">{icon}</div>
            <div className="flex justify-around text-2xl text-white">{text}</div>
        </div>
    )

}