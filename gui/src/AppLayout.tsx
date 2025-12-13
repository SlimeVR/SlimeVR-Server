import { useLayoutEffect } from 'react';
import { useConfig } from './hooks/config';
import { Outlet, useNavigate } from 'react-router-dom';
import { applyColors } from './components/commons/CustomThemeColors';

export function AppLayout() {
  const { config } = useConfig();
  const navigate = useNavigate();

  useLayoutEffect(() => {
    if (!config) return;
    if (config.theme !== undefined) {
      document.documentElement.dataset.theme = config.theme;
      applyColors(config.theme, config.customHue);
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
    if (config && !config.doneOnboarding) {
      navigate('/onboarding/home');
    }
  }, [config?.doneOnboarding]);

  return (
    <>
      <Outlet />
    </>
  );
}
