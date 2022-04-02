import { createContext, MutableRefObject, useContext, useEffect, useRef, useState } from "react";

import { AcknowledgementT, ApplicationType, ConnectionRequestT, InboundPacketT, InboundUnion, OutboundPacket, OutboundPacketT, OutboundUnion } from 'slimevr-protocol/dist/server'

import { Builder, ByteBuffer } from 'flatbuffers'
import { useInterval } from "./timeout";

export interface WebSocketApi {
    isConnected: boolean,
    eventlistenerRef: MutableRefObject<EventTarget>,
    usePacket: <T>(type: OutboundUnion, callback: (packet: T) => void) => void
    sendPacket: (type: InboundUnion, data: InboundPacketType, acknowledgeMe?: boolean) => Promise<boolean>
}


export const WebSocketApiContext = createContext<WebSocketApi>(undefined as any);

export type InboundPacketType = InboundPacketT['packet'];
export type OutboundPacketType = OutboundPacketT['packet'];

export function useProvideWebsocketApi(): WebSocketApi {
    const packetCounterRef = useRef<number>(0);
    const toAcknoledgePacketsRef = useRef<{ [key: number]: () => void }>([]);
    const webSocketRef = useRef<WebSocket | null>(null);
    const eventlistenerRef = useRef<EventTarget>(new EventTarget());
    const [isConnected, setConnected] = useState(false);


    useInterval(() => {
        if (webSocketRef.current && !isConnected) {
            disconnect();
            connect();
            console.log('Try reconnecting');
        }
    }, 3000);

    const onConnected = (event: Event) => {
        if (!webSocketRef.current) return ;

        setConnected(true);
        const conn = new ConnectionRequestT();
        conn.applicationType = ApplicationType.UI;

        sendPacket(InboundUnion.ConnectionRequest, conn);
    }

    const onConnectionClose = (event: Event) => {
        setConnected(false);

        packetCounterRef.current = 0;
        toAcknoledgePacketsRef.current = [];
    }

    const onMessage = async (event: { data: Blob }) => {
        if (!event.data.arrayBuffer)
            return ;
        const buffer = await event.data.arrayBuffer();

        const fbb = new ByteBuffer(new Uint8Array(buffer));

        const outbountPacket = OutboundPacket.getRootAsOutboundPacket(fbb).unpack();
        eventlistenerRef.current?.dispatchEvent(new CustomEvent(OutboundUnion[outbountPacket.packetType], { detail: outbountPacket }))

        if (outbountPacket.acknowledgeMe && webSocketRef.current) {
            const fbb = new Builder();

            const acknowledgement = new AcknowledgementT();
            acknowledgement.packetId = outbountPacket.packetCounter;
            fbb.finish(acknowledgement.pack(fbb));
            webSocketRef.current.send(fbb.asUint8Array());
        }

        if (outbountPacket.packetType === OutboundUnion.slimevr_protocol_misc_Acknowledgement) {
            const acknowledgement = outbountPacket.packet as AcknowledgementT;
            const acknoledgePromise = toAcknoledgePacketsRef.current[acknowledgement.packetId];
            if (!acknoledgePromise)
                return;
            delete toAcknoledgePacketsRef.current[acknowledgement.packetId];
            acknoledgePromise()
        }
    }

    const sendPacket = async (type: InboundUnion, data: InboundPacketType, acknowledgeMe = false): Promise<boolean> => {
        if (!webSocketRef.current)
            throw new Error('No connection');

        const fbb = new Builder(1);

        const inbound = new InboundPacketT();
        inbound.acknowledgeMe = acknowledgeMe;
        inbound.packetCounter = packetCounterRef.current;
        inbound.packet = data;
        inbound.packetType = type;

        fbb.finish(inbound.pack(fbb));
        webSocketRef.current.send(fbb.asUint8Array());

        if (acknowledgeMe) {
            return await new Promise((resolve, reject) => {
                // TODO implement retry
                const timeoutId = setTimeout(() => {
                    reject(false);
                }, 3000)

                const acknoledged = () => {
                    clearTimeout(timeoutId);
                    resolve(true);
                }

                toAcknoledgePacketsRef.current[inbound.packetCounter] = acknoledged;
            })
        }

        packetCounterRef.current++;

        return true;
    }


    const connect = () =>  {
        webSocketRef.current = new WebSocket('ws://localhost:21110');
        

        // Connection opened
        webSocketRef.current.addEventListener('open', onConnected);
        webSocketRef.current.addEventListener('close', onConnectionClose);
        webSocketRef.current.addEventListener('message', onMessage);
    }

    const disconnect = () => {
        if (!webSocketRef.current) return ;

        webSocketRef.current.removeEventListener('open', onConnected);
        webSocketRef.current.removeEventListener('close', onConnectionClose);
        webSocketRef.current.removeEventListener('message', onMessage);
    }

    useEffect(() => {
        connect();
        return () => {
            disconnect();
        }
    }, [])

    return {
        isConnected,
        eventlistenerRef,
        usePacket: <T>(type: OutboundUnion, callback: (packet: T) => void) => {
            const onEvent = (event: CustomEventInit) => {
                callback(event.detail.packet)
            }

            useEffect(() => {
                eventlistenerRef.current.addEventListener(OutboundUnion[type], onEvent)
                return () => {
                    eventlistenerRef.current.removeEventListener(OutboundUnion[type], onEvent)
                }
            }, [])
        },
        sendPacket
    }
}


export function useWebsocketAPI(): WebSocketApi {
    const context = useContext<WebSocketApi>(WebSocketApiContext);
    if (!context) {
        throw new Error('useWebsocketAPI must be within a WebSocketApi Provider')
    }
    return context;
}