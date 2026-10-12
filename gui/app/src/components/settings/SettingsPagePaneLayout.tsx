import classNames from 'classnames';
import { ReactNode } from 'react';

export function SettingsPagePaneLayout({
  children,
  className,
  icon,
  ...props
}: {
  children: ReactNode;
  icon: ReactNode;
} & React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={classNames(
        'bg-background-70 rounded-lg px-4 py-8 flex xs:gap-4 w-full relative',
        className
      )}
      {...props}
    >
      <div className="flex mobile:absolute mobile:right-4">
        <div className="w-10 h-10 bg-accent-background-40 flex justify-center items-center rounded-full fill-background-10">
          {icon}
        </div>
      </div>
      <div className="flex-col w-full min-w-0">{children}</div>
    </div>
  );
}
