import { BaseDirectory, readTextFile } from '@tauri-apps/plugin-fs';

import { createContext, useContext, useState } from 'react';
import { DeveloperModeWidgetForm } from '@/components/widgets/DeveloperModeWidget';
import { error } from '@/utils/logging';
import { useDebouncedEffect } from './timeout';

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
  textSize: number;
  fonts: string[];
  advancedAssign: boolean;
}

export interface ConfigContext {
  config: Config | null;
  loading: boolean;
  setConfig: (config: Partial<Config>) => Promise<void>;
  loadConfig: () => Promise<Config | null>;
}

export const defaultConfig = {
  lang: 'en',
  debug: false,
  doneOnboarding: false,
  watchNewDevices: true,
  feedbackSound: true,
  feedbackSoundVolume: 0.5,
  theme: 'slime',
  textSize: 12,
  fonts: ['poppins'],
  advancedAssign: false,
};

function fallbackToDefaults(loadedConfig: any): Config {
  return Object.assign({}, defaultConfig, loadedConfig);
}

export function useConfigProvider(): ConfigContext {
  const [currConfig, set] = useState<Config | null>(null);
  const [loading, setLoading] = useState(false);

  useDebouncedEffect(
    () => {
      if (!currConfig) return;

      localStorage.setItem('config.json', JSON.stringify(currConfig));
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
        const migrated = localStorage.getItem('configMigrated');
        if (!migrated) {
          const oldConfig = await readTextFile('config.json', {
            dir: BaseDirectory.AppConfig,
          }).catch(() => null);

          if (oldConfig) localStorage.setItem('config.json', oldConfig);

          localStorage.setItem('configMigrated', 'true');
        }

        const json = localStorage.getItem('config.json');

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
