import { open } from '@tauri-apps/plugin-shell';
import classNames from 'classnames';
import { ReactNode } from 'react';

export function A({
  href,
  children,
  className,
}: {
  href?: string;
  children?: ReactNode;
  className?: string;
}) {
  return (
    <a
      href="javascript:void(0)"
      onClick={() =>
        href && open(href).catch(() => window.open(href, '_blank'))
      }
      className={classNames(className, 'underline', 'cursor-pointer')}
    >
      {children}
    </a>
  );
}
