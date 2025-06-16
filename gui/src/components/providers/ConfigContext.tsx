import { ReactNode, useContext, useLayoutEffect } from 'react';
import { ConfigContextC, loadConfig, useConfigProvider } from '@/hooks/config';
import { DEFAULT_LOCALE, LangContext } from '@/i18n/config';
import { getSentryOrCompute } from '@/utils/sentry';

const config = await loadConfig();

if (config?.errorTracking !== undefined) {
  // load sentry ASAP to catch early errors
  getSentryOrCompute(config.errorTracking ?? false);
}

export function ConfigContextProvider({ children }: { children: ReactNode }) {
  const context = useConfigProvider(config);
  const { changeLocales } = useContext(LangContext);

  useLayoutEffect(() => {
    changeLocales([config?.lang || DEFAULT_LOCALE]);
  }, []);

  useLayoutEffect(() => {
    if (config?.errorTracking !== undefined) {
      // Alows for sentry to refresh if user change the setting once the gui
      // is initialized
      getSentryOrCompute(config.errorTracking ?? false);
    }
  }, [config?.errorTracking]);

  return (
    <ConfigContextC.Provider value={context}>
      {children}
    </ConfigContextC.Provider>
  );
}
