import { ReactNode } from 'react';
import { BulbIcon } from './icon/BulbIcon';
import { WarningIcon } from './icon/WarningIcon';
import { Typography } from './Typography';
import classNames from 'classnames';
import { QuestionIcon } from './icon/QuestionIcon';

export function TipBox({
  children,
  hideIcon = false,
  whitespace = false,
  className,
}: {
  children: ReactNode;
  hideIcon?: boolean;
  whitespace?: boolean;
  className?: string;
}) {
  return (
    <div
      className={classNames(
        'flex flex-row gap-4 bg-accent-background-50 p-4 rounded-md',
        className
      )}
    >
      <div
        className={classNames(
          'fill-accent-background-20 flex flex-col justify-center',
          hideIcon && 'hidden'
        )}
      >
        <BulbIcon></BulbIcon>
      </div>
      <div className="flex flex-col justify-center">
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

export function QuestionBox({
  children,
  hideIcon = false,
  whitespace = false,
}: {
  children: ReactNode;
  hideIcon?: boolean;
  whitespace?: boolean;
}) {
  return (
    <div className="flex flex-row gap-4 bg-trans-blue-400 p-4 rounded-md">
      <div
        className={classNames(
          'stroke-background-90 flex flex-col justify-center',
          hideIcon && 'hidden'
        )}
      >
        <QuestionIcon width={whitespace ? 36 : 24}></QuestionIcon>
      </div>
      <div className="flex flex-col justify-center">
        <Typography
          color="text-background-90"
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
      <div className="flex flex-col justify-center">
        <Typography
          color="text-background-60"
          whitespace={whitespace ? 'whitespace-pre-line' : undefined}
        >
          {children}
        </Typography>
      </div>
    </div>
  );
}
