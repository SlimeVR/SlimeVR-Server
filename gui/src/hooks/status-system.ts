import { createContext, useEffect, useReducer, useContext } from 'react';
import {
  RpcMessage,
  StatusData,
  StatusMessageT,
  StatusSystemFixedT,
  StatusSystemRequestT,
  StatusSystemResponseT,
  StatusSystemUpdateT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { FluentVariable } from '@fluent/bundle';

type StatusSystemStateAction =
  | StatusSystemStateFixedAction
  | StatusSystemStateNewAction
  | StatusSystemStateUpdateAction;

interface StatusSystemStateFixedAction {
  type: RpcMessage.StatusSystemFixed;
  data: number;
}

interface StatusSystemStateUpdateAction {
  type: RpcMessage.StatusSystemUpdate;
  data: StatusMessageT;
}

interface StatusSystemStateNewAction {
  type: RpcMessage.StatusSystemResponse;
  data: StatusMessageT[];
}

interface StatusSystemState {
  statuses: {
    [id: number]: StatusMessageT;
  };
}

export interface StatusSystemContext {
  statuses: {
    [id: number]: StatusMessageT;
  };
}

function reducer(
  state: StatusSystemState,
  action: StatusSystemStateAction
): StatusSystemState {
  switch (action.type) {
    case RpcMessage.StatusSystemFixed: {
      const newState = {
        statuses: { ...state.statuses },
      };
      delete newState.statuses[action.data];
      return newState;
    }
    case RpcMessage.StatusSystemUpdate: {
      return {
        statuses: { ...state.statuses, [action.data.id]: action.data },
      };
    }
    case RpcMessage.StatusSystemResponse: {
      return {
        // Convert the array into an object, we dont want to have an array on our map!
        statuses: action.data.reduce((prev, cur) => ({ ...prev, [cur.id]: cur }), {}),
      };
    }
  }
}

export function useProvideStatusContext(): StatusSystemContext {
  const { useRPCPacket, sendRPCPacket, isConnected } = useWebsocketAPI();
  const [state, dispatch] = useReducer(reducer, { statuses: {} });

  useRPCPacket(
    RpcMessage.StatusSystemResponse,
    ({ currentStatuses }: StatusSystemResponseT) =>
      dispatch({ type: RpcMessage.StatusSystemResponse, data: currentStatuses })
  );

  useRPCPacket(RpcMessage.StatusSystemFixed, ({ fixedStatusId }: StatusSystemFixedT) =>
    dispatch({ type: RpcMessage.StatusSystemFixed, data: fixedStatusId })
  );

  useRPCPacket(
    RpcMessage.StatusSystemUpdate,
    ({ newStatus }: StatusSystemUpdateT) =>
      newStatus && dispatch({ type: RpcMessage.StatusSystemUpdate, data: newStatus })
  );

  useEffect(() => {
    if (!isConnected) return;
    sendRPCPacket(RpcMessage.StatusSystemRequest, new StatusSystemRequestT());
  }, [isConnected]);

  return state;
}

export const StatusSystemC = createContext<StatusSystemContext>(undefined as never);

export function useStatusContext() {
  const context = useContext<StatusSystemContext>(StatusSystemC);
  if (!context) {
    throw new Error('useStatusContext must be within a StatusSystemContext Provider');
  }
  return context;
}

export function parseStatusToLocale(
  status: StatusMessageT
): Record<string, FluentVariable> {
  switch (status.dataType) {
    case StatusData.NONE:
    case StatusData.StatusTrackerReset:
      return {};
    case StatusData.StatusDoublyAssignedBody:
      // const data = status.data as StatusDoublyAssignedBodyT;
      return {};
  }
}
