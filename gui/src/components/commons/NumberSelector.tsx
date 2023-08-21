import { Control, Controller } from 'react-hook-form';
import { Button } from './Button';
import { Typography } from './Typography';

export function NumberSelector({
  label,
  valueLabelFormat,
  control,
  name,
  min,
  max,
  step,
  disabled = false,
}: {
  label?: string;
  valueLabelFormat?: (value: number) => string;
  control: Control<any>;
  name: string;
  min: number;
  max: number;
  step: number | ((value: number, add: boolean) => number);
  disabled?: boolean;
}) {
  const stepFn =
    typeof step === 'function'
      ? step
      : (value: number, add: boolean) =>
          +(add ? value + step : value - step).toFixed(2);

  return (
    <Controller
      control={control}
      name={name}
      render={({ field: { onChange, value } }) => (
        <div className="flex flex-col gap-1 w-full">
          <Typography bold>{label}</Typography>
          <div className="flex gap-5 bg-background-60 p-2 rounded-lg">
            <div className="flex">
              <Button
                variant="tertiary"
                rounded
                onClick={() => onChange(stepFn(value, false))}
                disabled={stepFn(value, false) < min || disabled}
              >
                -
              </Button>
            </div>
            <div className="flex flex-grow justify-center items-center w-10 text-standard">
              {valueLabelFormat ? valueLabelFormat(value) : value}
            </div>
            <div className="flex">
              <Button
                variant="tertiary"
                rounded
                onClick={() => onChange(stepFn(value, true))}
                disabled={stepFn(value, true) > max || disabled}
              >
                +
              </Button>
            </div>
          </div>
        </div>
      )}
    />
  );
}
