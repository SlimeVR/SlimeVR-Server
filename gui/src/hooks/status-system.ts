import { createContext, useEffect, useReducer, useContext } from 'react';
import {
  BodyPart,
  RpcMessage,
  StatusData,
  StatusMessageT,
  StatusSteamVRDisconnectedT,
  StatusSystemFixedT,
  StatusSystemRequestT,
  StatusSystemResponseT,
  StatusSystemUpdateT,
  StatusTrackerErrorT,
  StatusTrackerResetT,
  StatusUnassignedHMDT,
  TrackerDataT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { FluentVariable } from '@fluent/bundle';
import { ReactLocalization } from '@fluent/react';
import { FlatDeviceTracker } from '@/store/app-store';

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
  status: StatusMessageT,
  trackers: FlatDeviceTracker[] | null,
  l10n: ReactLocalization
): Record<string, FluentVariable> {
  switch (status.dataType) {
    case StatusData.NONE:
    case StatusData.StatusTrackerReset:
    case StatusData.StatusUnassignedHMD:
    case StatusData.StatusPublicNetwork:
      return {};
    case StatusData.StatusSteamVRDisconnected: {
      const data = status.data as StatusSteamVRDisconnectedT;
      if (typeof data.bridgeSettingsName === 'string') {
        return { type: data.bridgeSettingsName };
      }
      return {};
    }
    case StatusData.StatusTrackerError: {
      const data = status.data as StatusTrackerErrorT;
      if (data.trackerId?.trackerNum === undefined || !trackers) {
        return {};
      }

      const tracker = trackers.find(
        ({ tracker }) =>
          tracker?.trackerId?.trackerNum == data.trackerId?.trackerNum &&
          tracker?.trackerId?.deviceId?.id == data.trackerId?.deviceId?.id
      );
      if (!tracker)
        return {
          trackerName: 'unknown',
        };
      const name = tracker.tracker.info?.customName
        ? tracker.tracker.info?.customName
        : tracker.tracker.info?.bodyPart
          ? l10n.getString('body_part-' + BodyPart[tracker.tracker.info?.bodyPart])
          : tracker.tracker.info?.displayName || 'unknown';
      if (typeof name !== 'string') {
        return {
          trackerName: new TextDecoder().decode(name),
        };
      }
      return { trackerName: name };
    }
  }
}

export const doesntContainTrackerInfo: readonly StatusData[] = [StatusData.NONE];
export function trackerStatusRelated(
  tracker: TrackerDataT,
  status: StatusMessageT
): boolean {
  if (doesntContainTrackerInfo.includes(status.dataType)) {
    return false;
  }

  switch (status.dataType) {
    case StatusData.StatusTrackerReset: {
      const data = status.data as StatusTrackerResetT;
      return (
        data.trackerId?.trackerNum == tracker.trackerId?.trackerNum &&
        data.trackerId?.deviceId?.id === tracker.trackerId?.deviceId?.id
      );
    }
    case StatusData.StatusTrackerError: {
      const data = status.data as StatusTrackerErrorT;
      return (
        data.trackerId?.trackerNum == tracker.trackerId?.trackerNum &&
        data.trackerId?.deviceId?.id === tracker.trackerId?.deviceId?.id
      );
    }
    case StatusData.StatusUnassignedHMD: {
      const data = status.data as StatusUnassignedHMDT;
      return (
        data.trackerId?.trackerNum == tracker.trackerId?.trackerNum &&
        data.trackerId?.deviceId?.id === tracker.trackerId?.deviceId?.id
      );
    }
    default:
      return false;
  }
}
