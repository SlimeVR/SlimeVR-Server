import classNames from 'classnames';
import { forwardRef, useMemo, useState } from 'react';
import {
  Control,
  Controller,
  FieldError,
  FieldPath,
  FieldValues,
  UseControllerProps,
} from 'react-hook-form';
import { EyeIcon } from './icon/EyeIcon';
import { IconButton } from './IconButton';
import { Typography } from './Typography';
import { FLOATING_LABEL_PADDING, FloatingLabel } from './FloatingLabel';

interface InputProps {
  variant?: 'primary' | 'secondary' | 'tertiary';
  label?: string;
  name: string;
}

export const InputInside = forwardRef<
  HTMLInputElement,
  {
    variant?: 'primary' | 'secondary' | 'tertiary';
    label?: string;
    error?: FieldError;
    autocomplete?: boolean | string;
    className?: string;
    errorClassName?: string;
    onBlur?: () => void;
  } & Partial<React.HTMLProps<HTMLInputElement>>
>(function AppInput(
  {
    type,
    placeholder,
    label,
    disabled,
    autocomplete,
    name,
    onChange,
    value,
    error,
    variant = 'primary',
    className,
    errorClassName,
    onBlur,
  },
  ref
) {
  const [forceText, setForceText] = useState(false);
  const [focused, setFocused] = useState(false);

  const togglePassword = () => setForceText(!forceText);

  const classes = useMemo(() => {
    const variantsMap = {
      primary: classNames({
        'placeholder:text-background-10 placeholder:italic bg-background-60 border-background-60':
          !disabled,
        'text-background-30 placeholder:text-background-30 border-background-70 bg-background-70':
          disabled,
      }),
      secondary: classNames({
        'placeholder:text-background-10 placeholder:italic bg-background-50 border-background-50':
          !disabled,
        'text-background-40 placeholder:text-background-40 border-background-70 bg-background-70':
          disabled,
      }),
      tertiary: classNames({
        'placeholder:text-background-10 placeholder:italic bg-background-40 border-background-40':
          !disabled,
        'text-background-30 placeholder:text-background-30 border-background-70 bg-background-70':
          disabled,
      }),
    };

    return classNames(
      variantsMap[variant],
      'w-full min-h-[48px] z-10 rounded-md focus:border-accent-background-40',
      'text-standard text-background-10 relative transition-colors',
      error && 'border-status-critical border-1'
    );
  }, [variant, disabled, error]);

  const computedValue = disabled
    ? placeholder
    : value !== undefined
      ? value
      : '';

  // The label sits inside the field as a floating label: centered while the
  // field is empty and unfocused, shrunk to the top-left once focused or filled.
  // A bare placeholder with no label behaves the same way.
  const floatingText = label || placeholder;
  const hasFloatingLabel = !!floatingText && !disabled;
  const hasValue =
    value !== undefined && value !== null && String(value).length > 0;
  const floating = focused || hasValue;

  return (
    <label className="flex flex-col gap-1">
      {label && !hasFloatingLabel && <Typography>{label}</Typography>}
      <div className="relative w-full">
        <input
          type={forceText ? 'text' : type}
          className={classNames(
            classes,
            {
              'pr-10 sentry-mask': type === 'password',
              [FLOATING_LABEL_PADDING]: hasFloatingLabel,
            },
            className
          )}
          placeholder={
            hasFloatingLabel
              ? focused && placeholder && placeholder !== floatingText
                ? placeholder
                : undefined
              : placeholder || undefined
          }
          autoComplete={autocomplete ? 'off' : 'on'}
          onChange={onChange}
          onFocus={() => setFocused(true)}
          name={name}
          value={computedValue} // Do we want that behaviour ?
          disabled={disabled}
          ref={ref}
          onBlur={() => {
            setFocused(false);
            onBlur?.();
          }}
        />
        {hasFloatingLabel && (
          <FloatingLabel label={floatingText} floating={floating} />
        )}
        {type === 'password' && (
          <IconButton
            labelId={forceText ? 'input-password-hide' : 'input-password-show'}
            pressed={forceText}
            tooltip={false}
            className="fill-background-10 absolute inset-y-0 right-0 pr-6 z-10 my-auto w-[16px] h-[16px] cursor-pointer"
            onClick={togglePassword}
          >
            <EyeIcon width={16} closed={forceText} />
          </IconButton>
        )}
        {error?.message && (
          <div
            className={classNames(
              'absolute z-0 pt-1.5 px-1 w-full rounded-b-md bg-dark-background-600 text-status-critical',
              hasFloatingLabel ? 'top-[calc(100%-4px)]' : 'top-[44px]',
              errorClassName
            )}
          >
            {error.message}
          </div>
        )}
      </div>
    </label>
  );
});

export const Input = <T extends FieldValues = FieldValues>({
  type = 'text',
  control,
  name,
  placeholder,
  label,
  autocomplete = false,
  disabled,
  variant = 'primary',
  rules,
  className,
  errorClassName,
  onBlur,
}: {
  rules?: UseControllerProps<T, FieldPath<T>>['rules'];
  control: Control<T>;
  name: FieldPath<T>;
  autocomplete?: boolean | string;
  className?: string;
  errorClassName?: string;
  onBlur?: () => void;
} & Omit<InputProps, 'name'> &
  Partial<React.HTMLProps<HTMLInputElement>>) => {
  return (
    <Controller
      control={control}
      name={name}
      rules={rules}
      render={({
        field: { onChange, value, ref, name },
        fieldState: { error },
      }) => (
        <InputInside
          type={type}
          autocomplete={autocomplete}
          label={label}
          placeholder={placeholder}
          variant={variant}
          value={value}
          disabled={disabled}
          error={error}
          onChange={onChange}
          ref={ref}
          name={name}
          className={className}
          errorClassName={errorClassName}
          onBlur={onBlur}
        />
      )}
    />
  );
};
