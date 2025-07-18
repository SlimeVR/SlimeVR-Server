import { Control, Controller } from 'react-hook-form';
import { Button } from './Button';
import { Typography } from './Typography';
import { ReactNode, useMemo } from 'react';
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
  step: number | ((value: number, sign: number) => number);
  doubleStep?: number | ((value: number, sign: number) => number);
  disabled?: boolean;
  showButtonWithNumber?: number;
}) {
  const { currentLocales } = useLocaleConfig();

  const stepFn =
    typeof step === 'function'
      ? step
      : (value: number, sign: number) => +(value + step * sign).toFixed(2);

  const doubleStepFn =
    typeof doubleStep === 'function'
      ? doubleStep
      : (value: number, sign: number) =>
          doubleStep === undefined
            ? 0
            : +(value + doubleStep * sign).toFixed(2);

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
                  onClick={() => onChange(doubleStepFn(value, -1))}
                  disabled={doubleStepFn(value, -1) < min || disabled}
                >
                  {showButtonWithNumber !== undefined
                    ? decimalFormat.format(-showButtonWithNumber)
                    : '--'}
                </Button>
              )}
              <Button
                variant="tertiary"
                rounded
                onClick={() => onChange(stepFn(value, -1))}
                disabled={stepFn(value, -1) < min || disabled}
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
                onClick={() => onChange(stepFn(value, 1))}
                disabled={stepFn(value, 1) > max || disabled}
              >
                +
              </Button>
              {doubleStep !== undefined && (
                <Button
                  variant="tertiary"
                  rounded
                  onClick={() => onChange(doubleStepFn(value, 1))}
                  disabled={doubleStepFn(value, 1) > max || disabled}
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
