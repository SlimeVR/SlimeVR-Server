import classNames from 'classnames';
import { ReactNode } from 'react';
import { LoaderIcon, SlimeState } from './icon/LoaderIcon';
import { Typography } from './Typography';

/** Slime, a title and a line of guidance for a panel with nothing in it */
export function EmptyState({
  slimeState,
  iconSize,
  glow = false,
  lowPriority = false,
  titleId,
  titleColor,
  descriptionId,
  descriptionColor,
  action,
  className,
}: {
  slimeState: SlimeState;
  iconSize?: number;
  /** Accent halo behind the slime */
  glow?: boolean;
  lowPriority?: boolean;
  titleId: string;
  titleColor?: string;
  descriptionId: string;
  descriptionColor?: string;
  action?: ReactNode;
  className?: string;
}) {
  const icon = (
    <LoaderIcon
      slimeState={slimeState}
      size={iconSize}
      lowPriority={lowPriority}
    />
  );

  return (
    <div
      className={classNames(
        'flex flex-col items-center justify-center gap-4 text-center p-6',
        className
      )}
    >
      {glow ? (
        <div className="relative flex items-center justify-center antialiased">
          <div
            className="absolute w-64 h-64 rounded-full pointer-events-none"
            style={{
              background:
                'radial-gradient(circle, rgb(var(--accent-background-30), 0.25) 0%, rgb(var(--accent-background-30), 0) 70%)',
            }}
          />
          {icon}
        </div>
      ) : (
        icon
      )}
      <div className="flex flex-col gap-1 max-w-xs">
        <Typography
          bold
          variant="section-title"
          color={titleColor}
          id={titleId}
        />
        <Typography
          variant="standard"
          color={descriptionColor}
          id={descriptionId}
        />
      </div>
      {action}
    </div>
  );
}
