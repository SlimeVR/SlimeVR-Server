import { openUrl } from '@tauri-apps/plugin-opener';
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
        href && openUrl(href).catch(() => window.open(href, '_blank'))
      }
      className={classNames(className, { underline: underline })}
    >
      {children}
    </a>
  );
}
