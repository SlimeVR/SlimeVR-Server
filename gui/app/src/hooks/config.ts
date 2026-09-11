import { createContext, useContext, useState } from 'react';
import { error } from '@/utils/logging';
import { useDebouncedEffect } from './timeout';
import { waitUntil } from '@/utils/a11y';
import { v4 as uuidv4 } from 'uuid';
import { useElectron } from './electron';
import { CrossStorage } from '@slimevr/gui-shared';

export interface WindowConfig {
  width: number;
  height: number;
  x: number;
  y: number;
}

export interface DeveloperModeConfig {
  highContrast: boolean;
  preciseRotation: boolean;
  fastDataFeed: boolean;
  rawSlimeRotation: boolean;
}

export const defaultDevSettings: DeveloperModeConfig = {
  highContrast: false,
  preciseRotation: false,
  fastDataFeed: false,
  rawSlimeRotation: false,
};

export interface TrackersTableColumnsConfig {
  type: boolean;
  battery: boolean;
  ping: boolean;
  tps: boolean;
  rotation: boolean;
  temperature: boolean;
  linearAcceleration: boolean;
  position: boolean;
  stayAligned: boolean;
  url: boolean;
}

export const defaultTrackersTableColumns: TrackersTableColumnsConfig = {
  type: true,
  battery: true,
  ping: true,
  tps: true,
  rotation: true,
  temperature: true,
  linearAcceleration: false,
  position: false,
  stayAligned: false,
  url: false,
};

export const trackersTableColumnOrder = [
  'type',
  'battery',
  'ping',
  'tps',
  'rotation',
  'temperature',
  'linearAcceleration',
  'position',
  'stayAligned',
  'url',
] as const satisfies readonly (keyof TrackersTableColumnsConfig)[];

export type TrackersTableOptionalColumn = (typeof trackersTableColumnOrder)[number];

export interface TrackerDisplayConfig {
  showBatteryVoltage: boolean;
  showNumericSignal: boolean;
}

export const defaultTrackerDisplay: TrackerDisplayConfig = {
  showBatteryVoltage: false,
  showNumericSignal: false,
};

export interface DongleTelemetryConfig {
  windowSec: number;
  disabledTrackerIds: number[];
  showMinMax: boolean;
}

export const defaultDongleTelemetryConfig: DongleTelemetryConfig = {
  windowSec: 30,
  disabledTrackerIds: [],
  showMinMax: true,
};

export interface Config {
  uuid: string;
  debug: boolean;
  lang: string;
  doneOnboarding: boolean;
  devSettings: DeveloperModeConfig;
  dongleTelemetry: DongleTelemetryConfig;
  trackersTableColumns: TrackersTableColumnsConfig;
  trackerDisplay: TrackerDisplayConfig;
  feedbackSound: boolean;
  feedbackSoundVolume: number;
  connectedTrackersWarning: boolean;
  theme: string;
  textSize: number;
  fonts: string[];
  useTray: boolean | null;
  mirrorView: boolean;
  assignShowAllBodyParts: boolean;
  discordPresence: boolean;
  errorTracking: boolean | null;
  vrcMutedWarnings: string[];
  bvhDirectory: string | null;
  homeLayout: 'default' | 'table';
  skeletonPreview: boolean;
  skeletonPreviewStyle: 'mesh' | 'lines';
  lastUsedProportions: 'manual' | 'autobone' | 'scaled' | null;
  dontShowUdevModal: boolean;
  collapsedConnectionGroups: Record<string, boolean>;
  controllerNav: boolean;
}

export interface ConfigContext {
  config: Config | null;
  setConfig: (config: Partial<Config>) => Promise<void>;
  saveConfig: () => Promise<void>;
}

