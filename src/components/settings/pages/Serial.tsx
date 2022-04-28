import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { CloseSerialRequestT, OpenSerialRequestT, RpcMessage, SerialUpdateResponseT, SetWifiRequestT } from "solarxr-protocol";
import { useLayout } from "../../../hooks/layout";
import { useWebsocketAPI } from "../../../hooks/websocket-api";
import { Button } from "../../commons/Button";
import { Input } from "../../commons/Input";

export interface WifiForm {
    ssid: string;
    password: string;
}

export function Serial() {

    const { layoutHeight, layoutWidth, ref: consoleRef } = useLayout<HTMLDivElement>();

    const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
    // const consoleRef = useRef<HTMLPreElement>(null);
    const [consoleContent, setConsole] = useState("");
    const [isSerialOpen, setSerialOpen] = useState(false);
    const { register, handleSubmit } = useForm<WifiForm>({ defaultValues: {} });


    useEffect(() => {
        sendRPCPacket(RpcMessage.OpenSerialRequest, new OpenSerialRequestT())
        return () => {
            sendRPCPacket(RpcMessage.CloseSerialRequest, new CloseSerialRequestT());
        }
    }, [])


    useRPCPacket(RpcMessage.SerialUpdateResponse, (data: SerialUpdateResponseT) => {
        if (data.closed) {
            setSerialOpen(false)
            setTimeout(() => {
                sendRPCPacket(RpcMessage.OpenSerialRequest, new OpenSerialRequestT())
            }, 1000)
        }

        if (!data.closed) {
            setSerialOpen(true);
        }

        if (data.log && consoleRef.current) {
            setConsole((console) => console + data.log)
        }
    })

    useEffect(() => {
        if (consoleRef.current)
            consoleRef.current.scrollTo({ top: consoleRef.current.scrollHeight })
    }, [consoleContent])


    useEffect(() => {
        const id = setInterval(() => {
            if (!isSerialOpen)
                sendRPCPacket(RpcMessage.OpenSerialRequest, new OpenSerialRequestT())
            else
                clearInterval(id);
        }, 1000);

        return () => {
            clearInterval(id);
        }
    }, [isSerialOpen, sendRPCPacket])

    const sendWifiCredentials = (value: WifiForm) => {
        const wifi = new SetWifiRequestT();

        wifi.password = value.password;
        wifi.ssid = value.ssid;
        
        sendRPCPacket(RpcMessage.SetWifiRequest, wifi)
    }


    return (
        <form className="flex flex-col h-full gap-2" onSubmit={handleSubmit(sendWifiCredentials)}>
            <div ref={consoleRef}  style={{ height: layoutHeight, width: layoutWidth }} className="overflow-x-auto overflow-y-auto flex select-text pl-3">
                <pre>
                    {isSerialOpen ? consoleContent : 'Connection to serial lost, Reconnecting...'}
                </pre>
            </div>
            <div className="flex flex-col gap-2 p-3" >
                <Input {...register('ssid', { required: true })} type="text" placeholder="SSID"></Input>
                <Input {...register('password', { required: true })} type="password" placeholder="Password"></Input>
                <Button variant="primary" type="submit">Send</Button>
            </div>
        </form>
    )

}