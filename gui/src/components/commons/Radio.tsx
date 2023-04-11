import classNames from 'classnames';
import { Control, Controller } from 'react-hook-form';
import { Typography } from './Typography';

export function Radio({
  control,
  name,
  label,
  value,
  desciption,
  // input props
  disabled,
  ...props
}: {
  control: Control<any>;
  name: string;
  label: string;
  value: string | number;
  desciption?: string | null;
} & React.HTMLProps<HTMLInputElement>) {
  return (
    <Controller
      control={control}
      name={name}
      render={({ field: { onChange, ref, name, value: checked } }) => (
        <label
          className={classNames(
            'w-full bg-background-60 p-3 rounded-md flex gap-3 border-2',
            {
              'border-accent-background-30': value == checked,
              'border-transparent': value != checked,
              'cursor-pointer': !disabled,
            }
          )}
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
            checked={value == checked}
            {...props}
          />
          <div className="flex flex-col gap-2">
            <Typography bold>{label}</Typography>
            {desciption && (
              <Typography variant="standard" color="secondary">
                {desciption}
              </Typography>
            )}
          </div>
        </label>
      )}
    />
  );
}
