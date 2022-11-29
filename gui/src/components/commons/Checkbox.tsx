import classNames from 'classnames';
import { ReactChild, useMemo } from 'react';
import { Control, Controller } from 'react-hook-form';

export function CheckBox({
  label,
  variant = 'checkbox',
  control,
  outlined,
  name,
  // input props
  disabled,
  ...props
}: {
  label: string | ReactChild;
  control: Control<any>;
  name: string;
  variant?: 'checkbox' | 'toggle';
  outlined?: boolean;
} & React.HTMLProps<HTMLInputElement>) {
  const classes = useMemo(() => {
    const vriantsMap = {
      checkbox: {
        checkbox: classNames(
          'bg-background-50 border-background-50 rounded-md w-5 h-5 text-accent-background-30 focus:border-accent-background-40 focus:ring-transparent focus:ring-offset-transparent focus:outline-transparent'
        ),
        toggle: '',
        pin: '',
      },
      toggle: {
        checkbox: classNames('hidden'),
        toggle: classNames('w-10 h-4 rounded-full relative transition-colors'),
        pin: classNames('h-2 w-2 bg-background-10 rounded-full absolute m-1'),
      },
    };
    return vriantsMap[variant];
  }, [variant]);

  return (
    <Controller
      control={control}
      name={name}
      render={({ field: { onChange, value, ref, onBlur, name } }) => (
        <div
          className={classNames(
            {
              'bg-background-60 rounded-lg text-white': outlined,
              'text-background-30': !outlined,
            },
            'flex items-center gap-2 w-full'
          )}
        >
          <label
            className={classNames(
              'w-full py-3 flex gap-2 items-center text-standard-bold',
              {
                'px-3': outlined,
                'cursor-pointer': !disabled,
              }
            )}
          >
            <input
              ref={ref}
              onChange={onChange}
              checked={value}
              name={name}
              className={classes.checkbox}
              type="checkbox"
              {...props}
            />
            {variant === 'toggle' && (
              <div
                className={classNames(classes.toggle, {
                  'bg-accent-background-30': value,
                  'bg-background-50': !value,
                })}
              >
                <div
                  className={classNames(classes.pin, {
                    'left-0': !value,
                    'right-0': value,
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
