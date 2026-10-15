import { ReactNode } from 'react';
import { ConfigContextC, loadConfig, useConfigProvider } from '@/hooks/config';

const config = await loadConfig();

export function ConfigContextProvider({ children }: { children: ReactNode }) {
  const context = useConfigProvider(config);

  return (
    <ConfigContextC.Provider value={context}>
      {children}
    </ConfigContextC.Provider>
  );
}
