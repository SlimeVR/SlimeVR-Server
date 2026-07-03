import { WifiIcon } from '@/components/commons/icon/WifiIcon';
import { Typography } from '@/components/commons/Typography';
import { Tooltip } from '@/components/commons/Tooltip';

export function TrackerWifi({
  rssi,
  ping,
  rssiShowNumeric,
  disabled,
  packetLoss,
  packetsLost,
  packetsReceived,
  showPacketLoss = false,
  textColor = 'primary',
}: {
  rssi: number | null;
  ping: number | null;
  packetLoss?: number | null;
  packetsLost?: number | null;
  packetsReceived?: number | null;
  showPacketLoss?: boolean;
  rssiShowNumeric?: boolean;
  disabled?: boolean;
  textColor?: string;
}) {
  return (
    <div className="flex gap-2">
      <div className="flex flex-col justify-around">
        <WifiIcon value={rssi} disabled={disabled} />
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
          {showPacketLoss && packetsReceived != null && (
            <Tooltip
              preferedDirection="top"
              content={<Typography id="tracker-infos-packet_loss" />}
            >
              <Typography
                color={textColor}
                whitespace="whitespace-nowrap"
              >{`${((packetLoss ?? 0) * 100).toFixed(0)}% (${packetsLost ?? 0} / ${packetsReceived})`}</Typography>
            </Tooltip>
          )}
        </div>
      )) || (
        <div className="flex flex-col justify-center w-12">
          <div className="w-7 h-1 bg-background-30 rounded-full" />
        </div>
      )}
    </div>
  );
}
