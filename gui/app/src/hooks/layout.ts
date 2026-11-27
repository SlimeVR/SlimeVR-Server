import { MutableRefObject, useLayoutEffect, useRef, useState } from 'react';

export function useElemSize<T extends HTMLElement>(
  forwardRef?: MutableRefObject<T | null>
) {
  const innerRef = useRef<T | null>(null);
  const ref = forwardRef || innerRef;
  const [height, setHeight] = useState(0);
  const [width, setWidth] = useState(0);

  const observer = useRef(
    new ResizeObserver((entries) => {
      const { width, height } = entries[0].contentRect;
      setWidth(width);
      setHeight(height);
    })
  );

  useLayoutEffect(() => {
    if (ref.current) {
      observer.current.observe(ref.current);
    }

    return () => {
      if (!ref.current) return;
      observer.current.unobserve(ref.current);
    };
  }, [ref, observer]);

  return {
    ref,
    height,
    width,
  };
}
