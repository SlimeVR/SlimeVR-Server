import { useEffect, useLayoutEffect, useState } from 'react';
import { useConfig } from './hooks/config';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { Helmet } from 'react-helmet';
import randomColor from 'randomcolor';

export function AppLayout() {
  const { config } = useConfig();
  const navigate = useNavigate();
  const location = useLocation();
  const [colors, setColors] = useState<string[] | null>();

  useEffect(() => {
    setColors(
      [
        (
          randomColor({
            format: 'rgbArray',
            count: 9,
            luminosity: 'random',
            hue: Math.floor(Math.random() * 360),
          }) as unknown as number[][]
        ).map((x) => x.join(',')),
        (
          randomColor({
            format: 'rgbArray',
            count: 5,
            luminosity: 'random',
            hue: Math.floor(Math.random() * 360),
          }) as unknown as number[][]
        ).map((x) => x.join(',')),
        (
          randomColor({
            format: 'rgbArray',
            count: 6,
            luminosity: 'random',
          }) as unknown as number[][]
        ).map((x) => x.join(',')),
      ].flat()
    );
  }, [location]);

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
    if (config && !config.doneOnboarding) {
      navigate('/onboarding/home');
    }
  }, [config?.doneOnboarding]);

  return (
    <>
      <Helmet>
        <style>
          {colors &&
            `
    :root[data-theme='random'] {
      --background-10: ${colors[0]};
      --background-20: ${colors[1]};
      --background-30: ${colors[2]};
      --background-40: ${colors[3]};
      --background-50: ${colors[4]};
      --background-60: ${colors[5]};
      --background-70: ${colors[6]};
      --background-80: ${colors[7]};
      --background-90: ${colors[8]};

      --accent-background-10: ${colors[9]};
      --accent-background-20: ${colors[10]};
      --accent-background-30: ${colors[11]};
      --accent-background-40: ${colors[12]};
      --accent-background-50: ${colors[13]};

      --success: ${colors[14]};
      --warning: ${colors[15]};
      --critical: ${colors[16]};
      --special: ${colors[17]};
      --window-icon-stroke: ${colors[18]};

      --default-color: ${colors[19]};
    }
    `}
        </style>
      </Helmet>
      <Outlet />
    </>
  );
}
