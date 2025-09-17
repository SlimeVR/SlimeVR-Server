import { useConfig } from '@/hooks/config';
import { useLocaleConfig } from '@/i18n/config';
import { BatteryIcon } from '@/components/commons/icon/BatteryIcon';
import { Typography } from '@/components/commons/Typography';

export function TrackerBattery({
  value,
  voltage,
  disabled,
  textColor = 'secondary',
}: {
  /**
   * a [0, 1] value range is expected
   */
  value: number;
  voltage?: number | null;
  disabled?: boolean;
  textColor?: string;
}) {
  const { currentLocales } = useLocaleConfig();
  const { config } = useConfig();
  const percentFormatter = new Intl.NumberFormat(currentLocales, {
    style: 'percent',
  });
  const voltageFormatter = new Intl.NumberFormat(currentLocales, {
    maximumFractionDigits: 2,
    minimumFractionDigits: 2,
  });

  const charging = (voltage || 0) > 4.3;
  const showVoltage = voltage && config?.debug;

  return (
    <div className="flex gap-2">
      <div className="flex flex-col justify-around">
        <BatteryIcon value={value} disabled={disabled} charging={charging} />
      </div>
      {((!charging || showVoltage) && (
        <div className="w-10">
          {!charging && (
            <Typography color={textColor}>
              {percentFormatter.format(value)}
            </Typography>
          )}
          {showVoltage && (
            <Typography color={textColor}>
              {voltageFormatter.format(voltage)}V
            </Typography>
          )}
        </div>
      )) || (
        <div className="flex flex-col justify-center w-10">
          <div className="w-5 h-1 bg-background-30 rounded-full"></div>
        </div>
      )}
    </div>
  );
}
