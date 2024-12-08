import resolveConfig from 'tailwindcss/resolveConfig';
import { useMediaQuery } from 'react-responsive';
import tailwindConfig from '../../tailwind.config';

const fullConfig = resolveConfig(tailwindConfig as any);

type BreakpointKey = keyof typeof tailwindConfig.theme.screens;

export function useBreakpoint<K extends BreakpointKey>(breakpointKey: K) {
  // FIXME There is a flickering issue caused by this, because isMobile is not resolved fast enough
  // one solution would be to have this solved only once on the appProvider and reuse the value all the time
  const bool = useMediaQuery({
    query: fullConfig.theme.screens[breakpointKey].raw
      ? fullConfig.theme.screens[breakpointKey].raw
      : `(min-width: ${fullConfig.theme.screens[breakpointKey]})`,
  });
  const capitalizedKey =
    breakpointKey.toString()[0].toUpperCase() + breakpointKey.toString().substring(1);
  type Key = `is${Capitalize<K>}`;
  return {
    [`is${capitalizedKey}`]: bool,
  } as Record<Key, boolean>;
}

export function useIsTauri() {
  return window.isTauri;
}
