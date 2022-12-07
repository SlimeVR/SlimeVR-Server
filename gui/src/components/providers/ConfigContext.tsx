import { ReactNode, useEffect } from 'react';
import { ConfigContextC, useConfigProvider } from '../../hooks/config';
import i18next from '../../i18n/config';

export function ConfigContextProvider({ children }: { children: ReactNode }) {
  const context = useConfigProvider();

  useEffect(() => {
    context.loadConfig().then((config) => {
      i18next.changeLanguage(config?.lang || 'en');
    });
  }, []);

  return (
    <ConfigContextC.Provider value={context}>
      {children}
    </ConfigContextC.Provider>
  );
}
