import {
  createContext,
  Dispatch,
  Reducer,
  useContext,
  useEffect,
  useMemo,
  useReducer,
} from 'react';
import {
  BoneT,
  DataFeedMessage,
  DataFeedUpdateT,
  DeviceDataT,
  ResetResponseT,
  ResetStatus,
  RpcMessage,
  StartDataFeedT,
  TrackerDataT,
} from 'solarxr-protocol';
import { playSoundOnResetStarted } from '@/sounds/sounds';
import { useConfig } from './config';
import { useDataFeedConfig } from './datafeed-config';
import { useWebsocketAPI } from './websocket-api';
import { error } from '@/utils/logging';

export interface FlatDeviceTracker {
  device?: DeviceDataT;
  tracker: TrackerDataT;
}

export type AppStateAction =
  | { type: 'datafeed'; value: DataFeedUpdateT }
  | { type: 'ignoreTracker'; value: string };

export interface AppState {
  datafeed?: DataFeedUpdateT;
  ignoredTrackers: Set<string>;
}

export interface AppContext {
  state: AppState;
  trackers: FlatDeviceTracker[];
  dispatch: Dispatch<AppStateAction>;
  bones: BoneT[];
  computedTrackers: FlatDeviceTracker[];
}

export function reducer(state: AppState, action: AppStateAction) {
  switch (action.type) {
    case 'datafeed':
      return { ...state, datafeed: action.value };
    case 'ignoreTracker':
      return {
        ...state,
        ignoredTrackers: new Set([...state.ignoredTrackers, action.value]),
      };
    default:
      throw new Error(`unhandled state action ${(action as AppStateAction).type}`);
  }
}

export function useProvideAppContext(): AppContext {
  const { useRPCPacket, sendDataFeedPacket, useDataFeedPacket, isConnected } =
    useWebsocketAPI();
  const { config } = useConfig();
  const { dataFeedConfig } = useDataFeedConfig();
  const [state, dispatch] = useReducer<Reducer<AppState, AppStateAction>>(reducer, {
    datafeed: new DataFeedUpdateT(),
    ignoredTrackers: new Set(),
  });

  useEffect(() => {
    if (isConnected) {
      const startDataFeed = new StartDataFeedT();
      startDataFeed.dataFeeds = [dataFeedConfig];
      sendDataFeedPacket(DataFeedMessage.StartDataFeed, startDataFeed);
    }
  }, [isConnected]);

  const trackers = useMemo(
    () =>
      (state.datafeed?.devices || []).reduce<FlatDeviceTracker[]>(
        (curr, device) => [
          ...curr,
          ...device.trackers.map((tracker) => ({ tracker, device })),
        ],
        []
      ),
    [state]
  );

  const computedTrackers: FlatDeviceTracker[] = useMemo(
    () => (state.datafeed?.syntheticTrackers || []).map((tracker) => ({ tracker })),
    [state]
  );

  const bones = useMemo(() => state.datafeed?.bones || [], [state]);

  useDataFeedPacket(DataFeedMessage.DataFeedUpdate, (packet: DataFeedUpdateT) => {
    dispatch({ type: 'datafeed', value: packet });
  });

  useRPCPacket(RpcMessage.ResetResponse, ({ status, resetType }: ResetResponseT) => {
    if (!config?.feedbackSound) return;
    try {
      switch (status) {
        case ResetStatus.STARTED: {
          playSoundOnResetStarted(resetType, config?.feedbackSoundVolume);
          break;
        }
      }
    } catch (e) {
      error(e);
    }
  });

  return {
    state,
    trackers,
    dispatch,
    bones,
    computedTrackers,
  };
}

export const AppContextC = createContext<AppContext>(undefined as any);

export function useAppContext() {
  const context = useContext<AppContext>(AppContextC);
  if (!context) {
    throw new Error('useAppContext must be within a AppContext Provider');
  }
  return context;
}
