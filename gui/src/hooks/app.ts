import {
  createContext,
  Dispatch,
  Reducer,
  useContext,
  useEffect,
  useLayoutEffect,
  useMemo,
  useReducer,
} from 'react';
import { useNavigate } from 'react-router-dom';
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
import { playSoundOnResetStarted } from '../sounds/sounds';
import { useConfig } from './config';
import { useDataFeedConfig } from './datafeed-config';
import { useWebsocketAPI } from './websocket-api';
import { log } from '../utils/logging';

export interface FlatDeviceTracker {
  device?: DeviceDataT;
  tracker: TrackerDataT;
}

type AppStateAction = { type: 'datafeed'; value: DataFeedUpdateT };

export interface AppState {
  datafeed?: DataFeedUpdateT;
}

export interface AppContext {
  state: AppState;
  trackers: FlatDeviceTracker[];
  dispatch: Dispatch<AppStateAction>;
  bones: BoneT[];
}

export function reducer(state: AppState, action: AppStateAction) {
  switch (action.type) {
    case 'datafeed':
      return { ...state, datafeed: action.value };
    default:
      throw new Error(`unhandled state action ${action.type}`);
  }
}

export function useProvideAppContext(): AppContext {
  const { useRPCPacket, sendDataFeedPacket, useDataFeedPacket, isConnected } =
    useWebsocketAPI();
  const { config } = useConfig();
  const { dataFeedConfig } = useDataFeedConfig();
  const navigate = useNavigate();
  const [state, dispatch] = useReducer<Reducer<AppState, AppStateAction>>(reducer, {
    datafeed: new DataFeedUpdateT(),
  });

  useEffect(() => {
    if (isConnected) {
      const startDataFeed = new StartDataFeedT();
      startDataFeed.dataFeeds = [dataFeedConfig];
      sendDataFeedPacket(DataFeedMessage.StartDataFeed, startDataFeed);
    }
  }, [isConnected]);

  useLayoutEffect(() => {
    if (config && !config.doneOnboarding) {
      navigate('/onboarding/home');
    }
  }, [config]);

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
    } catch (error) {
      log(error);
    }
  });

  return {
    state,
    trackers,
    dispatch,
    bones,
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
