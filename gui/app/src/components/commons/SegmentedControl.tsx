import classNames from 'classnames';
import { ReactNode } from 'react';
import { Clickable } from './Clickable';
import { Typography } from './Typography';

export interface SegmentedOption<T> {
  value: T;
  labelId?: string;
  vars?: Record<string, string | number>;
  label?: ReactNode;
  dotClass?: string;
}

const variantClasses = {
  primary: { track: 'bg-background-80', active: 'bg-accent-background-30' },
  secondary: { track: 'bg-background-80', active: 'bg-background-60' },
  tertiary: { track: 'bg-background-70', active: 'bg-accent-background-30' },
};

export function SegmentedControl<T extends string | number>({
  value,
  onChange,
  options,
  variant = 'primary',
  className,
}: {
  value: T;
  onChange: (value: T) => void;
  options: SegmentedOption<T>[];
  variant?: keyof typeof variantClasses;
  className?: string;
}) {
  return (
    <div
      className={classNames(
        'flex flex-wrap items-stretch gap-0.5 p-1 rounded-md min-h-[48px] min-w-0',
        variantClasses[variant].track,
        className
      )}
    >
      {options.map((option) => {
        const active = option.value === value;
        return (
          <Clickable
            key={String(option.value)}
            pressed={active}
            onClick={() => onChange(option.value)}
            className={classNames(
              'flex items-center gap-1.5 px-3.5 py-1.5 rounded min-w-0 transition-colors',
              active
                ? classNames(
                    variantClasses[variant].active,
                    'text-background-10'
                  )
                : 'text-background-20 hover:text-background-10'
            )}
          >
            {option.dotClass && (
              <span
                className={classNames(
                  'w-2 h-2 rounded-full shrink-0',
                  option.dotClass
                )}
              />
            )}
            {option.labelId ? (
              <Typography
                tag="span"
                bold
                color="text-inherit"
                id={option.labelId}
                vars={option.vars}
              />
            ) : (
              <span className="text-standard-bold min-w-0">{option.label}</span>
            )}
          </Clickable>
        );
      })}
    </div>
  );
}
