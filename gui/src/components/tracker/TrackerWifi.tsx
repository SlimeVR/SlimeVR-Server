import { WifiIcon } from '@/components/commons/icon/WifiIcon';
import { Typography } from '@/components/commons/Typography';

export function TrackerWifi({
  rssi,
  ping,
  rssiShowNumeric,
  disabled,
  textColor = 'secondary',
}: {
  rssi: number;
  ping: number;
  rssiShowNumeric?: boolean;
  disabled?: boolean;
  textColor?: string;
}) {
  return (
    <div className="flex gap-2">
      <div className="flex flex-col justify-around">
        <WifiIcon value={rssi} disabled={disabled} />
      </div>
      {!disabled && (
        <div className="w-12">
          <Typography color={textColor} whitespace="whitespace-nowrap">
            {ping} ms
          </Typography>
          {rssiShowNumeric && (
            <Typography color={textColor} whitespace="whitespace-nowrap">
              {rssi} dBm
            </Typography>
          )}
        </div>
      )}
      {disabled && (
        <div className="flex flex-col justify-center w-12">
          <div className="w-7 h-1 bg-background-30 rounded-full"></div>
        </div>
      )}
    </div>
  );
}
