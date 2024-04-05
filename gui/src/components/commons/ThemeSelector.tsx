import classNames from 'classnames';
import { Control, Controller } from 'react-hook-form';

export function ThemeSelector({
  control,
  name,
  value,
  // input props
  disabled,
  colors,
  ...props
}: {
  control: Control<any>;
  name: string;
  colors: string | undefined;
  value: string;
} & React.HTMLProps<HTMLInputElement>) {
  return (
    <Controller
      control={control}
      name={name}
      render={({ field: { onChange, ref, name, value: checked } }) => (
        <input
          type="radio"
          className={classNames(
            colors,
            'focus:ring-transparent focus:ring-offset-transparent',
            'focus:outline-transparent appearance-none border-4 border-solid',
            'border-transparent checked:!border-accent-background-30 w-16 h-16'
          )}
          style={{
            WebkitTextFillColor: 'transparent',
          }}
          name={name}
          ref={ref}
          onChange={onChange}
          value={value}
          checked={value == checked}
          disabled={disabled}
          {...props}
        />
      )}
    />
  );
}
