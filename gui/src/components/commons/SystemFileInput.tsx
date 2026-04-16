import {
  Control,
  Controller,
  FieldPath,
  FieldValues,
  RefCallBack,
  UseControllerProps,
} from 'react-hook-form';
import { FileInputContentBlank, FileInputContentFile } from './FileInput';
import { useElectron } from '@/hooks/electron';

export function InnerSystemFileInput({
  label,
  value,
  onChange,
  directory,
  ref,
}: {
  label: string;
  value: string | null;
  onChange: (...event: any[]) => void;
  directory: boolean;
  ref: RefCallBack;
}) {
  const electron = useElectron();

  const handleClick = async () => {
    if (!electron.isElectron) return;

    const open = await electron.api.openDialog({
      properties: ['openDirectory'],
    });
    if (open.canceled) {
      onChange(null);
      return;
    }
    onChange(open.filePaths[0]);
  };

  return (
    <div ref={ref} onClick={handleClick}>
      {value !== null
        ? FileInputContentFile({
            directory,
            importedFileName: value,
            onClearPicker: () => onChange(null),
          })
        : FileInputContentBlank({ isDragging: false, label, directory })}
    </div>
  );
}

export function SystemFileInput<T extends FieldValues = FieldValues>({
  control,
  rules,
  name,
  label,
  directory = false,
}: {
  rules: UseControllerProps<T, FieldPath<T>>['rules'];
  control: Control<T>;
  /**
   * Use a translation key!
   **/
  label: string;
  name: FieldPath<T>;
  directory?: boolean;
  disabled?: boolean;
}) {
  return (
    <Controller
      rules={rules}
      name={name}
      control={control}
      render={({ field: { onChange, value, ref } }) => (
        <InnerSystemFileInput
          label={label}
          value={value}
          onChange={onChange}
          ref={ref}
          directory={directory}
        />
      )}
    />
  );
}
