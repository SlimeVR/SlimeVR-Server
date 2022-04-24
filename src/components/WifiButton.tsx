import { ReactChild, useEffect, useRef, useState } from "react";
import { useWebsocketAPI } from "../hooks/websocket-api";
import { Button } from "./commons/Button";
import { AppModal } from "./Modal";
import { CloseSerialRequestT, OpenSerialRequestT, RpcMessage, SerialUpdateResponseT, SetWifiRequestT } from "solarxr-protocol"
import { Input } from "./commons/Input";
import { useForm } from "react-hook-form";

export interface WifiForm {
    ssid: string;
    password: string;
}

export function WIFIButton({ children }: { children: ReactChild }) {

    const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
    const consoleRef = useRef<HTMLPreElement>(null);
    const [consoleContent, setConsole] = useState("");
    const [isSerialOpen, setSerialOpen] = useState(false);
    const { register, reset, handleSubmit } = useForm<WifiForm>({ defaultValues: {} });

    const [isOpen, setOpen] = useState(false);
    
    const openModal = () => {
        setOpen(true);
        setConsole('');
        setSerialOpen(false);
        sendRPCPacket(RpcMessage.OpenSerialRequest, new OpenSerialRequestT())
    }

    const closeModal = () => {
        setOpen(false);
        reset({ ssid: '', password: '' })
        sendRPCPacket(RpcMessage.CloseSerialRequest, new CloseSerialRequestT());
    }

    useRPCPacket(RpcMessage.SerialUpdateResponse, (data: SerialUpdateResponseT) => {
        if (data.closed && isOpen) {
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
        <>
            <Button variant="primary" onClick={openModal}>{children}</Button>
            <AppModal 
                isOpen={isOpen} 
                name={<>Wifi Settings</>} 
                onRequestClose={closeModal}
            >
                <form className="flex flex-col gap-2" onSubmit={handleSubmit(sendWifiCredentials)}>
                    <pre ref={consoleRef} className="flex w-full bg-gray-800 h-80 rounded-lg overflow-y-auto select-text">
                        {isSerialOpen ? consoleContent : 'Connection to serial lost, Reconnecting...'}
                    </pre>
                    <div className="flex flex-col gap-2" >
                        <Input {...register('ssid', { required: true })} type="text" placeholder="SSID"></Input>
                        <Input {...register('password', { required: true })} type="password" placeholder="Password"></Input>
                        <Button variant="primary" type="submit">Send</Button>
                    </div>
                </form>
            </AppModal>
        </>
    )

}