import { Localized } from '@fluent/react';
import classNames from 'classnames';
import {
  forwardRef,
  useImperativeHandle,
  useMemo,
  useRef,
  useState,
} from 'react';
import { Control, Controller, UseControllerProps } from 'react-hook-form';
import { FileIcon } from './icon/FileIcon';
import { UploadFileIcon } from './icon/UploadFileIcon';
import { Typography } from './Typography';
import { CloseIcon } from './icon/CloseIcon';

interface InputProps {
  variant?: 'primary' | 'secondary';
  label?: string;
  name: string;
}

const FileInputContentBlank = ({
  isDragging,
  label,
}: {
  isDragging: boolean;
  label: string;
}) => {
  return (
    <div
      className={classNames(
        'flex justify-center w-full h-32 px-4 transition border-2',
        'border-background-20 rounded-md appearance-none cursor-pointer',
        'hover:border-accent-background-20 focus:outline-none',
        'border-dashed'
      )}
    >
      <span className="flex items-center space-x-2 pointer-events-none">
        <UploadFileIcon isDragging={isDragging} />
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
    </div>
  );
};

const FileInputContentFile = ({
  importedFileName,
  onClearPicker,
}: {
  importedFileName: string;
  onClearPicker: () => any;
}) => {
  return (
    <div
      className={classNames(
        'flex flex-col w-full transition border-2',
        'border-background-20 rounded-md appearance-none cursor-pointer',
        'hover:border-accent-background-20 focus:outline-none'
      )}
    >
      <div className="flex items-center space-x-2">
        <div className="flex items-center space-x-2 px-4">
          <FileIcon />
          <span>{importedFileName}</span>
        </div>
        <span className="flex-grow"></span>
        <a
          href="#"
          className="h-12 w-12 hover:bg-accent-background-20 cursor-pointer"
          onClick={() => {
            onClearPicker();
          }}
        >
          <CloseIcon
            className="stroke-background-20 hover:stroke-background-90"
            size={48}
          />
        </a>
      </div>
    </div>
  );
};

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
    importedFileName: string | null;
  }
>(function AppInput(
  {
    label = 'tips-file_select',
    name,
    onChange,
    accept,
    capture,
    multiple = false,
    importedFileName,
  },
  ref
) {
  const innerRef = useRef<HTMLInputElement>(null);

  useImperativeHandle(ref, () => innerRef.current!);

  const acceptList = useMemo(() => accept.split(/, ?/), [accept]);
  const [isDragging, setDragging] = useState(false);

  const isFileImported = importedFileName !== null && !isDragging;

  const onClearPicker = () => {
    onChange([]);
    innerRef.current!.value = '';
  };

  return (
    <label
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
      {isFileImported
        ? FileInputContentFile({ importedFileName, onClearPicker })
        : FileInputContentBlank({ isDragging, label })}

      <input
        type="file"
        className="hidden"
        onChange={(ev) => {
          if (ev.target.files?.length) {
            onChange(ev.target.files);
          }
        }}
        name={name}
        ref={innerRef}
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
  importedFileName,
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
  importedFileName: string | null;
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
          importedFileName={importedFileName}
        ></FileInputInside>
      )}
    />
  );
};
