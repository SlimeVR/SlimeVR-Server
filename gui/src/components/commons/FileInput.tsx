import classNames from 'classnames';
import { forwardRef, useMemo } from 'react';
import { Control, Controller, UseControllerProps } from 'react-hook-form';

interface InputProps {
  variant?: 'primary' | 'secondary';
  label?: string;
  name: string;
}

export interface FileValue {
  value?: string;
  files: FileList;
}

export const FileInputInside = forwardRef<
  HTMLInputElement,
  {
    variant?: 'primary' | 'secondary';
    label?: string;
    accept: string;
    capture?: boolean | 'user' | 'environment';
    multiple?: boolean;
    value: FileValue;
    onChange: (...event: any[]) => void;
  } & Partial<HTMLInputElement>
>(function AppInput(
  {
    label,
    name,
    onChange,
    value,
    variant = 'primary',
    accept,
    capture,
    multiple = false,
  },
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
          onChange={(ev) => {
            onChange({
              value: ev.target.value,
              files: ev.target.files,
            });
          }}
          name={name}
          value={value?.value || ''}
          ref={ref}
          accept={accept}
          multiple={multiple}
          capture={capture}
        ></input>
      </div>
    </label>
  );
});

export const FileInput = ({
  control,
  name,
  label,
  variant = 'primary',
  rules,
  accept,
  multiple,
  capture,
}: {
  rules: UseControllerProps<any>['rules'];
  control: Control<any>;
  accept: string;
  multiple?: boolean;
  capture?: boolean | 'user' | 'environment';
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
          variant={variant}
          value={value}
          onChange={onChange}
          ref={ref}
          name={name}
          accept={accept}
          capture={capture}
          multiple={multiple}
        ></FileInputInside>
      )}
    />
  );
};
