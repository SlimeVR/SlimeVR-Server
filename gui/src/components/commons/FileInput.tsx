import classNames from 'classnames';
import { forwardRef, MouseEvent, useMemo, useState } from 'react';
import { Control, Controller, UseControllerProps } from 'react-hook-form';

interface InputProps {
  variant?: 'primary' | 'secondary';
  label?: string;
  name: string;
}

export const FileInputInside = forwardRef<
  HTMLInputElement,
  {
    variant?: 'primary' | 'secondary';
    label?: string;
    onChange: () => void;
  } & Partial<HTMLInputElement>
>(function AppInput(
  { placeholder, label, name, onChange, value, variant = 'primary' },
  ref
) {
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
          type="file"
          className={classNames(classes)}
          placeholder={placeholder || undefined}
          onChange={onChange}
          name={name}
          value={value || ''}
          ref={ref}
        ></input>
      </div>
    </label>
  );
});

export const FileInput = ({
  control,
  name,
  placeholder,
  label,
  variant = 'primary',
  rules,
}: {
  rules: UseControllerProps<any>['rules'];
  control: Control<any>;
} & InputProps &
  Partial<HTMLInputElement>) => {
  return (
    <Controller
      control={control}
      name={name}
      rules={rules}
      render={({ field: { onChange, value, ref, name } }) => (
        <FileInputInside
          label={label}
          placeholder={placeholder}
          variant={variant}
          value={value}
          onChange={onChange}
          ref={ref}
          name={name}
        ></FileInputInside>
      )}
    />
  );
};
