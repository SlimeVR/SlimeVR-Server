import { Localized } from '@fluent/react';
import classNames from 'classnames';
import { forwardRef, useMemo, useState } from 'react';
import { Control, Controller, UseControllerProps } from 'react-hook-form';
import { FileIcon } from './icon/FileIcon';
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
        setDragging(false);

        if (
          ev.dataTransfer.files.length &&
          // If MIME type is any of the accept list,
          // or if file extension is anything on the acceptList
          (acceptList.includes(ev.dataTransfer.files[0].type) ||
            acceptList.some((ext) =>
              ev.dataTransfer.files[0].name.endsWith(ext)
            ))
        ) {
          onChange(ev.dataTransfer.files);
        }
      }}
      onDragEnter={(ev) => {
        ev.preventDefault();
        setDragging(true);
      }}
      onDragLeave={(ev) => {
        ev.preventDefault();
        setDragging(false);
      }}
    >
      <span className="flex items-center space-x-2 pointer-events-none">
        <FileIcon isDragging={isDragging} />
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
