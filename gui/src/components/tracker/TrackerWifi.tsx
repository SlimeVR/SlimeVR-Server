import { WifiIcon } from '@/components/commons/icon/WifiIcon';
import { Typography } from '@/components/commons/Typography';

export function TrackerWifi({
  rssi,
  ping,
  rssiShowNumeric,
  disabled,
  textColor = 'secondary',
}: {
  rssi: number | null;
  ping: number | null;
  rssiShowNumeric?: boolean;
  disabled?: boolean;
  textColor?: string;
}) {
  return (
    <div className="flex gap-2">
      <div className="flex flex-col justify-around">
        <WifiIcon value={rssi || -100} disabled={disabled} />
      </div>
      {(!disabled && (ping != null || (rssiShowNumeric && rssi != null)) && (
        <div className="w-12">
          {ping != null && (
            <Typography color={textColor} whitespace="whitespace-nowrap">
              {ping} ms
            </Typography>
          )}
          {rssiShowNumeric && rssi != null && (
            <Typography color={textColor} whitespace="whitespace-nowrap">
              {rssi} dBm
            </Typography>
          )}
        </div>
      )) || (
        <div className="flex flex-col justify-center w-12">
          <div className="w-7 h-1 bg-background-30 rounded-full"></div>
        </div>
      )}
    </div>
  );
}
