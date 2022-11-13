import { WifiIcon } from '../commons/icon/WifiIcon';
import { Typography } from '../commons/Typography';

export function TrackerWifi({
  rssi,
  ping,
  disabled,
}: {
  rssi: number;
  ping: number;
  disabled?: boolean;
}) {
  return (
    <div className="flex gap-2">
      <div className="flex flex-col justify-around  ">
        <WifiIcon value={rssi} disabled={disabled} />
      </div>
      {!disabled && (
        <div className="w-10">
          <Typography color="secondary">{ping} ms</Typography>
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
