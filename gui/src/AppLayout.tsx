import { useLayoutEffect } from 'react';
import { useConfig } from './hooks/config';
import { Outlet, useNavigate } from 'react-router-dom';

function hslToRGB(h: number, s: number, v: number): [number, number, number] {
    var r, g, b, i, f, p, q, t;
    i = Math.floor(h * 6);
    f = h * 6 - i;
    p = v * (1 - s);
    q = v * (1 - f * s);
    t = v * (1 - (1 - f) * s);
    switch (i % 6) {
        case 0: r = v, g = t, b = p; break;
        case 1: r = q, g = v, b = p; break;
        case 2: r = p, g = v, b = t; break;
        case 3: r = p, g = q, b = v; break;
        case 4: r = t, g = p, b = v; break;
        case 5: r = v, g = p, b = q; break;
    }
    return [
        Math.round(r * 255),
        Math.round(g * 255),
        Math.round(b * 255)
    ];
}

export function AppLayout() {
  const { config } = useConfig();
  const navigate = useNavigate();

  useLayoutEffect(() => {
    if (!config) return;
    if (config.theme !== undefined) {
      document.documentElement.dataset.theme = config.theme;
      if (config.theme == 'custom-bright') {
        console.log(config.customHue);
        console.log(hslToRGB(config.customHue,1,1));
        document.documentElement.style.setProperty('--background-20', hslToRGB(config.customHue, 0.06, 0.91).join(','));
        document.documentElement.style.setProperty('--background-30', hslToRGB(config.customHue, 0.06, 0.83).join(','));
        document.documentElement.style.setProperty('--background-40', hslToRGB(config.customHue, 0.10, 0.61).join(','));
        document.documentElement.style.setProperty('--background-50', hslToRGB(config.customHue, 0.21, 0.38).join(','));
        document.documentElement.style.setProperty('--background-60', hslToRGB(config.customHue, 0.25, 0.28).join(','));
        document.documentElement.style.setProperty('--background-70', hslToRGB(config.customHue, 0.27, 0.22).join(','));
        document.documentElement.style.setProperty('--background-80', hslToRGB(config.customHue, 0.29, 0.15).join(','));
      }
      else {
        document.documentElement.style.setProperty('--background-20', null);
        document.documentElement.style.setProperty('--background-30', null);
        document.documentElement.style.setProperty('--background-40', null);
        document.documentElement.style.setProperty('--background-50', null);
        document.documentElement.style.setProperty('--background-60', null);
        document.documentElement.style.setProperty('--background-70', null);
        document.documentElement.style.setProperty('--background-80', null);
      }
      if (config.theme == 'custom-bright' || config.theme == 'custom-dark') {
        document.documentElement.style.setProperty('--accent-background-10', hslToRGB(config.customHue, 0.20, 1).join(','));
        document.documentElement.style.setProperty('--accent-background-20', hslToRGB(config.customHue, 0.51, 0.92).join(','));
        document.documentElement.style.setProperty('--accent-background-30', hslToRGB(config.customHue, 0.62, 0.72).join(','));
        document.documentElement.style.setProperty('--accent-background-40', hslToRGB(config.customHue, 0.62, 0.56).join(','));
        document.documentElement.style.setProperty('--accent-background-50', hslToRGB(config.customHue, 0.70, 0.36).join(','));
        document.documentElement.style.setProperty('--window-icon-stroke', hslToRGB(config.customHue, 0.43, 0.92).join(','));
      }
      else {
        document.documentElement.style.setProperty('--accent-background-10', null);
        document.documentElement.style.setProperty('--accent-background-20', null);
        document.documentElement.style.setProperty('--accent-background-30', null);
        document.documentElement.style.setProperty('--accent-background-40', null);
        document.documentElement.style.setProperty('--accent-background-50', null);
        document.documentElement.style.setProperty('--window-icon-stroke', null);
      }
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
