import { createContext, useContext, useEffect, useState } from 'react';
import {
  DataFeedMessage,
  DataFeedUpdateT,
  ResetResponseT,
  ResetStatus,
  ResetType,
  RpcMessage,
  StartDataFeedT,
} from 'solarxr-protocol';
import { playSoundOnResetEnded, playSoundOnResetStarted } from '@/sounds/sounds';
import { useConfig } from './config';
import { useBonesDataFeedConfig, useDataFeedConfig } from './datafeed-config';
import { useWebsocketAPI } from './websocket-api';
import { error } from '@/utils/logging';
import { useAtomValue, useSetAtom } from 'jotai';
import { bonesAtom, datafeedAtom, devicesAtom } from '@/store/app-store';
import { updateSentryContext } from '@/utils/sentry';
import { fetchCurrentFirmwareRelease, FirmwareRelease } from './firmware-update';

export interface AppContext {
  currentFirmwareRelease: FirmwareRelease | null;
}

export function useProvideAppContext(): AppContext {
  const { useRPCPacket, sendDataFeedPacket, useDataFeedPacket, isConnected } =
    useWebsocketAPI();
  const { config } = useConfig();
  const { dataFeedConfig } = useDataFeedConfig();
  const bonesDataFeedConfig = useBonesDataFeedConfig();
  const setDatafeed = useSetAtom(datafeedAtom);
  const setBones = useSetAtom(bonesAtom);
  const devices = useAtomValue(devicesAtom);

  const [currentFirmwareRelease, setCurrentFirmwareRelease] =
    useState<FirmwareRelease | null>(null);

  useEffect(() => {
    if (isConnected) {
      const startDataFeed = new StartDataFeedT();
      startDataFeed.dataFeeds = [dataFeedConfig, bonesDataFeedConfig];
      sendDataFeedPacket(DataFeedMessage.StartDataFeed, startDataFeed);
    }
  }, [isConnected]);

  useDataFeedPacket(DataFeedMessage.DataFeedUpdate, (packet: DataFeedUpdateT) => {
    if (packet.index === 0) {
      setDatafeed(packet);
    } else if (packet.index === 1) {
      setBones(packet.bones);
    }
  });

  useEffect(() => {
    updateSentryContext(devices);
  }, [devices]);

  useRPCPacket(RpcMessage.ResetResponse, ({ status, resetType }: ResetResponseT) => {
    if (!config?.feedbackSound) return;
    try {
      switch (status) {
        case ResetStatus.STARTED: {
          if (resetType !== ResetType.Yaw)
            playSoundOnResetStarted(config?.feedbackSoundVolume);
          break;
        }
        case ResetStatus.FINISHED: {
          playSoundOnResetEnded(resetType, config?.feedbackSoundVolume);
          break;
        }
      }
    } catch (e) {
      error(e);
    }
  });

  useEffect(() => {
    fetchCurrentFirmwareRelease().then((res) => setCurrentFirmwareRelease(res));
  }, []);

  return {
    currentFirmwareRelease,
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
