import { createContext, useContext, useState } from 'react';
import { DeveloperModeWidgetForm } from '@/components/widgets/DeveloperModeWidget';
import { error, log } from '@/utils/logging';
import { useDebouncedEffect } from './timeout';
import { createStore, Store } from '@tauri-apps/plugin-store';
import { useIsTauri } from './breakpoint';
import { waitUntil } from '@/utils/a11y';
import { appConfigDir, resolve } from '@tauri-apps/api/path';
import { exists, mkdir, writeTextFile } from '@tauri-apps/plugin-fs';
import { isTauri } from '@tauri-apps/api/core';

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
  assignMode: AssignMode;
  discordPresence: boolean;
  decorations: boolean;
  showNavbarOnboarding: boolean;
  profile: string;
}

export interface ConfigContext {
  config: Config | null;
  loading: boolean;
  setConfig: (config: Partial<Config>) => Promise<void>;
  loadConfig: () => Promise<Config | null>;
  saveConfig: () => Promise<void>;
}

export const defaultConfig: Omit<Config, 'devSettings'> = {
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
  assignMode: AssignMode.Core,
  discordPresence: false,
  decorations: false,
  showNavbarOnboarding: true,
  profile: 'default',
};

interface CrossStorage {
  set(key: string, value: string): Promise<void>;
  get(key: string): Promise<string | null>;
}

const localStore: CrossStorage = {
  get: async (key) => localStorage.getItem(key),
  set: async (key, value) => localStorage.setItem(key, value),
};

let store: CrossStorage = isTauri()
  ? await createStore('gui-settings.dat')
  : localStore;

function fallbackToDefaults(loadedConfig: any): Config {
  return Object.assign({}, defaultConfig, loadedConfig);
}

async function getProfileStore(profile: string): Promise<CrossStorage> {
  const appDirectory = await appConfigDir();
  const profileDir = await resolve(`${appDirectory}/profiles/${profile}`);
  const profileFile = await resolve(`${profileDir}/gui-settings.dat`);

  const profileDirExists = await exists(profileDir);
  log(`Profile directory exists: ${profileDirExists}`);
  const profileFileExists = await exists(profileFile);
  log(`Profile file exists: ${profileFileExists}`);

  if (profileDirExists && profileFileExists) {
    log(`Profile ${profile} exists, loading profile config`);
  } else {
    log(`Profile ${profile} does not exist for some reason, creating profile config with defaults`);

    await mkdir(profileDir, { recursive: true });
    await writeTextFile(profileFile, JSON.stringify(defaultConfig));
  }

  return createStore(profileFile);
}

export function useConfigProvider(): ConfigContext {
  const [currConfig, set] = useState<Config | null>(null);
  const [loading, setLoading] = useState(false);
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
          let newConfig: Partial<Config> = JSON.parse(
            (await store.get('config.json')) ?? '{}'
          );

          // Get profile, if not default profile, load profile config
          const profile = newConfig.profile ?? 'default';
          const profileConfig = JSON.parse(
            (await store.get(`profiles/${profile}/config.json`)) ?? '{}'
          );

          // If profile config is empty, use default config
          newConfig = profileConfig === '{}' ? profileConfig : newConfig;

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
    loading,
    setConfig,
    loadConfig: async () => {
      setLoading(true);
      try {
        const migrated = await store.get('configMigratedToTauri');
        if (!migrated) {
          const oldConfig = localStorage.getItem('config.json');

          if (oldConfig) await store.set('config.json', oldConfig);

          store.set('configMigratedToTauri', 'true');
        }

        const json = await store.get('config.json');

        if (!json) throw new Error('Config has ceased existing for some reason');

        let loadedConfig = fallbackToDefaults(JSON.parse(json));

        // Get profile and set the store to the profile's store if it exists
        const profile = loadedConfig.profile ?? 'default';
        const profileStore = await getProfileStore(profile);
        store = profileStore;

        const profileConfig = JSON.parse(
          (await profileStore.get('config.json')) ?? '{}'
        );

        // If profile config is empty, use default config
        loadedConfig = profileConfig === '{}' ? profileConfig : loadedConfig;
        set(loadedConfig);

        log('Loaded config: ', JSON.stringify(loadedConfig, null, 2));

        setLoading(false);
        return loadedConfig;
      } catch (e) {
        error(e);
        setConfig(defaultConfig);
        setLoading(false);
        return null;
      }
    },
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
