import { BatteryIcon } from '../commons/icon/BatteryIcon';
import { Typography } from '../commons/Typography';

export function TrackerBattery({
  value,
  voltage,
  disabled,
  textColor = 'secondary',
}: {
  value: number;
  voltage?: number | null;
  disabled?: boolean;
  textColor?: string;
}) {
  const percent = value * 100;

  return (
    <div className="flex gap-2">
      <div className="flex flex-col justify-around">
        <BatteryIcon value={percent} disabled={disabled} />
      </div>
      {!disabled && (
        <div className="w-10">
          <Typography color={textColor}>{percent.toFixed(0)} %</Typography>
          {voltage && (
            <Typography color={textColor}>{voltage.toFixed(2)} V</Typography>
          )}
        </div>
      )}
      {disabled && (
        <div className="flex flex-col justify-center w-10">
          <div className="w-7 h-1 bg-background-30 rounded-full"></div>
        </div>
      )}
    </div>
  );
}
