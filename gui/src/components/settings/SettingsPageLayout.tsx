import classNames from 'classnames';
import { ReactChild } from 'react';

export function SettingsPageLayout({
  children,
  className,
  icon,
  ...props
}: {
  children: ReactChild;
  icon: ReactChild;
} & React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={classNames(
        'bg-background-70 rounded-lg p-8 flex gap-8 w-full',
        className
      )}
      {...props}
    >
      <div className="flex">
        <div className="w-10 h-10 bg-accent-background-40 flex justify-center items-center rounded-full fill-background-10">
          {icon}
        </div>
      </div>
      <div className="flex-col w-full">{children}</div>
    </div>
  );
}
