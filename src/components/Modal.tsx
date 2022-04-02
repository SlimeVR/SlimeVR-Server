import { ReactChild } from "react";
import ReactModal from "react-modal";
import { IconButton } from "./commons/ButtonIcon";
import { CrossIcon } from "./commons/icon/CrossIcon";

export function AppModal({ children, name, ...props }: { children?: ReactChild, name: ReactChild } & ReactModal.Props) {

    return (
        <ReactModal 
            {...props} 
            overlayClassName="fixed top-0 right-0 left-0 bottom-0 bg-black bg-opacity-60 flex justify-center items-center overflow-y-auto border-none" 
            className="items-center w-full max-w-2xl h-full md:h-auto relative rounded-lg bg-primary-3 shadow-lg text-white border-none"
        >
            <div className="flex justify-between items-start p-5 rounded-t border-b-2 border-primary-1">
                <h3 className="text-xl font-semibold lg:text-2x">
                    {name}
                </h3>
                <div className="flex">
                    <IconButton icon={<CrossIcon></CrossIcon>} onClick={props.onRequestClose}></IconButton>
                </div>
            </div>
            <div className="p-6">
                {children}
            </div>
        </ReactModal>
    )

}