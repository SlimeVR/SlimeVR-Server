import { createContext, useContext, useState } from 'react';
import {
  defaultValues as defaultDevSettings,
  DeveloperModeWidgetForm,
} from '@/components/widgets/DeveloperModeWidget';
import { error } from '@/utils/logging';
import { useDebouncedEffect } from './timeout';
import { load, Store } from '@tauri-apps/plugin-store';
import { useIsTauri } from './breakpoint';
import { waitUntil } from '@/utils/a11y';
import { isTauri } from '@tauri-apps/api/core';
import { v4 as uuidv4 } from 'uuid';

export interface WindowConfig {
  width: number;
  height: number;
  x: number;
  y: number;
}

export enum AssignMode {
  LowerBody = 'lower-body',
  Core = 'core',
  EnhancedCore = 'enhanced-core',
  FullBody = 'full-body',
  All = 'all',
}

export interface Config {
  uuid: string;
  debug: boolean;
  lang: string;
  doneOnboarding: boolean;
  watchNewDevices: boolean;
  devSettings: DeveloperModeWidgetForm;
  feedbackSound: boolean;
  feedbackSoundVolume: number;
  connectedTrackersWarning: boolean;
  theme: string;
  textSize: number;
  fonts: string[];
  useTray: boolean | null;
  mirrorView: boolean;
  assignMode: AssignMode | null;
  discordPresence: boolean;
  errorTracking: boolean | null;
  decorations: boolean;
  vrcMutedWarnings: string[];
  bvhDirectory: string | null;
  homeLayout: 'default' | 'table';
  skeletonPreview: boolean;
  lastUsedProportions: 'manual' | 'autobone' | 'scaled' | null;
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
  watchNewDevices: true,
  feedbackSound: true,
  feedbackSoundVolume: 0.5,
  connectedTrackersWarning: true,
  theme: 'slime',
  textSize: 12,
  fonts: ['poppins'],
  useTray: null,
  mirrorView: true,
  assignMode: null,
  discordPresence: false,
  errorTracking: null,
  decorations: false,
  vrcMutedWarnings: [],
  devSettings: defaultDevSettings,
  bvhDirectory: null,
  homeLayout: 'default',
  skeletonPreview: true,
  lastUsedProportions: null,
};

interface CrossStorage {
  set(key: string, value: unknown): Promise<void>;
  get<T>(key: string): Promise<T | undefined>;
}

const localStore: CrossStorage = {
  get: async <T>(key: string) => (localStorage.getItem(key) as T) ?? undefined,
  set: async (key, value) => localStorage.setItem(key, value as string),
};

const store: CrossStorage = isTauri()
  ? await load('gui-settings.dat', { autoSave: 100, defaults: {} })
  : localStore;

function fallbackToDefaults(loadedConfig: any): Config {
  return Object.assign({}, defaultConfig, loadedConfig);
}

// Move the load of the config ouside of react
// allows to load everything before the first render
export const loadConfig = async () => {
  try {
    const migrated = await store.get<string>('configMigratedToTauri');
    if (!migrated) {
      const oldConfig = localStorage.getItem('config.json');

      if (oldConfig) await store.set('config.json', oldConfig);

      store.set('configMigratedToTauri', 'true');
    }

    const json = await store.get<string>('config.json');

    if (!json) throw new Error('Config has ceased existing for some reason');

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
  const tauri = useIsTauri();

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
    if (tauri) {
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
      if (!tauri) return;
      await (store as Store).save();
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
