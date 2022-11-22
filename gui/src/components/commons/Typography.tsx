import classNames from 'classnames';
import { createElement, ReactNode, useMemo } from 'react';

export function Typography({
  variant = 'standard',
  bold = false,
  color = 'primary',
  children,
}: {
  variant?: 'main-title' | 'section-title' | 'standard' | 'vr-accessible';
  bold?: boolean;
  block?: boolean;
  color?: 'primary' | 'secondary' | string;
  children: ReactNode;
}) {
  const tag = useMemo(() => {
    const tags = {
      'main-title': 'h1',
      'section-title': 'h2',
      standard: 'p',
      'vr-accessible': 'p',
    };
    return tags[variant];
  }, [variant]);

  return createElement(
    tag,
    {
      className: classNames([
        variant === 'main-title' && 'text-main-title',
        variant === 'section-title' && 'text-section-title',
        variant === 'standard' &&
          (bold ? 'text-standard-bold' : 'text-standard'),
        variant === 'vr-accessible' &&
          (bold ? 'text-vr-accesible-bold' : 'text-vr-accesible'),
        color === 'primary' && 'text-background-10',
        color === 'secondary' && 'text-background-30',
        typeof color === 'string' && color,
      ]),
    },
    children
  );
}