export const defaultConfig: Config = {
  uuid: uuidv4(),
  lang: 'en',
  debug: false,
  doneOnboarding: false,
  feedbackSound: true,
  feedbackSoundVolume: 0.5,
  connectedTrackersWarning: true,
  theme: 'slime',
  textSize: 12,
  fonts: ['poppins'],
  useTray: null,
  mirrorView: true,
  assignShowAllBodyParts: false,
  discordPresence: false,
  errorTracking: null,
  vrcMutedWarnings: [],
  devSettings: defaultDevSettings,
  dongleTelemetry: defaultDongleTelemetryConfig,
  trackersTableColumns: defaultTrackersTableColumns,
  trackerDisplay: defaultTrackerDisplay,
  bvhDirectory: null,
  homeLayout: 'default',
  skeletonPreview: true,
  skeletonPreviewStyle: 'mesh',
  lastUsedProportions: null,
  dontShowUdevModal: false,
  collapsedConnectionGroups: {},
  controllerNav: true,
};

const localStore: CrossStorage = {
  get: async <T>(key: string) => (localStorage.getItem(key) as T) ?? undefined,
  set: async (key, value) => localStorage.setItem(key, value as string),
  save: async () => true,
  delete: async (key: string) => {
    localStorage.removeItem(key);
    return true;
  },
};

const store: CrossStorage = window.electronAPI
  ? await window.electronAPI.getStorage('settings')
  : localStore;

function fallbackToDefaults(loadedConfig: any): Config {
  return {
    ...defaultConfig,
    ...loadedConfig,
    devSettings: {
      ...defaultDevSettings,
      ...(loadedConfig?.devSettings ?? {}),
    },
    dongleTelemetry: {
      ...defaultDongleTelemetryConfig,
      ...(loadedConfig?.dongleTelemetry ?? loadedConfig?.telemetry ?? {}),
    },
    trackersTableColumns: {
      ...defaultTrackersTableColumns,
      ...(loadedConfig?.trackersTableColumns ?? {}),
    },
    trackerDisplay: {
      ...defaultTrackerDisplay,
      ...(loadedConfig?.trackerDisplay ?? {}),
    },
  };
}

// Move the load of the config ouside of react
// allows to load everything before the first render
export const loadConfig = async () => {
  try {
    const json = (await store.get<string>('config.json')) ?? '{}';
    const loadedConfig = fallbackToDefaults(JSON.parse(json));

    if (!loadedConfig.uuid) {
      // Make sure the config always has a uuid
      loadedConfig.uuid = uuidv4();
      await store.set('config.json', JSON.stringify(loadedConfig));
    }

    return loadedConfig;
  } catch (e) {
    error(e);
    return null;
  }
};

export function useConfigProvider(initialConfig: Config | null): ConfigContext {
  const [currConfig, set] = useState<Config | null>(
    initialConfig || (defaultConfig as Config)
  );
  const electron = useElectron();

  useDebouncedEffect(
    () => {
      if (!currConfig) return;

      store.set('config.json', JSON.stringify(currConfig));
    },
    [currConfig],
    100
  );

  const setConfig = async (config: Partial<Config>) => {
    set((curr) =>
      config
        ? ({
            ...curr,
            ...config,
          } as Config)
        : null
    );
    if (electron.isElectron) {
      await waitUntil(
        async () => {
          const newConfig: Partial<Config> = JSON.parse(
            (await store.get('config.json')) ?? '{}'
          );
          return Object.entries(config).every(([key, value]) =>
            typeof value === 'object'
              ? JSON.stringify(newConfig[key as keyof Config]) === JSON.stringify(value)
              : newConfig[key as keyof Config] === value
          );
        },
        100,
        10
      );
    } else {
      await waitUntil(
        () => {
          const newConfig: Partial<Config> = JSON.parse(
            localStorage.getItem('config.json') ?? '{}'
          );
          return Object.entries(config).every(([key, value]) =>
            typeof value === 'object'
              ? JSON.stringify(newConfig[key as keyof Config]) === JSON.stringify(value)
              : newConfig[key as keyof Config] === value
          );
        },
        100,
        10
      );
    }
  };

  return {
    config: currConfig,
    setConfig,
    saveConfig: async () => {
      if (!electron.isElectron) return;
      await store.save();
    },
  };
}

export const ConfigContextC = createContext<ConfigContext>(undefined as never);

export function useConfig() {
  const context = useContext<ConfigContext>(ConfigContextC);
  if (!context) {
    throw new Error('useConfig must be within a ConfigContext Provider');
  }
  return context;
}
