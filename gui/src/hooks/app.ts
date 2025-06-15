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
import { error } from '@/utils/logging';
import { useAtomValue, useSetAtom } from 'jotai';
import { bonesAtom, datafeedAtom, devicesAtom } from '@/store/app-store';
import { updateSentryContext } from '@/utils/sentry';
import { useConfig } from './config';
import { useBonesDataFeedConfig, useDataFeedConfig } from './datafeed-config';
import { useWebsocketAPI } from './websocket-api';
import { cacheWrap } from './cache';

export interface FirmwareRelease {
  name: string;
  version: string;
  changelog: string;
  firmwareFile: string;
}

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
    const fetchCurrentFirmwareRelease = async () => {
      const releases: any[] | null = JSON.parse(
        (await cacheWrap(
          'firmware-releases',
          () =>
            fetch('https://api.github.com/repos/SlimeVR/SlimeVR-Tracker-ESP/releases')
              .then((res) => res.text())
              .catch(() => null),
          60 * 60 * 1000
        )) ?? 'null'
      );
      if (!releases) return null;

      const firstRelease = releases.find(
        (release) =>
          release.prerelease === false &&
          release.assets &&
          release.assets.find(
            (asset: any) =>
              asset.name === 'BOARD_SLIMEVR-firmware.bin' && asset.browser_download_url
          )
      );

      let version = firstRelease.tag_name;
      if (version.charAt(0) === 'v') {
        version = version.substring(1);
      }

      if (firstRelease) {
        return {
          name: firstRelease.name,
          version,
          changelog: firstRelease.body,
          firmwareFile: firstRelease.assets.find(
            (asset: any) =>
              asset.name === 'BOARD_SLIMEVR-firmware.bin' && asset.browser_download_url
          ).browser_download_url,
        };
      }
      return null;
    };

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
