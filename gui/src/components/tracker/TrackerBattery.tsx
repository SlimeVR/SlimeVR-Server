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

  return (
    <div className="flex gap-2">
      <div className="flex flex-col justify-around">
        <BatteryIcon
          value={((voltage || 0) <= 4.3 && value) || 0}
          disabled={disabled}
          charging={false}
        />
      </div>
      {((voltage || 0) > 4.3 && (
        <div className="flex flex-col justify-center w-10">
          <svg
            width="18"
            height="16"
            viewBox="0 0 14 10"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              className={'fill-status-warning'}
              d="M 0.93561138,11.744353 2.4349252,6.1488377 H 0.0312815 L 3.5761014,0.00903018 2.2061799,5.1216451 h 2.4534885 z"
              transform="translate(3, -0.5)"
            />
          </svg>
        </div>
      )) || (
        <div className="w-10">
          <Typography color={textColor}>
            {percentFormatter.format(value)}
          </Typography>
          {voltage && config?.debug && (
            <Typography color={textColor}>
              {voltageFormatter.format(voltage)}V
            </Typography>
          )}
        </div>
      )}
    </div>
  );
}
