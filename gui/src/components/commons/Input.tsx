import classNames from 'classnames';
import { forwardRef, MouseEvent, useMemo, useState } from 'react';
import { Control, Controller, UseControllerProps } from 'react-hook-form';
import { EyeIcon } from './icon/EyeIcon';

interface InputProps {
  variant?: 'primary' | 'secondary';
  label?: string;
  name: string;
}

export const InputInside = forwardRef<
  HTMLInputElement,
  {
    variant?: 'primary' | 'secondary';
    label?: string;
    onChange: () => void;
  } & Partial<HTMLInputElement>
>(function AppInput(
  {
    type,
    placeholder,
    label,
    autocomplete,
    name,
    onChange,
    value,
    variant = 'primary',
  },
  ref
) {
  const [forceText, setForceText] = useState(false);

  const togglePassword = (e: MouseEvent<HTMLDivElement>) => {
    e.preventDefault();
    setForceText(!forceText);
  };

  const classes = useMemo(() => {
    const variantsMap = {
      primary: classNames('bg-background-60 border-background-60'),
      secondary: classNames('bg-background-50 border-background-50'),
    };

    return classNames(
      variantsMap[variant],
      'w-full focus:ring-transparent focus:ring-offset-transparent focus:outline-transparent rounded-md bg-background-60 border-background-60 focus:border-accent-background-40 placeholder:text-background-30 text-standard relative'
    );
  }, [variant]);

  return (
    <label className="flex flex-col gap-1">
      {label}
      <div className="relative w-full">
        <input
          type={forceText ? 'text' : type}
          className={classNames(classes, { 'pr-10': type === 'password' })}
          placeholder={placeholder || undefined}
          autoComplete={autocomplete ? 'off' : 'on'}
          onChange={onChange}
          name={name}
          value={value || ''}
          ref={ref}
        ></input>
        {type === 'password' && (
          <div
            className="fill-background-10 absolute top-0 h-full flex flex-col justify-center right-0 p-4"
            onClick={togglePassword}
          >
            <EyeIcon></EyeIcon>
          </div>
        )}
      </div>
    </label>
  );
});

export const Input = ({
  type,
  control,
  name,
  placeholder,
  label,
  autocomplete,
  variant = 'primary',
  rules,
}: {
  rules?: UseControllerProps<any>['rules'];
  control: Control<any>;
} & InputProps &
  Partial<HTMLInputElement>) => {
  return (
    <Controller
      control={control}
      name={name}
      rules={rules}
      render={({ field: { onChange, value, ref, name } }) => (
        <InputInside
          type={type}
          autocomplete={autocomplete}
          label={label}
          placeholder={placeholder}
          variant={variant}
          value={value}
          onChange={onChange}
          ref={ref}
          name={name}
        ></InputInside>
      )}
    />
  );
};
