import { ReactChild, useEffect } from 'react';
import { ConfigContextC, useConfigProvider } from '../../hooks/config';

export function ConfigContextProvider({ children }: { children: ReactChild }) {
  const context = useConfigProvider();

  useEffect(() => {
    context.loadConfig();
  }, []);

  return (
    <ConfigContextC.Provider value={context}>
      {children}
    </ConfigContextC.Provider>
  );
}
