import { MutableRefObject, useLayoutEffect, useRef, useState } from 'react';

export function useLayout<T extends HTMLElement>() {
  const [layoutHeight, setLayoutHeigt] = useState(window.innerHeight);
  const [layoutWidth, setLayoutWidth] = useState(window.innerWidth);
  const ref = useRef<T | null>(null);

  const computeLayoutHeight = (windowHeight: number, windowWidth: number) => {
    if (ref.current) {
      setLayoutHeigt(windowHeight - ref.current.getBoundingClientRect().top);
      setLayoutWidth(windowWidth - ref.current.getBoundingClientRect().left);
    }
  };

  const onWindowResize = () => {
    computeLayoutHeight(window.innerHeight, window.innerWidth);
  };

  useLayoutEffect(() => {
    window.addEventListener('resize', onWindowResize);
    // Fix a bug where the layout would not update when the window is unfocused then focused again
    window.addEventListener('focus', onWindowResize);
    computeLayoutHeight(window.innerHeight, window.innerWidth);
    return () => {
      window.removeEventListener('focus', onWindowResize);
      window.removeEventListener('resize', onWindowResize);
    };
  });

  return {
    layoutHeight,
    layoutWidth,
    ref,
  };
}

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
