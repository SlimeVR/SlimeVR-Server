import { createContext, useContext, useEffect, useRef, useState } from 'react';

import {
  DataFeedMessage,
  DataFeedMessageHeaderT,
  MessageBundle,
  MessageBundleT,
  PubSubHeaderT,
  PubSubUnion,
  RpcMessage,
  RpcMessageHeaderT,
} from 'solarxr-protocol';

import { Builder, ByteBuffer } from 'flatbuffers';
import { useInterval } from './timeout';

export interface WebSocketApi {
  isConnected: boolean;
  isFirstConnection: boolean;
  useRPCPacket: <T>(type: RpcMessage, callback: (packet: T) => void) => void;
  useDataFeedPacket: <T>(
    type: DataFeedMessage,
    callback: (packet: T) => void
  ) => void;
  sendRPCPacket: (type: RpcMessage, data: RPCPacketType) => void;
  sendDataFeedPacket: (type: DataFeedMessage, data: DataFeedPacketType) => void;
  usePubSubPacket: <T>(
    type: PubSubUnion,
    callback: (packet: T) => void
  ) => void;
  sendPubSubPacket: (type: PubSubUnion, data: PubSubPacketType) => void;
}

export const WebSocketApiContext = createContext<WebSocketApi>(
  undefined as never
);

export type RPCPacketType = RpcMessageHeaderT['message'];
export type PubSubPacketType = PubSubHeaderT['u'];
export type DataFeedPacketType = DataFeedMessageHeaderT['message'];
// export type OutboundPacketType = OutboundPacketT['packet'];

