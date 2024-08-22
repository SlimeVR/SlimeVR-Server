import { useEffect, useLayoutEffect } from 'react';
import { useConfig } from './hooks/config';
import { Outlet, useNavigate } from 'react-router-dom';
import { getSentryOrCompute } from './utils/sentry';
import { log } from './utils/logging';

export function AppLayout() {
  const { loading, config } = useConfig();
  const navigate = useNavigate();

  useLayoutEffect(() => {
    if (loading || !config) return;
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
  }, [config, loading]);

  useLayoutEffect(() => {
    if (config && !config.doneOnboarding) {
      navigate('/onboarding/home');
    }
  }, [config?.doneOnboarding]);

  useEffect(() => {
    if (config?.dataCollection !== undefined) {
      log(
        `${config.dataCollection ? 'Enabled' : 'Disabled'} error logging with Sentry.`
      );
      getSentryOrCompute(config.dataCollection);
    }
  }, [config?.dataCollection]);

  // const location = useLocation();
  // const navigationType = useNavigationType();
  // useEffect(() => {
  //   if (import.meta.env.PROD) return;
  //   console.log('The current URL is', { ...location });
  //   console.log('The last navigation action was', navigationType);
  // }, [location, navigationType]);

  if (loading) return <></>;

  return (
    <>
      <Outlet />
    </>
  );
}
