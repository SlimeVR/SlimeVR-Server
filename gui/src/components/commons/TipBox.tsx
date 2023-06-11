import { ReactNode } from 'react';
import { BulbIcon } from './icon/BulbIcon';
import { WarningIcon } from './icon/WarningIcon';
import { Typography } from './Typography';
import classNames from 'classnames';

export function TipBox({
  children,
  hideIcon = false,
  whitespace = false,
}: {
  children: ReactNode;
  hideIcon?: boolean;
  whitespace?: boolean;
}) {
  return (
    <div className="flex flex-row gap-4 bg-accent-background-50 p-4 rounded-md">
      <div
        className={classNames(
          'fill-accent-background-20 flex flex-col justify-center',
          hideIcon && 'hidden'
        )}
      >
        <BulbIcon></BulbIcon>
      </div>
      <div className="flex flex-col">
        <Typography
          color="text-accent-background-10"
          whitespace={whitespace ? 'whitespace-pre' : undefined}
        >
          {children}
        </Typography>
      </div>
    </div>
  );
}

/**
 * Will respect new lines and spacing given in text
 */
export function WarningBox({
  children,
  whitespace = true,
  hideIcon = false,
}: {
  children: ReactNode;
  whitespace?: boolean;
  hideIcon?: boolean;
}) {
  return (
    <div className="flex flex-row gap-4 bg-status-warning p-4 rounded-md">
      <div
        className={classNames(
          'text-background-60 flex flex-col justify-center',
          hideIcon && 'hidden'
        )}
      >
        <WarningIcon></WarningIcon>
      </div>
      <div className="flex flex-col">
        <Typography
          color="text-background-60"
          whitespace={whitespace ? 'whitespace-pre' : undefined}
        >
          {children}
        </Typography>
      </div>
    </div>
  );
}
