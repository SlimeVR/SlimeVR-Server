import { createContext, MutableRefObject, useContext, useEffect, useRef, useState } from "react";

import { ApplicationType, HandshakeRequestT, InboundPacketT, InboundUnion, OutboundPacket, OutboundUnion } from 'slimevr-protocol/dist/server'

import { Builder, ByteBuffer } from 'flatbuffers'

export interface WebSocketApi {
    isConnected: boolean,
    eventlistenerRef: MutableRefObject<EventTarget>,
    usePacket: <T>(type: OutboundUnion, callback: (packet: T) => void) => void
}


export const WebSocketApiContext = createContext<WebSocketApi>(undefined as any);


export function useProvideWebsocketApi(): WebSocketApi {
    const webSocketRef = useRef<WebSocket | null>(null);
    const eventlistenerRef = useRef<EventTarget>(new EventTarget());
    const [isConnected, setConnected] = useState(false);



    const onConnected = (event: Event) => {
        if (!webSocketRef.current) return ;

        setConnected(true);

        let fbb = new Builder(1);

        const hand = new HandshakeRequestT();
        hand.applicationType = ApplicationType.UI;


        const inbound = new InboundPacketT();
        inbound.acknowledgeMe = true;
        inbound.packet = hand;
        inbound.packetType = InboundUnion.HandshakeRequest

        fbb.finish(inbound.pack(fbb));

        webSocketRef.current.send(fbb.asUint8Array());
    }

    const onConnectionClose = (event: Event) => {
        setConnected(false);
    }

    const onMessage = async (event: { data: Blob }) => {

        const buffer = await event.data.arrayBuffer();

        const fbb = new ByteBuffer(new Uint8Array(buffer));

        const outbountPacket = OutboundPacket.getRootAsOutboundPacket(fbb).unpack();

        eventlistenerRef.current?.dispatchEvent(new CustomEvent(OutboundUnion[outbountPacket.packetType], { detail: outbountPacket }))
    }

    useEffect(() => {
        webSocketRef.current = new WebSocket('ws://localhost:21110');

        // Connection opened
        webSocketRef.current.addEventListener('open', onConnected);
        webSocketRef.current.addEventListener('close', onConnectionClose);
        webSocketRef.current.addEventListener('message', onMessage);

        return () => {
            if (!webSocketRef.current) return ;

            webSocketRef.current.removeEventListener('open', onConnected);
            webSocketRef.current.removeEventListener('close', onConnectionClose);
            webSocketRef.current.removeEventListener('message', onMessage);
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
        }
    }
}


export function useWebsocketAPI(): WebSocketApi {
    const context = useContext<WebSocketApi>(WebSocketApiContext);
    if (!context) {
        throw new Error('useWebsocketAPI must be within a WebSocketApi Provider')
    }
    return context;
}