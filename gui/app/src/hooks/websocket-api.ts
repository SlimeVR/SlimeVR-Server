import { createContext, useContext, useEffect, useRef, useState } from 'react';

import {
  BoneRegistryRequestT,
  BoneRegistryT,
  ClientHelloT,
  ConfigurationDoneT,
  ConnectionMessage,
  ConnectionMessageHeaderT,
  DataFeedMessage,
  DataFeedMessageHeaderT,
  HelloStatus,
  MessageBundle,
  MessageBundleT,
  RpcMessage,
  RpcMessageHeaderT,
  ServerHelloT,
} from 'solarxr-protocol';

import { Builder, ByteBuffer } from 'flatbuffers';
import { useStore } from 'jotai';
import { useInterval, useTimeout } from './timeout';
import { log } from '@/utils/logging';
import { boneRegistryAtom } from '@/store/app-store';
import { bodyPartOfKey } from '@/utils/body-part';

// Must match SOLARXR_PROTOCOL_VERSION in server/core/.../solarxr/protocol.kt
const SOLARXR_PROTOCOL_VERSION = 2;

type ConnectionPhase = 'awaiting-hello' | 'configuring' | 'ready';

export interface WebSocketApi {
  isConnected: boolean;
  isFirstConnection: boolean;
  timedOut: boolean;
  reconnect: () => void;
  useRPCPacket: <T>(type: RpcMessage, callback: (packet: T) => void) => void;
  useDataFeedPacket: <T>(type: DataFeedMessage, callback: (packet: T) => void) => void;
  sendRPCPacket: (type: RpcMessage, data: RPCPacketType) => void;
  sendDataFeedPacket: (type: DataFeedMessage, data: DataFeedPacketType) => void;
}

export const WebSocketApiContext = createContext<WebSocketApi>(undefined as never);

export type RPCPacketType = RpcMessageHeaderT['message'];
export type DataFeedPacketType = DataFeedMessageHeaderT['message'];

export function useProvideWebsocketApi(): WebSocketApi {
  const store = useStore();
  const rpcPacketCounterRef = useRef<number>(0);
  const webSocketRef = useRef<WebSocket | null>(null);
  const rpclistenerRef = useRef<EventTarget>(new EventTarget());
  const datafeedlistenerRef = useRef<EventTarget>(new EventTarget());
  const phaseRef = useRef<ConnectionPhase>('awaiting-hello');
  const [isFirstConnection, setFirstConnection] = useState(true);
  const [timedOut, setTimedOut] = useState(false);
  const [isConnected, setConnected] = useState(false);

  const urlParams = new URLSearchParams(window.location.search);
  const targetIp = urlParams.get('ip') ?? 'localhost';
  const targetPort = urlParams.get('port') ?? '21110';

  useInterval(() => {
    if (webSocketRef.current && !isConnected) {
      log('Attempting to reconnect');
      reconnect();
    }
  }, 3000);

  const sendConnectionMessage = (
    message: ConnectionMessageHeaderT['message'],
    messageType: ConnectionMessage
  ) => {
    if (webSocketRef?.current?.readyState !== WebSocket.OPEN) return;
    const fbb = new Builder(64);
    const bundle = new MessageBundleT();
    bundle.connectionMsgs = [new ConnectionMessageHeaderT(messageType, message)];
    MessageBundle.finishMessageBundleBuffer(fbb, bundle.pack(fbb));
    webSocketRef.current.send(fbb.asUint8Array());
  };

  const onConnected = () => {
    if (!webSocketRef.current) return;
    setTimedOut(false);

    sendConnectionMessage(
      new ClientHelloT(SOLARXR_PROTOCOL_VERSION),
      ConnectionMessage.ClientHello
    );
  };

  const onConnectionClose = () => {
    phaseRef.current = 'awaiting-hello';
    store.set(boneRegistryAtom, new Map());
    setConnected(false);
    rpcPacketCounterRef.current = 0;
  };

  const onMessage = async (event: { data: Blob }) => {
    if (!event.data.arrayBuffer) return;
    const buffer = await event.data.arrayBuffer();
    const fbb = new ByteBuffer(new Uint8Array(buffer));

    const message = MessageBundle.getRootAsMessageBundle(fbb).unpack();

    message.connectionMsgs.forEach((connectionHeader) => {
      switch (connectionHeader.messageType) {
        case ConnectionMessage.ServerHello: {
          const hello = connectionHeader.message as ServerHelloT;
          if (hello.status !== HelloStatus.ACCEPTED) {
            log(`SolarXR handshake rejected (status ${hello.status}), reconnecting`);
            reconnect();
            return;
          }
          phaseRef.current = 'configuring';
          sendConnectionMessage(
            new BoneRegistryRequestT(),
            ConnectionMessage.BoneRegistryRequest
          );
          sendConnectionMessage(new ConfigurationDoneT(), ConnectionMessage.ConfigurationDone);
          break;
        }
        case ConnectionMessage.BoneRegistry: {
          const registry = connectionHeader.message as BoneRegistryT;
          store.set(
            boneRegistryAtom,
            new Map(
              registry.bones.map((bone) => [
                bone.id,
                bodyPartOfKey(bone.key?.toString() ?? ''),
              ])
            )
          );
          break;
        }
        case ConnectionMessage.ConfigurationDone:
          phaseRef.current = 'ready';
          setFirstConnection(false);
          setConnected(true);
          break;
        case ConnectionMessage.ConnectionError:
          log(`SolarXR connection error: ${JSON.stringify(connectionHeader.message)}`);
          break;
        default:
          break;
      }
    });

    if (phaseRef.current !== 'ready') return;

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
  };

  const sendRPCPacket = (type: RpcMessage, data: RPCPacketType): void => {
    if (webSocketRef?.current?.readyState !== WebSocket.OPEN) return;
    const fbb = new Builder(1);

    const message = new MessageBundleT();

    const rpcHeader = new RpcMessageHeaderT();
    rpcHeader.messageType = type;
    rpcHeader.message = data;

    message.rpcMsgs = [rpcHeader];
    MessageBundle.finishMessageBundleBuffer(fbb, message.pack(fbb));

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
    MessageBundle.finishMessageBundleBuffer(fbb, message.pack(fbb));

    webSocketRef.current.send(fbb.asUint8Array());
  };

  const connect = () => {
    phaseRef.current = 'awaiting-hello';
    webSocketRef.current = new WebSocket(`ws://${targetIp}:${targetPort}`);

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
    webSocketRef.current.close();
    webSocketRef.current = null;
    phaseRef.current = 'awaiting-hello';
    setConnected(false);
  };

  const reconnect = () => {
    disconnect();
    connect();
  };

  useTimeout(() => {
    if (!isConnected && isFirstConnection) {
      setTimedOut(true);
    }
  }, 10_000); // Show the user that the server timed out if no connection after 10s

  useEffect(() => {
    connect();
    return () => {
      disconnect();
    };
  }, []);

  return {
    isConnected,
    isFirstConnection,
    timedOut,
    reconnect,
    useDataFeedPacket: <T>(type: DataFeedMessage, callback: (packet: T) => void) => {
      useEffect(() => {
        const onEvent = (event: CustomEventInit) => {
          callback(event.detail);
        };
        datafeedlistenerRef.current.addEventListener(DataFeedMessage[type], onEvent);
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
    sendRPCPacket,
    sendDataFeedPacket,
  };
}

export function useWebsocketAPI(): WebSocketApi {
  const context = useContext<WebSocketApi>(WebSocketApiContext);
  if (!context) {
    throw new Error('useWebsocketAPI must be within a WebSocketApi Provider');
  }
  return context;
}
