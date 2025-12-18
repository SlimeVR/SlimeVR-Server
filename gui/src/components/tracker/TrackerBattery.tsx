import { useConfig } from '@/hooks/config';
import { useLocaleConfig } from '@/i18n/config';
import { BatteryIcon } from '@/components/commons/icon/BatteryIcon';
import { Typography } from '@/components/commons/Typography';
import { Tooltip } from '@/components/commons/Tooltip';

export function TrackerBattery({
  value,
  voltage,
  runtime,
  disabled,
  moreInfo = false,
  textColor = 'primary',
}: {
  /**
   * a [0, 1] value range is expected
   */
  value: number;
  voltage?: number | null;
  runtime?: bigint | null;
  disabled?: boolean;
  moreInfo?: boolean;
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
  const debug = config?.debug || config?.devSettings.moreInfo;
  const showVoltage = moreInfo && voltage && debug;

  return (
    <Tooltip
      disabled={!charging && (!runtime || debug)}
      preferedDirection="left"
      content=<Typography>{percentFormatter.format(value)}</Typography>
    >
      <div className="flex gap-2">
        <div className="flex flex-col justify-around">
          <BatteryIcon value={value} disabled={disabled} charging={charging} />
        </div>
        {((!charging || showVoltage) && (
          <div className="w-15">
            {!charging && runtime != null && runtime > 0 && (
              <Typography color={textColor}>
                {(runtime / BigInt(3600000000)).toString() +
                  'h ' +
                  (
                    (runtime % BigInt(3600000000)) /
                    BigInt(60000000)
                  ).toString() +
                  'min'}
              </Typography>
            )}
            {!charging && (!runtime || debug) && (
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
          <div className="flex flex-col justify-center w-15">
            <div className="w-5 h-1 bg-background-30 rounded-full" />
          </div>
        )}
      </div>
    </Tooltip>
  );
}
