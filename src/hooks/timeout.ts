import { useEffect } from 'react';

export const useTimeout = (fn: () => void, delay: number) => {
  useEffect(() => {
    const id = setTimeout(fn, delay);
    return () => clearTimeout(id);
  });
};

export const useInterval = (fn: () => void, delay: number) => {
  useEffect(() => {
    const id = setInterval(fn, delay);
    return () => clearInterval(id);
  });
};