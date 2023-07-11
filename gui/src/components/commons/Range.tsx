import classNames from 'classnames';
import { Control, Controller } from 'react-hook-form';

export function Range({
  control,
  name,
  values,
  min,
  max,
  step,
  // input props
  ...props
}: {
  control: Control<any>;
  name: string;
  max: number;
  min: number;
  step: number;
  values: { value: number; label: string; defaultValue?: boolean }[];
} & React.HTMLProps<HTMLInputElement>) {
  return (
    <Controller
      control={control}
      name={name}
      render={({ field: { onChange, ref, name, value } }) => (
        <label className="text-standard w-full text-center flex items-center flex-col">
          <input
            type="range"
            className=" text-background-10 border-accent-background-30"
            style={{
              width: 'calc(88% - 0.5vw)',
            }}
            name={name}
            ref={ref}
            value={value}
            onChange={onChange}
            list={`${name}-datalist`}
            min={min}
            max={max}
            step={step}
            {...props}
          />
          <datalist id={`${name}-datalist`} className="">
            {values.map(({ value }, i) => (
              <option key={i}>{value}</option>
            ))}
          </datalist>
          <div className="w-full flex flex-nowrap overflow-clip">
            {Array((max - min) / step + 1)
              .fill(0)
              .map((_v, i) => {
                const value = values.find(
                  ({ value }) => i * step + min === value
                );
                return (
                  <span
                    key={i}
                    className={classNames(
                      'flex-1',
                      value?.defaultValue && 'text-status-success'
                    )}
                  >
                    {value?.label}
                  </span>
                );
              })}
          </div>
        </label>
      )}
    />
  );
}
