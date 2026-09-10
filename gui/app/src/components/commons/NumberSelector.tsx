import { Control, Controller, FieldPath, FieldValues } from 'react-hook-form';
import { FieldCaption } from './FloatingLabel';
import { Typography } from './Typography';
import { useCallback, useMemo } from 'react';
import { useLocaleConfig } from '@/i18n/config';

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
        // Flat full-height stepper segments hugging the box edges.
        const segment =
          'flex min-w-[3rem] shrink-0 items-center justify-center px-3 text-lg font-bold leading-none text-background-10 fill-background-10 transition-colors enabled:hover:bg-background-50 disabled:opacity-30';
        return (
          <div className="flex flex-col gap-1 w-full">
            {hasLabel && <FieldCaption>{label}</FieldCaption>}
            <div className="relative flex items-stretch min-h-[48px] overflow-hidden rounded-md bg-background-60">
              {doubleStep !== undefined && (
                <button
                  type="button"
                  className={segment}
                  onClick={() => onChange(doubleStepFn(value, false))}
                  disabled={doubleStepFn(value, false) < min || disabled}
                >
                  {showButtonWithNumber
                    ? decimalFormat.format(-doubleStep)
                    : '−−'}
                </button>
              )}
              <button
                type="button"
                className={segment}
                onClick={() => onChange(stepFn(value, false))}
                disabled={stepFn(value, false) < min || disabled}
              >
                −
              </button>
              <div className="flex flex-grow items-center justify-center px-2 select-none">
                <Typography variant="standard">{shown}</Typography>
              </div>
              <button
                type="button"
                className={segment}
                onClick={() => onChange(stepFn(value, true))}
                disabled={stepFn(value, true) > max || disabled}
              >
                +
              </button>
              {doubleStep !== undefined && (
                <button
                  type="button"
                  className={segment}
                  onClick={() => onChange(doubleStepFn(value, true))}
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
