import { Control, Controller } from 'react-hook-form';
import { Button } from './Button';
import { Typography } from './Typography';
import { ReactNode, useCallback, useMemo } from 'react';
import { useLocaleConfig } from '@/i18n/config';

export function NumberSelector({
  label,
  valueLabelFormat,
  control,
  name,
  min,
  max,
  step,
  doubleStep,
  disabled = false,
  showButtonWithNumber,
}: {
  label?: string;
  valueLabelFormat?: (value: number) => string | ReactNode;
  control: Control<any>;
  name: string;
  min: number;
  max: number;
  step: number | ((value: number, add: boolean) => number);
  doubleStep?: number;
  disabled?: boolean;
  showButtonWithNumber?: number;
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
      render={({ field: { onChange, value } }) => (
        <div className="flex flex-col gap-1 w-full">
          <Typography bold>{label}</Typography>
          <div className="flex gap-5 bg-background-60 p-2 rounded-lg">
            <div className="flex gap-1">
              {doubleStep !== undefined && (
                <Button
                  variant="tertiary"
                  rounded
                  onClick={() => onChange(doubleStepFn(value, false))}
                  disabled={doubleStepFn(value, false) < min || disabled}
                >
                  {showButtonWithNumber !== undefined
                    ? decimalFormat.format(-showButtonWithNumber)
                    : '--'}
                </Button>
              )}
              <Button
                variant="tertiary"
                rounded
                onClick={() => onChange(stepFn(value, false))}
                disabled={stepFn(value, false) < min || disabled}
              >
                -
              </Button>
            </div>
            <div className="flex flex-grow justify-center text-center items-center w-10 text-standard">
              {valueLabelFormat ? valueLabelFormat(value) : value}
            </div>
            <div className="flex gap-1">
              <Button
                variant="tertiary"
                rounded
                onClick={() => onChange(stepFn(value, true))}
                disabled={stepFn(value, true) > max || disabled}
              >
                +
              </Button>
              {doubleStep !== undefined && (
                <Button
                  variant="tertiary"
                  rounded
                  onClick={() => onChange(doubleStepFn(value, true))}
                  disabled={doubleStepFn(value, true) > max || disabled}
                >
                  {showButtonWithNumber !== undefined
                    ? decimalFormat.format(showButtonWithNumber)
                    : '++'}
                </Button>
              )}
            </div>
          </div>
        </div>
      )}
    />
  );
}
