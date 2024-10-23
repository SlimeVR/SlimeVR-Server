import { useLayoutEffect } from 'react';
import { useConfig } from './hooks/config';
import { Outlet, useNavigate } from 'react-router-dom';

export function AppLayout() {
  const { loading, config } = useConfig();
  const navigate = useNavigate();

  const r = () => {
    return Math.random() * 255;
  };

  let style = document.getElementById('random-style');

  if (!style) {
    style = document.createElement('style');
    style.id = 'random-style';
    document.getElementsByTagName('head')[0].appendChild(style);
  }

  style.innerHTML = `
    :root[data-theme='random'] {
      --background-10: ${r()}, ${r()}, ${r()};
      --background-20: ${r()}, ${r()}, ${r()};
      --background-30: ${r()}, ${r()}, ${r()};
      --background-40: ${r()}, ${r()}, ${r()};
      --background-50: ${r()}, ${r()}, ${r()};
      --background-60: ${r()}, ${r()}, ${r()};
      --background-70: ${r()}, ${r()}, ${r()};
      --background-80: ${r()}, ${r()}, ${r()};
      --background-90: ${r()}, ${r()}, ${r()};

      --accent-background-10: ${r()}, ${r()}, ${r()};
      --accent-background-20: ${r()}, ${r()}, ${r()};
      --accent-background-30: ${r()}, ${r()}, ${r()};
      --accent-background-40: ${r()}, ${r()}, ${r()};
      --accent-background-50: ${r()}, ${r()}, ${r()};

      --success: ${r()}, ${r()}, ${r()};
      --warning: ${r()}, ${r()}, ${r()};
      --critical: ${r()}, ${r()}, ${r()};
      --special: ${r()}, ${r()}, ${r()};
      --window-icon-stroke: ${r()}, ${r()}, ${r()};

      --default-color: ${r()}, ${r()}, ${r()};
    }
    `;

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
