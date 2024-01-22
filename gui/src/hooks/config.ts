import { createContext, useContext, useMemo, useState } from 'react';
import { DeveloperModeWidgetForm } from '@/components/widgets/DeveloperModeWidget';
import { error } from '@/utils/logging';
import { useDebouncedEffect } from './timeout';
import { Store } from '@tauri-apps/plugin-store';
import { useIsTauri } from './breakpoint';

export interface WindowConfig {
  width: number;
  height: number;
  x: number;
  y: number;
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
  advancedAssign: boolean;
  doneManualMounting: boolean;
}

export interface ConfigContext {
  config: Config | null;
  loading: boolean;
  setConfig: (config: Partial<Config>) => Promise<void>;
  loadConfig: () => Promise<Config | null>;
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
  advancedAssign: false,
  doneManualMounting: false,
};

interface CrossStorage {
  set(key: string, value: string): Promise<void>;
  get(key: string): Promise<string | null>;
}

const tauriStore: CrossStorage = new Store('gui-settings.dat');

const localStore: CrossStorage = {
  get: async (key) => localStorage.getItem(key),
  set: async (key, value) => localStorage.setItem(key, value),
};

function fallbackToDefaults(loadedConfig: any): Config {
  return Object.assign({}, defaultConfig, loadedConfig);
}

export function useConfigProvider(): ConfigContext {
  const [currConfig, set] = useState<Config | null>(null);
  const [loading, setLoading] = useState(false);
  const tauri = useIsTauri();
  const store = useMemo(() => (tauri ? tauriStore : localStore), [tauri]);

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
  };

  return {
    config: currConfig,
    loading,
    setConfig,
    loadConfig: async () => {
      setLoading(true);
      try {
        const migrated = await localStorage.getItem('configMigratedToTauri');
        if (!migrated) {
          const oldConfig = localStorage.getItem('config.json');

          if (oldConfig) await store.set('config.json', oldConfig);

          localStorage.setItem('configMigratedToTauri', 'true');
        }

        const json = await store.get('config.json');

        if (!json) throw new Error('Config has ceased existing for some reason');

        const loadedConfig = fallbackToDefaults(JSON.parse(json));
        set(loadedConfig);

        setLoading(false);
        return loadedConfig;
      } catch (e) {
        error(e);
        setConfig(defaultConfig);
        setLoading(false);
        return null;
      }
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
