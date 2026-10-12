import classNames from 'classnames';
import { ReactNode } from 'react';
import { Typography } from '@/components/commons/Typography';

export function SettingsPagePaneLayout({
  children,
  className,
  icon,
  title,
  ...props
}: {
  children: ReactNode;
  icon: ReactNode;
  title?: string;
} & React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={classNames(
        'bg-background-70 rounded-lg p-3 xs:p-4 pt-10 xs:pt-4 pb-6 xs:pb-8 flex flex-col gap-4 w-full relative',
        className
      )}
      {...props}
    >
      <div className="flex items-center gap-4">
        <div className="w-10 h-10 flex-none bg-accent-background-40 flex justify-center items-center rounded-full fill-background-10">
          {icon}
        </div>
        {title && <Typography variant="main-title" id={title} />}
      </div>
      <div className="flex-col w-full min-w-0">{children}</div>
    </div>
  );
}
