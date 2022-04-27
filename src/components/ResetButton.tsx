import { useRef, useState } from "react";
import { ResetRequestT, ResetType, RpcMessage } from "solarxr-protocol";
import { useWebsocketAPI } from "../hooks/websocket-api";
import { BigButton } from "./commons/BigButton";
import { QuickResetIcon, ResetIcon } from "./commons/icon/ResetIcon";



export function ResetButton({ type }: { type: ResetType }) {

    const timerid = useRef<NodeJS.Timer | null>(null);
    const [reseting, setReseting] = useState(false);
    const [timer, setTimer] = useState(0);
    const { sendRPCPacket } = useWebsocketAPI();

    const reset = () => {
        const req = new ResetRequestT();
        req.resetType = type;
        setReseting(true);
        if (type !== ResetType.Quick) {
            if (timerid.current)
                clearInterval(timerid.current);
            timerid.current = setInterval(() => {
                setTimer((timer) => {
                    if (timer + 1 === 3) {
                        if (timerid.current)
                            clearInterval(timerid.current);
                            sendRPCPacket(RpcMessage.ResetRequest, req)
                            setTimer(0); 
                            setReseting(false) 
                    }
                    return timer + 1;
                });
            }, 1000);
        } else {
            sendRPCPacket(RpcMessage.ResetRequest, req)
            setReseting(false);
        }
    }

    return (
        <BigButton 
            text={!reseting ? type === ResetType.Quick ? "Quick Reset" : "Reset" : `${3 - timer}`} icon={type === ResetType.Quick ? <QuickResetIcon /> : <ResetIcon/>} 
            onClick={reset} 
            disabled={reseting}>
        </BigButton>
    )


}