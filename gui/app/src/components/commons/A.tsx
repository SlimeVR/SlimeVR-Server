import { openUrl } from '@/hooks/crossplatform';
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
      href={href}
      onClick={(e) => {
        e.preventDefault();
        if (href) openUrl(href);
      }}
      className={classNames(className, 'underline', 'cursor-pointer')}
    >
      {children}
    </a>
  );
}
