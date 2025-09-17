import {
  Control,
  Controller,
  RefCallBack,
  UseControllerProps,
} from 'react-hook-form';
import { FileInputContentBlank, FileInputContentFile } from './FileInput';
import { open } from '@tauri-apps/plugin-dialog';

export function InnerTauriFileInput({
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
  return (
    <div ref={ref} onClick={async () => onChange(await open({ directory }))}>
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

export function TauriFileInput({
  control,
  rules,
  name,
  label,
  directory = false,
}: {
  rules: UseControllerProps<any>['rules'];
  control: Control<any>;
  /**
   * Use a translation key!
   **/
  label: string;
  name: string;
  directory?: boolean;
  disabled?: boolean;
}) {
  return (
    <Controller
      rules={rules}
      name={name}
      control={control}
      render={({ field: { onChange, value, ref } }) => (
        <InnerTauriFileInput
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
