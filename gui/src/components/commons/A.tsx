import { open } from '@tauri-apps/plugin-shell';
import { ReactNode } from 'react';
import classNames from 'classnames';

export function A({
  href,
  children,
  className,
  underline = false,
}: {
  href?: string;
  children?: ReactNode;
  className?: string;
  underline?: boolean;
}) {
  return (
    <a
      href="javascript:void(0)"
      onClick={() =>
        href && open(href).catch(() => window.open(href, '_blank'))
      }
      className={classNames(className, { underline: underline })}
    >
      {children}
    </a>
  );
}
