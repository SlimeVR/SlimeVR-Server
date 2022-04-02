import { useEffect, useRef, useState } from "react";
import { InboundUnion, ResetRequestT } from "slimevr-protocol/dist/server";
import { useWebsocketAPI } from "./websocket-api";



export function useReset() {
    const timerid = useRef<NodeJS.Timer | null>(null);
    const [reseting, setReseting] = useState(false);
    const [timer, setTimer] = useState(0);
    const { sendPacket } = useWebsocketAPI();

    const reset = (quick: boolean) => {
        const req = new ResetRequestT();
        req.quick = quick;
        setReseting(true);
        if (!quick) {
            if (timerid.current)
                clearInterval(timerid.current);
            timerid.current = setInterval(() => {
                setTimer((timer) => {
                    if (timer + 1 === 3) {
                        if (timerid.current)
                            clearInterval(timerid.current);
                        sendPacket(InboundUnion.ResetRequest, req, false)
                            .then(() => { 
                                setTimer(0); 
                                setReseting(false) 
                            })
                    }
                    return timer + 1;
                });
            }, 1000);
        } else {
            sendPacket(InboundUnion.ResetRequest, req, false)
            setReseting(false);
        }
    }

    return {
        reset,
        timer,
        reseting
    }
}