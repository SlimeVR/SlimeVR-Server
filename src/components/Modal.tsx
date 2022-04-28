import { ReactChild } from "react";
import ReactModal from "react-modal";
import { IconButton } from "./commons/ButtonIcon";
import { CrossIcon } from "./commons/icon/CrossIcon";

export function AppModal({ children, name, ...props }: { children?: ReactChild, name: ReactChild } & ReactModal.Props) {

    return (
        <ReactModal 
            {...props} 
            overlayClassName="fixed top-0 right-0 left-0 bottom-0 flex bg-purple-gray-900 bg-opacity-60 justify-center items-center overflow-y-auto border-none" 
            className="items-center w-full max-w-2xl h-full md:h-auto bg-purple-gray-700 relative rounded-lg shadow-lg border-none"
        >
            <div className="flex justify-between items-start p-5 rounded-t border-b-2 border-primary-1">
                <h3 className="text-extra-emphasised">
                    {name}
                </h3>
                <div className="flex">
                    <IconButton icon={<CrossIcon></CrossIcon>} className="fill-purple-gray-200" onClick={props.onRequestClose}></IconButton>
                </div>
            </div>
            <div className="p-6">
                {children}
            </div>
        </ReactModal>
    )

}