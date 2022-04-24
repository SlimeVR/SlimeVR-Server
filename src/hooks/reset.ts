import { useRef, useState } from "react";
import { ResetRequestT, ResetType, RpcMessage } from "solarxr-protocol";
import { useWebsocketAPI } from "./websocket-api";



export function useReset() {
    const timerid = useRef<NodeJS.Timer | null>(null);
    const [reseting, setReseting] = useState(false);
    const [timer, setTimer] = useState(0);
    const { sendRPCPacket } = useWebsocketAPI();

    const reset = (type: ResetType) => {
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

    return {
        reset,
        timer,
        reseting
    }
}