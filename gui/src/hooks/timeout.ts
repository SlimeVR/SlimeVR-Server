import { useEffect, useState } from 'react';

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
