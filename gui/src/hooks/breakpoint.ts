import resolveConfig from 'tailwindcss/resolveConfig';
import { useMediaQuery } from 'react-responsive';
import tailwindConfig from '../../tailwind.config';

const fullConfig = resolveConfig(tailwindConfig as any);
const breakpoints = tailwindConfig.theme.screens;

type BreakpointKey = keyof typeof breakpoints;

export function useBreakpoint<K extends BreakpointKey>(breakpointKey: K) {
  const bool = useMediaQuery({
    query: fullConfig.theme.screens[breakpointKey].raw ? fullConfig.theme.screens[breakpointKey].raw : `(min-width: ${fullConfig.theme.screens[breakpointKey]})`,
  });
  const capitalizedKey = breakpointKey.toString()[0].toUpperCase() + breakpointKey.toString().substring(1);
  type Key = `is${Capitalize<K>}`;
  return {
    [`is${capitalizedKey}`]: bool,
  } as Record<Key, boolean>;
}

export function useIsTauri() {
  return !!window.__TAURI_METADATA__
}