export function useProvideWebsocketApi(): WebSocketApi {
  const rpcPacketCounterRef = useRef<number>(0);
  const webSocketRef = useRef<WebSocket | null>(null);
  const rpclistenerRef = useRef<EventTarget>(new EventTarget());
  const pubsublistenerRef = useRef<EventTarget>(new EventTarget());
  const datafeedlistenerRef = useRef<EventTarget>(new EventTarget());
  const [isFirstConnection, setFirstConnection] = useState(true);
  const [isConnected, setConnected] = useState(false);

  useInterval(() => {
    if (webSocketRef.current && !isConnected) {
      disconnect();
      connect();
      console.log('Try reconnecting');
    }
  }, 3000);

  const onConnected = () => {
    if (!webSocketRef.current) return;
    setFirstConnection(false);
    setConnected(true);
  };

  const onConnectionClose = () => {
    setConnected(false);
    rpcPacketCounterRef.current = 0;
  };

  const onMessage = async (event: { data: Blob }) => {
    if (!event.data.arrayBuffer) return;
    const buffer = await event.data.arrayBuffer();

    const fbb = new ByteBuffer(new Uint8Array(buffer));

    const message = MessageBundle.getRootAsMessageBundle(fbb).unpack();

    message.rpcMsgs.forEach((rpcHeader) => {
      rpclistenerRef.current?.dispatchEvent(
        new CustomEvent(RpcMessage[rpcHeader.messageType], {
          detail: rpcHeader.message,
        })
      );
    });

    message.dataFeedMsgs.forEach((datafeedHeader) => {
      datafeedlistenerRef.current?.dispatchEvent(
        new CustomEvent(DataFeedMessage[datafeedHeader.messageType], {
          detail: datafeedHeader.message,
        })
      );
    });

    message.pubSubMsgs.forEach((pubSubHeader) => {
      pubsublistenerRef.current?.dispatchEvent(
        new CustomEvent(PubSubUnion[pubSubHeader.uType], {
          detail: pubSubHeader.u,
        })
      );
    });
  };

  const sendRPCPacket = (type: RpcMessage, data: RPCPacketType): void => {
    if (webSocketRef?.current?.readyState !== WebSocket.OPEN) return;
    const fbb = new Builder(1);

    const message = new MessageBundleT();

    const rpcHeader = new RpcMessageHeaderT();
    rpcHeader.messageType = type;
    rpcHeader.message = data;

    message.rpcMsgs = [rpcHeader];
    fbb.finish(message.pack(fbb));

    webSocketRef.current.send(fbb.asUint8Array());

    rpcPacketCounterRef.current++;
  };

  const sendDataFeedPacket = (
    type: DataFeedMessage,
    data: DataFeedPacketType
  ): void => {
    if (webSocketRef?.current?.readyState !== WebSocket.OPEN) return;
    const fbb = new Builder(1);

    const message = new MessageBundleT();

    const datafeedHeader = new DataFeedMessageHeaderT();
    datafeedHeader.messageType = type;
    datafeedHeader.message = data;

    message.dataFeedMsgs = [datafeedHeader];
    fbb.finish(message.pack(fbb));

    webSocketRef.current.send(fbb.asUint8Array());
  };

  const sendPubSubPacket = (
    type: PubSubUnion,
    data: PubSubPacketType
  ): void => {
    if (webSocketRef?.current?.readyState !== WebSocket.OPEN) return;
    const fbb = new Builder(1);

    const message = new MessageBundleT();

    const pubSubHeader = new PubSubHeaderT();
    pubSubHeader.uType = type;
    pubSubHeader.u = data;

    message.pubSubMsgs = [pubSubHeader];
    fbb.finish(message.pack(fbb));

    webSocketRef.current.send(fbb.asUint8Array());
  };

  const connect = () => {
    webSocketRef.current = new WebSocket('ws://localhost:21110');

    // Connection opened
    webSocketRef.current.addEventListener('open', onConnected);
    webSocketRef.current.addEventListener('close', onConnectionClose);
    webSocketRef.current.addEventListener('message', onMessage);
  };

  const disconnect = () => {
    if (!webSocketRef.current) return;

    webSocketRef.current.removeEventListener('open', onConnected);
    webSocketRef.current.removeEventListener('close', onConnectionClose);
    webSocketRef.current.removeEventListener('message', onMessage);
    if (isConnected) {
      setConnected(false);
      webSocketRef.current.close();
    }
  };

  useEffect(() => {
    connect();
    return () => {
      disconnect();
    };
  }, []);

  return {
    isConnected,
    isFirstConnection,
    useDataFeedPacket: <T>(
      type: DataFeedMessage,
      callback: (packet: T) => void
    ) => {
      useEffect(() => {
        const onEvent = (event: CustomEventInit) => {
          callback(event.detail);
        };
        datafeedlistenerRef.current.addEventListener(
          DataFeedMessage[type],
          onEvent
        );
        return () => {
          datafeedlistenerRef.current.removeEventListener(
            DataFeedMessage[type],
            onEvent
          );
        };
      }, [callback, type]);
    },
    useRPCPacket: <T>(type: RpcMessage, callback: (packet: T) => void) => {
      useEffect(() => {
        const onEvent = (event: CustomEventInit) => {
          callback(event.detail);
        };
        rpclistenerRef.current.addEventListener(RpcMessage[type], onEvent);
        return () => {
          rpclistenerRef.current.removeEventListener(RpcMessage[type], onEvent);
        };
      }, [callback, type]);
    },
    usePubSubPacket: <T>(type: PubSubUnion, callback: (packet: T) => void) => {
      useEffect(() => {
        const onEvent = (event: CustomEventInit) => {
          callback(event.detail);
        };
        pubsublistenerRef.current.addEventListener(PubSubUnion[type], onEvent);
        return () => {
          pubsublistenerRef.current.removeEventListener(
            PubSubUnion[type],
            onEvent
          );
        };
      }, [callback, type]);
    },
    sendRPCPacket,
    sendDataFeedPacket,
    sendPubSubPacket,
  };
}

export function useWebsocketAPI(): WebSocketApi {
  const context = useContext<WebSocketApi>(WebSocketApiContext);
  if (!context) {
    throw new Error('useWebsocketAPI must be within a WebSocketApi Provider');
  }
  return context;
}
