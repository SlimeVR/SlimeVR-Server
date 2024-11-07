import classNames from 'classnames';
import { useMemo } from 'react';
import { Control, Controller } from 'react-hook-form';

export const CHECKBOX_CLASSES = classNames(
  'bg-background-50 border-background-50 rounded-md w-5 h-5 text-accent-background-30 focus:border-accent-background-40 focus:ring-transparent focus:ring-offset-transparent focus:outline-transparent'
);

export function CheckBox({
  label,
  variant = 'checkbox',
  color = 'primary',
  control,
  outlined,
  name,
  loading,
  // input props
  disabled,
  ...props
}: {
  label: string;
  control: Control<any>;
  name: string;
  variant?: 'checkbox' | 'toggle';
  color?: 'primary' | 'secondary' | 'tertiary';
  outlined?: boolean;
  loading?: boolean;
} & React.HTMLProps<HTMLInputElement>) {
  const classes = useMemo(() => {
    const vriantsMap = {
      checkbox: {
        checkbox: CHECKBOX_CLASSES,
        toggle: '',
        pin: '',
      },
      toggle: {
        checkbox: classNames('hidden'),
        toggle: classNames('w-10 h-4 rounded-full relative transition-colors'),
        pin: classNames(
          'h-2 w-2 bg-background-10 rounded-full absolute m-1 transition-opacity'
        ),
      },
    };
    return vriantsMap[variant];
  }, [variant]);

  return (
    <Controller
      control={control}
      name={name}
      render={({ field: { onChange, value, ref, name } }) => (
        <div
          className={classNames(
            {
              'rounded-lg': outlined,
              'text-background-30': !outlined || disabled,
              'bg-background-60': outlined && color === 'primary',
              'bg-background-70': outlined && color === 'secondary',
              'bg-background-50': outlined && color === 'tertiary',
            },
            'flex items-center gap-2 w-full'
          )}
        >
          <label
            className={classNames(
              'w-full py-3 flex gap-2 items-center text-standard-bold',
              {
                'px-3': outlined,
                'cursor-pointer': !disabled || !loading,
                'cursor-default': disabled || loading,
              }
            )}
          >
            <input
              ref={ref}
              onChange={onChange}
              checked={value || false}
              name={name}
              className={classes.checkbox}
              type="checkbox"
              disabled={disabled || loading}
              {...props}
            />
            {variant === 'toggle' && (
              <div
                className={classNames(classes.toggle, {
                  'bg-accent-background-30': value && !disabled && !loading,
                  'bg-accent-background-50': value && disabled,
                  'bg-accent-background-30 animate-pulse': loading && !disabled,
                  'bg-background-50':
                    ((!value && color == 'primary') || color == 'secondary') &&
                    !loading,
                  'bg-background-40': !value && color == 'tertiary' && !loading,
                })}
              >
                <div
                  className={classNames(classes.pin, {
                    'left-0': !value && !loading,
                    'opacity-0': loading,
                    'right-0': value && !loading,
                    'bg-background-30': disabled,
                  })}
                ></div>
              </div>
            )}
            {label}
          </label>
        </div>
      )}
    />
  );
}
