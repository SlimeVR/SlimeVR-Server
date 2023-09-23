import { ReactNode, useEffect, useContext } from 'react';
import { ConfigContextC, useConfigProvider } from '@/hooks/config';
import { DEFAULT_LOCALE, LangContext } from '@/i18n/config';

export function ConfigContextProvider({ children }: { children: ReactNode }) {
  const context = useConfigProvider();
  const { changeLocales } = useContext(LangContext);

  useEffect(() => {
    context.loadConfig().then((config) => {
      changeLocales([config?.lang || DEFAULT_LOCALE]);
    });
  }, []);

  return (
    <ConfigContextC.Provider value={context}>
      {children}
    </ConfigContextC.Provider>
  );
}
