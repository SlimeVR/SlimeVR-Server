import { Localized } from '@fluent/react';
import classNames from 'classnames';
import { forwardRef, useMemo, useState } from 'react';
import { Control, Controller, UseControllerProps } from 'react-hook-form';
import { Typography } from './Typography';

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
    accept: string;
    capture?: boolean | 'user' | 'environment';
    multiple?: boolean;
    value: FileList;
    onChange: (...event: any[]) => void;
    name: string;
  }
>(function AppInput(
  {
    label = 'tips-file_select',
    name,
    onChange,
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
      'w-full focus:ring-transparent focus:ring-offset-transparent',
      'focus:outline-transparent rounded-md bg-background-60 border-background-60',
      'focus:border-accent-background-40 placeholder:text-background-30 text-standard',
      'relative hidden'
    );
  }, [variant]);
  const acceptList = useMemo(() => accept.split(/, ?/), [accept]);
  const [isDragging, setDragging] = useState(false);

  return (
    <label
      className={classNames(
        'flex justify-center w-full h-32 px-4 transition border-2',
        'border-background-20 border-dashed rounded-md appearance-none cursor-pointer',
        'hover:border-background-40 focus:outline-none'
      )}
      onClick={() => typeof ref !== 'function' && ref?.current?.click()}
      onDragOver={(ev) => ev.preventDefault()}
      onDrop={(ev) => {
        ev.preventDefault();

        if (
          ev.dataTransfer.files.length &&
          acceptList.includes(ev.dataTransfer.files[0].type)
        ) {
          onChange(ev.dataTransfer.files);
        }
      }}
      onDragEnter={(ev) => {
        ev.preventDefault();
        setDragging(true);
      }}
      onDragLeave={() => setDragging(false)}
    >
      <span className="flex items-center space-x-2 pointer-events-none">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
          fill="currentColor"
          className={classNames(
            'w-6 h-6 transition-transform',
            isDragging && 'scale-150'
          )}
        >
          <path
            fillRule="evenodd"
            d="M5.625 1.5H9a3.75 3.75 0 013.75 3.75v1.875c0 1.036.84 1.875 1.875 1.875H16.5a3.75 3.75 0 013.75 3.75v7.875c0 1.035-.84 1.875-1.875 1.875H5.625a1.875 1.875 0 01-1.875-1.875V3.375c0-1.036.84-1.875 1.875-1.875zm6.905 9.97a.75.75 0 00-1.06 0l-3 3a.75.75 0 101.06 1.06l1.72-1.72V18a.75.75 0 001.5 0v-4.19l1.72 1.72a.75.75 0 101.06-1.06l-3-3z"
            clipRule="evenodd"
          />
          <path d="M14.25 5.25a5.23 5.23 0 00-1.279-3.434 9.768 9.768 0 016.963 6.963A5.23 5.23 0 0016.5 7.5h-1.875a.375.375 0 01-.375-.375V5.25z" />
        </svg>
        <div>
          <Localized
            id={label}
            elems={{
              u: <span className="underline text-background-20"></span>,
            }}
          >
            <Typography>
              Drop files to attach, or{' '}
              <span className="underline text-background-20">browse</span>
            </Typography>
          </Localized>
        </div>
      </span>
      <input
        type="file"
        className={classNames(classes)}
        onChange={(ev) => {
          onChange(ev.target.files);
        }}
        name={name}
        ref={ref}
        accept={accept}
        multiple={multiple}
        capture={capture}
      ></input>
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
  /**
   * Use a translation key!
   **/
  label?: string;
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
