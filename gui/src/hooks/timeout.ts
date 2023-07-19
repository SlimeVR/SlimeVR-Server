import { useEffect } from 'react';

export function useTimeout(fn: () => void, delay: number) {
  useEffect(() => {
    const id = setTimeout(fn, delay);
    return () => clearTimeout(id);
  });
}

export function useInterval(fn: () => void, delay: number) {
  useEffect(() => {
    const id = setInterval(fn, delay);
    return () => clearInterval(id);
  });
}

export const useDebouncedEffect = (effect: () => void, deps: any[], delay: number) => {
  useEffect(() => {
    const handler = setTimeout(() => effect(), delay);

    return () => clearTimeout(handler);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...(deps || []), delay]);
};


export const debounce = <F extends (...args: any) => any>(
  func: F,
  waitFor: number,
) => {
  let timeout = 0

  const debounced = (...args: any) => {
    window.clearTimeout(timeout)
    timeout = window.setTimeout(() => func(...args), waitFor)
  }

  return debounced as (...args: Parameters<F>) => ReturnType<F>
}
