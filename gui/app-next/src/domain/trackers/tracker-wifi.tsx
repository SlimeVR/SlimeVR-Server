import { TrackerStatus } from 'solarxr-protocol';
import { createUniqueId, Show } from 'solid-js';
import { fmt, formats } from '@/lib/format';
import { TextTooltip } from '@/ui/tooltip';
import { WifiSignalIcon } from '@/ui/wifi-signal-icon';
import { trackerDisplayPref } from './display-prefs';
import { trackerByKey, trackerDevice, type TrackerKey } from './store';

export interface TrackerWifiProps {
  trackerKey: TrackerKey;
  // Show the dBm value. Default: the `showNumericSignal` display pref.
  numeric?: boolean;
  packetLoss?: boolean;
}

function PacketLoss(props: {
  loss: number | null | undefined;
  lost: number | null | undefined;
  received: number | null | undefined;
}) {
  const tipId = createUniqueId();
  let el: HTMLSpanElement | undefined;
  const counts = () =>
    props.lost != null && props.received != null
      ? ` (${props.lost} / ${props.received})`
      : '';

  return (
    <>
      <span ref={el} aria-describedby={tipId}>
        {fmt(formats.percent, props.loss)}
        {props.loss == null ? '' : counts()}
      </span>
      <TextTooltip
        id={tipId}
        anchor={() => el}
        placement="top"
        text="tracker-infos-packet_loss"
      />
    </>
  );
}

// Signal icon + ping, dBm and packet loss. Nothing when the device reports
// neither ping nor signal.
export function TrackerWifi(props: TrackerWifiProps) {
  const hw = () => trackerDevice(props.trackerKey)?.hardwareStatus;
  const disconnected = () =>
    trackerByKey(props.trackerKey)?.status === TrackerStatus.DISCONNECTED;
  const numeric = () => props.numeric ?? trackerDisplayPref().showNumericSignal;

  return (
    <Show when={(hw()?.rssi != null || hw()?.ping != null) && hw()}>
      {(status) => {
        const showText = () =>
          !disconnected() &&
          (status().ping != null || (numeric() && status().rssi != null));

        return (
          <div class="flex items-center gap-sm">
            <WifiSignalIcon rssi={status().rssi} disabled={disconnected()} />
            <Show when={showText()}>
              <span class="flex w-12 flex-col whitespace-nowrap">
                <Show when={status().ping != null}>
                  <span>{fmt(formats.milliseconds, status().ping)}</span>
                </Show>
                <Show when={numeric() && status().rssi != null}>
                  <span>{fmt(formats.dbm, status().rssi)}</span>
                </Show>
                <Show when={props.packetLoss}>
                  <PacketLoss
                    loss={status().packetLoss}
                    lost={status().packetsLost}
                    received={status().packetsReceived}
                  />
                </Show>
              </span>
            </Show>
          </div>
        );
      }}
    </Show>
  );
}
