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
}: {
  label: string;
  valueLabelFormat?: (value: number) => string;
  control: Control<any>;
  name: string;
  min: number;
  max: number;
  step: number | ((value: number, add: boolean) => number);
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
          <div className="flex gap-2 bg-background-60 p-2 rounded-lg">
            <div className="flex">
              <Button
                variant="tiertiary"
                rounded
                onClick={() => onChange(stepFn(value, false))}
                disabled={stepFn(value, false) < min}
              >
                -
              </Button>
            </div>
            <div className="flex flex-grow justify-center items-center w-10">
              {valueLabelFormat ? valueLabelFormat(value) : value}
            </div>
            <div className="flex">
              <Button
                variant="tiertiary"
                rounded
                onClick={() => onChange(stepFn(value, true))}
                disabled={stepFn(value, true) > max}
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
