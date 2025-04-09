import { FlightListRequestT, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useEffect } from 'react';

export function useSessionFlightlist() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  useRPCPacket(RpcMessage.FlightListResponse, (data) => {
    console.log(data);
  });
  useRPCPacket(RpcMessage.FlightListStepChangeResponse, (data) => {
    console.log(data);
  });

  useEffect(() => {
    sendRPCPacket(RpcMessage.FlightListRequest, new FlightListRequestT());
  }, []);
}
