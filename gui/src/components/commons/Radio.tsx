import classNames from 'classnames';
import { Control, Controller } from 'react-hook-form';
import { Typography } from './Typography';
import { ReactNode } from 'react';

export function Radio({
  control,
  name,
  label,
  value,
  description,
  children,
  // input props
  disabled,
  ...props
}: {
  control: Control<any>;
  name: string;
  label?: string;
  value: string;
  description?: string | null;
  children?: ReactNode;
} & React.HTMLProps<HTMLInputElement>) {
  return (
    <Controller
      control={control}
      name={name}
      render={({ field: { onChange, ref, name, value: checked } }) => (
        <label
          className={classNames('w-full p-3 rounded-md flex gap-3 border-2', {
            'border-accent-background-30': value == checked,
            'border-transparent': value != checked,
            'bg-background-60 cursor-pointer hover:bg-background-50': !disabled,
            'bg-background-80 cursor-not-allowed': disabled,
          })}
        >
          <input
            type="radio"
            className={classNames(
              'text-accent-background-30 focus:ring-transparent',
              'focus:ring-offset-transparent focus:outline-transparent'
            )}
            name={name}
            ref={ref}
            onChange={onChange}
            value={value}
            disabled={disabled}
            checked={value == checked}
            {...props}
          />
          <div className="flex flex-col gap-2 pointer-events-none">
            {children ? children : <Typography bold>{label}</Typography>}
            {description && (
              <Typography variant="standard" color="secondary">
                {description}
              </Typography>
            )}
          </div>
        </label>
      )}
    />
  );
}
