import { createUniqueId } from 'solid-js';
import { cx } from '@/lib/cx';
import { levelColor } from '@/ui/battery-icon';

const BACK =
  'M7.799 12.378L15.585 2.67801C13.3492 0.95947 10.6129 0.01903 7.793 1.00136e-05C4.9725 0.0172 2.23528 0.95782 0 2.67801L7.786 12.378L7.793 12.385L7.799 12.378Z';
const WEDGE =
  'M0 2.712L7.782 12.392V12.407L7.795 12.396L15.577 2.716C13.3449 0.980306 10.6044 0.026036 7.777 0C4.95656 0.021826 2.22242 0.975276 0 2.712Z';

// -95 dBm reads empty, -40 dBm full
const RSSI_EMPTY = -95;
const RSSI_FULL = -40;

export function signalLevel(rssi: number | null | undefined): number {
  if (rssi == null) return 0;
  const level = (rssi - RSSI_EMPTY) / (RSSI_FULL - RSSI_EMPTY);
  return Math.min(Math.max(level, 0), 1);
}

export interface WifiSignalIconProps {
  // dBm, null when unknown (drawn empty)
  rssi: number | null | undefined;
  disabled?: boolean;
}

export function WifiSignalIcon(props: WifiSignalIconProps) {
  const maskId = createUniqueId();
  const level = () => signalLevel(props.rssi);
  const color = () =>
    props.disabled ? 'text-fg-disabled' : levelColor(level());

  return (
    <svg
      width="16"
      height="16"
      viewBox="0 0 16 13"
      aria-hidden="true"
      class="shrink-0"
    >
      <defs>
        <mask
          id={maskId}
          maskUnits="userSpaceOnUse"
          x="0"
          width="16"
          height="13"
          style={{ 'mask-type': 'alpha' }}
        >
          <path d={WEDGE} />
        </mask>
      </defs>
      <path d={BACK} class="fill-fg-disabled" />
      <g mask={`url(#${maskId})`} class={cx('fill-current', color())}>
        <path
          d={WEDGE}
          style={{ transform: `translateY(${(1 - level()) * 13}px)` }}
        />
      </g>
    </svg>
  );
}
