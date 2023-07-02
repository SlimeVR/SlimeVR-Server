import { BaseDirectory, readTextFile } from '@tauri-apps/api/fs';

import { createContext, useContext, useRef, useState } from 'react';
import { DeveloperModeWidgetForm } from '../components/widgets/DeveloperModeWidget';
import { log } from './logging';

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
  theme: string;
}

export interface ConfigContext {
  config: Config | null;
  loading: boolean;
  setConfig: (config: Partial<Config>) => Promise<void>;
  loadConfig: () => Promise<Config | null>;
}

const defaultConfig: Partial<Config> = {
  lang: 'en',
  doneOnboarding: false,
  watchNewDevices: true,
  feedbackSound: true,
  feedbackSoundVolume: 0.5,
  theme: 'slime',
};

function fallbackToDefaults(loadedConfig: any): Config {
  return Object.assign({}, defaultConfig, loadedConfig);
}

export function useConfigProvider(): ConfigContext {
  const debounceTimer = useRef<NodeJS.Timeout | null>(null);
  const [currConfig, set] = useState<Config | null>(null);
  const [loading, setLoading] = useState(false);

  const setConfig = async (config: Partial<Config>) => {
    const newConfig = config
      ? {
          ...currConfig,
          ...config,
        }
      : null;
    set(newConfig as Config);
    if ('theme' in config) {
      document.documentElement.dataset.theme = config.theme;
    }

    if (!debounceTimer.current) {
      debounceTimer.current = setTimeout(async () => {
        localStorage.setItem('config.json', JSON.stringify(newConfig));
        debounceTimer.current = null;
      }, 10);
    }
  };

  return {
    config: currConfig,
    loading,
    setConfig,
    loadConfig: async () => {
      setLoading(true);
      try {
        const migrated = localStorage.getItem('configMigrated');
        if (!migrated) {
          const oldConfig = await readTextFile('config.json', {
            dir: BaseDirectory.App,
          }).catch(() => null);

          if (oldConfig) localStorage.setItem('config.json', oldConfig);

          localStorage.setItem('configMigrated', 'true');
        }

        const json = localStorage.getItem('config.json');

        if (!json) throw new Error('Config has ceased existing for some reason');

        const loadedConfig = fallbackToDefaults(JSON.parse(json));
        set(loadedConfig);
        document.documentElement.dataset.theme = loadedConfig.theme;
        setLoading(false);
        return loadedConfig;
      } catch (e) {
        log(e);
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
