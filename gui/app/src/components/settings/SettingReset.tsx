import classNames from 'classnames';
import { FieldPath, FieldPathValue, FieldValues } from 'react-hook-form';
import { isEqual } from '@react-hookz/deep-equal';
import { IconButton } from '@/components/commons/IconButton';
import { ResetIcon } from '@/components/commons/icon/ResetIcon';

export interface SettingResetProps {
  modified: boolean;
  onReset: () => void;
}

export function SettingResetButton({ modified, onReset }: SettingResetProps) {
  return (
    <IconButton
      labelId="settings-reset-single"
      tooltipDirection="top"
      disabled={!modified}
      onClick={onReset}
      className={classNames(
        'w-7 h-7 rounded-xl flex items-center justify-center transition-colors',
        modified
          ? 'text-accent hover:bg-background-60 cursor-pointer'
          : 'text-background-60 cursor-not-allowed'
      )}
    >
      <ResetIcon size={16} />
    </IconButton>
  );
}

export function settingReset<T extends FieldValues, P extends FieldPath<T>>(
  value: FieldPathValue<T, P> | undefined,
  current: unknown,
  write: (value: FieldPathValue<T, P>) => void
): { reset: SettingResetProps } {
  return {
    reset: {
      modified: value !== undefined && !isEqual(current, value),
      onReset: () => {
        if (value === undefined) return;
        write(value);
      },
    },
  };
}
