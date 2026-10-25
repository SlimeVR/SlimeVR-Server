import { createUniqueId, Match, Switch } from 'solid-js';
import { cx } from '@/lib/cx';

const BODY =
  'M11.0833 0H1.31203C0.995003 0.00131561 0.691347 0.121213 0.467167 0.333594C0.242986 0.545976 0.116428 0.83365 0.115039 1.134V7.383C0.114754 7.68458 0.240506 7.97399 0.464808 8.18799C0.689109 8.40198 0.993714 8.52315 1.31203 8.525H11.0833V0Z';
const CAP =
  'M15.0005 8.525C15.3175 8.52368 15.6212 8.40379 15.8454 8.19141C16.0696 7.97902 16.1961 7.69135 16.1975 7.391V5.968H17.9972V2.558H16.1975V1.134C16.1961 0.83365 16.0696 0.545976 15.8454 0.333594C15.6212 0.121213 15.3175 0.00131561 15.0005 0H10.9672V8.525H15.0005Z';
const BOLT =
  'M 7.7638355,8.4189633 8.0112251,4.9834646 5.7712838,4.9834645 8.5644084,0.07977871 8.3170195,3.5152773 H 10.55696 Z';
const CHECK =
  'M 5.5342464,4.6225095 C 6.1777799,5.0106205 6.6131537,5.2516456 7.5253371,6.545223 8.4340868,4.4016445 8.7809738,3.661475 10.605195,1.5520288';

export function levelColor(level: number): string {
  if (level > 0.4) return 'text-success';
  if (level > 0.2) return 'text-warning';
  if (level > 0) return 'text-critical';
  return 'text-fg';
}

export interface BatteryIconProps {
  value: number;
  charging?: boolean;
  disabled?: boolean;
}

export function BatteryIcon(props: BatteryIconProps) {
  const shapeId = createUniqueId();
  const holeId = createUniqueId();
  const level = () =>
    props.value > 2 ? 0 : Math.min(Math.max(props.value, 0), 1);
  const full = () => props.value > 1 && props.value <= 2;
  const color = () => {
    if (props.disabled) return 'text-fg-disabled';
    if (props.charging) return 'text-success';
    return levelColor(level());
  };

  return (
    <svg
      width="19"
      height="11"
      viewBox="0 0 19 9"
      aria-hidden="true"
      class="shrink-0"
    >
      <defs>
        <mask
          id={shapeId}
          maskUnits="userSpaceOnUse"
          x="0"
          y="0"
          width="18"
          height="9"
          style={{ 'mask-type': 'alpha' }}
        >
          <path d={BODY} />
          <path d={CAP} />
        </mask>
        <mask
          id={holeId}
          maskUnits="userSpaceOnUse"
          x="0"
          y="0"
          width="19"
          height="9"
        >
          <rect width="19" height="9" fill="white" />
          <Switch>
            <Match when={props.charging && full()}>
              <path
                d={CHECK}
                fill="none"
                stroke="black"
                stroke-width="1.5"
                stroke-linecap="square"
              />
            </Match>
            <Match when={props.charging}>
              <path d={BOLT} fill="black" />
            </Match>
          </Switch>
        </mask>
      </defs>
      <g mask={`url(#${holeId})`}>
        <g class="fill-fg-disabled">
          <path d={BODY} />
          <path d={CAP} />
        </g>
        <g mask={`url(#${shapeId})`} class={cx('fill-current', color())}>
          <rect width={props.charging ? 18 : level() * 18} height="9" />
        </g>
      </g>
    </svg>
  );
}
