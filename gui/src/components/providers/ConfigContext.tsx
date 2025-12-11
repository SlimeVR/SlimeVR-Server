import { ReactNode } from 'react';
import { ConfigContextC, loadConfig, useConfigProvider } from '@/hooks/config';
import { getSentryOrCompute } from '@/utils/sentry';

const config = await loadConfig();

if (config?.errorTracking !== undefined) {
  // load sentry ASAP to catch early errors
  getSentryOrCompute(config.errorTracking ?? false, config.uuid);
}

export function ConfigContextProvider({ children }: { children: ReactNode }) {
  const context = useConfigProvider(config);

  return (
    <ConfigContextC.Provider value={context}>
      {children}
    </ConfigContextC.Provider>
  );
}
