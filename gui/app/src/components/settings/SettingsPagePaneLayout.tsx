import classNames from 'classnames';
import { ReactNode } from 'react';
import { Typography } from '@/components/commons/Typography';

export function SettingsPagePaneLayout({
  children,
  className,
  contentClassName,
  icon,
  title,
  actions,
  hideTitleOnMobile = false,
  ...props
}: {
  children: ReactNode;
  icon: ReactNode;
  title?: string;
  actions?: ReactNode;
  hideTitleOnMobile?: boolean;
  contentClassName?: string;
} & React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={classNames(
        'bg-background-70 rounded-lg p-3 xs:p-4 pt-10 xs:pt-4 pb-6 xs:pb-8 flex flex-col gap-4 w-full relative',
        className
      )}
      {...props}
    >
      <div className="flex flex-wrap items-center gap-4">
        <div
          className={classNames(
            'w-10 h-10 flex-none bg-accent-background-40 flex justify-center items-center rounded-full fill-background-10',
            hideTitleOnMobile && 'mobile:hidden'
          )}
        >
          {icon}
        </div>
        {title && (
          <div className={classNames(hideTitleOnMobile && 'mobile:hidden')}>
            <Typography variant="main-title" id={title} />
          </div>
        )}
        {actions && (
          <div className="flex flex-grow flex-wrap items-center gap-2">
            {actions}
          </div>
        )}
      </div>
      <div className={classNames('flex-col w-full min-w-0', contentClassName)}>
        {children}
      </div>
    </div>
  );
}
