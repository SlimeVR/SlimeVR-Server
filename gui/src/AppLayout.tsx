import { useLayoutEffect } from 'react';
import { useConfig } from './hooks/config';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';

export function AppLayout() {
  const { config } = useConfig();
  const { pathname } = useLocation()
  const navigate = useNavigate();

  useLayoutEffect(() => {
    if (!config) return;
    if (config.theme !== undefined) {
      document.documentElement.dataset.theme = config.theme;
    }

    if (config.fonts !== undefined) {
      document.documentElement.style.setProperty(
        '--font-name',
        config.fonts.map((x) => `"${x}"`).join(',')
      );
    }

    if (config.textSize !== undefined) {
      document.documentElement.style.setProperty(
        '--font-size',
        `${config.textSize}rem`
      );
    }
  }, [config]);

  useLayoutEffect(() => {
    if (config && !config.doneOnboarding && !pathname.startsWith('/onboarding/')) {
      navigate('/onboarding/home');
    }
  }, [config]);

  return (
    <>
      <Outlet />
    </>
  );
}
