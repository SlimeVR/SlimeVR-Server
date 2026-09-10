import { Control, Controller, FieldPath, FieldValues } from 'react-hook-form';
import classNames from 'classnames';
import { FieldCaption } from './FloatingLabel';
import { Typography } from './Typography';
import { KeyboardEvent, useCallback, useMemo } from 'react';
import { useLocaleConfig } from '@/i18n/config';
import { FOCUS_RING } from '@/utils/a11y';

export function NumberSelector<T extends FieldValues = FieldValues>({
  label,
  valueLabelFormat,
  control,
  name,
  min,
  max,
  step,
  doubleStep,
  disabled = false,
  showButtonWithNumber = false,
}: {
  label?: string;
  valueLabelFormat?: (value: number) => string;
  control: Control<T>;
  name: FieldPath<T>;
  min: number;
  max: number;
  step: number | ((value: number, add: boolean) => number);
  doubleStep?: number;
  disabled?: boolean;
  showButtonWithNumber?: boolean;
}) {
  const { currentLocales } = useLocaleConfig();

  const stepFn =
    typeof step === 'function'
      ? step
      : (value: number, add: boolean) =>
          +(add ? value + step : value - step).toFixed(2);

  const doubleStepFn = useCallback(
    (value: number, add: boolean) =>
      doubleStep === undefined
        ? 0
        : +(add ? value + doubleStep : value - doubleStep).toFixed(2),
    [doubleStep]
  );

  const decimalFormat = useMemo(
    () =>
      new Intl.NumberFormat(currentLocales, {
        style: 'decimal',
        maximumFractionDigits: 2,
        signDisplay: 'exceptZero',
      }),
    [currentLocales]
  );

  return (
    <Controller
      control={control}
      name={name}
      render={({ field: { onChange, value } }) => {
        const hasLabel = label !== undefined && label.length !== 0;
        const shown = valueLabelFormat ? valueLabelFormat(value) : value;

        const clamp = (v: number) => Math.min(max, Math.max(min, v));
        const bump = (add: boolean, big = false) =>
          !disabled &&
          onChange(clamp(big ? doubleStepFn(value, add) : stepFn(value, add)));

        const onKeyDown = (e: KeyboardEvent) => {
          switch (e.key) {
            case 'ArrowUp':
            case 'ArrowRight':
              bump(true);
              break;
            case 'ArrowDown':
            case 'ArrowLeft':
              bump(false);
              break;
            case 'PageUp':
              bump(true, doubleStep !== undefined);
              break;
            case 'PageDown':
              bump(false, doubleStep !== undefined);
              break;
            case 'Home':
              if (!disabled) onChange(min);
              break;
            case 'End':
              if (!disabled) onChange(max);
              break;
            default:
              return;
          }
          e.preventDefault();
        };

        // Flat full-height stepper segments hugging the box edges.
        const segment =
          'flex min-w-[3rem] shrink-0 items-center justify-center px-3 text-lg font-bold leading-none text-background-10 fill-background-10 transition-colors enabled:hover:bg-background-50 disabled:opacity-30';
        return (
          <div className="flex flex-col gap-1 w-full">
            {hasLabel && <FieldCaption>{label}</FieldCaption>}
            <div
              role="spinbutton"
              tabIndex={disabled ? -1 : 0}
              aria-label={label}
              aria-disabled={disabled || undefined}
              aria-valuenow={typeof value === 'number' ? value : undefined}
              aria-valuemin={min}
              aria-valuemax={max}
              aria-valuetext={typeof shown === 'string' ? shown : undefined}
              onKeyDown={onKeyDown}
              className={classNames(
                'relative flex items-stretch min-h-[48px] overflow-hidden rounded-md bg-background-60',
                FOCUS_RING
              )}
            >
              {doubleStep !== undefined && (
                <button
                  type="button"
                  tabIndex={-1}
                  className={segment}
                  onClick={() => bump(false, true)}
                  disabled={doubleStepFn(value, false) < min || disabled}
                >
                  {showButtonWithNumber
                    ? decimalFormat.format(-doubleStep)
                    : '--'}
                </button>
              )}
              <button
                type="button"
                tabIndex={-1}
                className={segment}
                onClick={() => bump(false)}
                disabled={stepFn(value, false) < min || disabled}
              >
                −
              </button>
              <div className="flex flex-grow items-center justify-center px-2 select-none">
                <Typography variant="standard">{shown}</Typography>
              </div>
              <button
                type="button"
                tabIndex={-1}
                className={segment}
                onClick={() => bump(true)}
                disabled={stepFn(value, true) > max || disabled}
              >
                +
              </button>
              {doubleStep !== undefined && (
                <button
                  type="button"
                  tabIndex={-1}
                  className={segment}
                  onClick={() => bump(true, true)}
                  disabled={doubleStepFn(value, true) > max || disabled}
                >
                  {showButtonWithNumber
                    ? decimalFormat.format(doubleStep)
                    : '++'}
                </button>
              )}
            </div>
          </div>
        );
      }}
    />
  );
}
