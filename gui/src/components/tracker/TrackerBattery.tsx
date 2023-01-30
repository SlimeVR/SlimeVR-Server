import { useLocaleConfig } from '../../i18n/config';
import { BatteryIcon } from '../commons/icon/BatteryIcon';
import { Typography } from '../commons/Typography';

export function TrackerBattery({
  value,
  voltage,
  disabled,
  textColor = 'secondary',
  deviceId,
}: {
  /**
   * a [0, 1] value range is expected
   */
  value: number;
  voltage?: number | null;
  disabled?: boolean;
  textColor?: string;
  deviceId: number | null;
}) {
  const { currentLocales } = useLocaleConfig();
  const percentFormatter = new Intl.NumberFormat(currentLocales, {
    style: 'percent',
  });
  const voltageFormatter = new Intl.NumberFormat(currentLocales, {
    minimumFractionDigits: 2,
  });

  const oldBatteryInfo = localStorage.getItem(`deviceBattery_${deviceId}`);

  if (!disabled) {
    const batteryInfo: BatteryInfo = { value, voltage };
    localStorage.setItem(
      `deviceBattery_${deviceId}`,
      JSON.stringify(batteryInfo)
    );
  } else if (oldBatteryInfo) {
    const batteryInfo: BatteryInfo = JSON.parse(oldBatteryInfo);
    value = batteryInfo.value;
    voltage = batteryInfo.voltage;
  }

  // Why did I name it like this
  const actuallyDisabled = disabled && !oldBatteryInfo;

  return (
    <div className="flex gap-2">
      <div className="flex flex-col justify-around">
        <BatteryIcon value={value} disabled={disabled} />
      </div>
      {!actuallyDisabled && (
        <div className="w-10">
          <Typography color={textColor}>
            {percentFormatter.format(value)}
          </Typography>
          {voltage && (
            <Typography color={textColor}>
              {voltageFormatter.format(voltage)} V
            </Typography>
          )}
        </div>
      )}
      {actuallyDisabled && (
        <div className="flex flex-col justify-center w-10">
          <div className="w-7 h-1 bg-background-30 rounded-full"></div>
        </div>
      )}
    </div>
  );
}

interface BatteryInfo {
  value: number;
  voltage?: number | null;
}
