import { useEffect, useLayoutEffect, useRef } from 'react';

export function useTimeout(fn: () => void, delay: number | null) {
  const saved = useRef(fn);

  useLayoutEffect(() => {
    saved.current = fn;
  }, [fn]);

  useEffect(() => {
    if (delay === null) return;
    const id = setTimeout(() => saved.current(), delay);
    return () => clearTimeout(id);
  }, [delay]);
}

export function useInterval(fn: () => void, delay: number | null) {
  const saved = useRef(fn);

  useLayoutEffect(() => {
    saved.current = fn;
  }, [fn]);

  useEffect(() => {
    if (delay === null) return;
    const id = setInterval(() => saved.current(), delay);
    return () => clearInterval(id);
  }, [delay]);
}

export const useDebouncedEffect = (effect: () => void, deps: any[], delay: number) => {
  useEffect(() => {
    const handler = setTimeout(() => effect(), delay);

    return () => clearTimeout(handler);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...(deps || []), delay]);
};

export const debounce = <F extends (...args: any) => any>(func: F, waitFor: number) => {
  let timeout = 0;

  const debounced = (...args: any) => {
    window.clearTimeout(timeout);
    timeout = window.setTimeout(() => func(...args), waitFor);
  };

  return debounced as (...args: Parameters<F>) => ReturnType<F>;
};
