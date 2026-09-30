import { ReactNode } from 'react';
import { ConfigContextC, loadConfig, useConfigProvider } from '@/hooks/config';
import { initSentry } from '@/utils/sentry';
import { getDefaultStore } from 'jotai';
import { ErrorReportingConsent } from 'solarxr-protocol';
import { serverErrorReportingAtom } from '@/hooks/error-reporting';

const isSteam = window.electronAPI ? await window.electronAPI.isSteam() : false;
initSentry(
  isSteam,
  () =>
    getDefaultStore().get(serverErrorReportingAtom)?.consent ??
    ErrorReportingConsent.UNDECIDED
);

const config = await loadConfig();

export function ConfigContextProvider({ children }: { children: ReactNode }) {
  const context = useConfigProvider(config);

  return (
    <ConfigContextC.Provider value={context}>
      {children}
    </ConfigContextC.Provider>
  );
}
