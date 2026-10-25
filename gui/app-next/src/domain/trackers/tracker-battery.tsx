import { TrackerStatus } from 'solarxr-protocol';
import { createUniqueId, Show } from 'solid-js';
import { debugPref } from '@/domain/developer';
import { fmt, formats } from '@/lib/format';
import { t } from '@/lib/l10n';
import { BatteryIcon } from '@/ui/battery-icon';
import { Tooltip } from '@/ui/tooltip';
import { trackerDisplayPref } from './display-prefs';
import { trackerByKey, trackerDevice, type TrackerKey } from './store';

// Above this voltage the tracker is on a charger
const CHARGING_VOLTS = 4.3;
const MICROS_PER_HOUR = 3_600_000_000n;
const MICROS_PER_MINUTE = 60_000_000n;

function runtimeText(micros: bigint): string {
  return t('tracker-battery-runtime', {
    hours: Number(micros / MICROS_PER_HOUR),
    minutes: Number((micros % MICROS_PER_HOUR) / MICROS_PER_MINUTE),
  });
}

// Battery icon + runtime or percent (+ voltage when enabled). Nothing when the
// device reports no battery.
export function TrackerBattery(props: { trackerKey: TrackerKey }) {
  const tipId = createUniqueId();
  let el: HTMLDivElement | undefined;
  const hw = () => trackerDevice(props.trackerKey)?.hardwareStatus;
  const disconnected = () =>
    trackerByKey(props.trackerKey)?.status === TrackerStatus.DISCONNECTED;

  return (
    <Show when={hw()?.batteryPctEstimate != null && hw()}>
      {(status) => {
        // 0..1 with the over and underflow encodings BatteryIcon understands
        const value = () => status().batteryPctEstimate! / 100;
        const percent = () =>
          value() > 2 ? 0 : Math.min(Math.max(value(), 0), 1);
        const voltage = () => status().batteryVoltage;
        const runtime = () => {
          const micros = status().batteryRuntimeEstimate;
          return micros != null && micros > 0n ? micros : null;
        };
        const charging = () => (voltage() ?? 0) > CHARGING_VOLTS;
        const showVoltage = () =>
          !!voltage() && trackerDisplayPref().showBatteryVoltage;
        const showPercent = () =>
          !charging() && (runtime() == null || debugPref());
        // Runtime replaces the percent, which moves to the tooltip
        const percentTip = () =>
          !charging() && runtime() != null && !debugPref();

        return (
          <>
            <div
              ref={el}
              aria-describedby={percentTip() ? tipId : undefined}
              class="flex items-center gap-sm"
            >
              <BatteryIcon
                value={value()}
                charging={charging()}
                disabled={disconnected()}
              />
              <Show
                when={!charging() || showVoltage()}
                fallback={
                  <span class="flex w-15 items-center">
                    <span class="h-1 w-5 rounded-full bg-fg-disabled" />
                  </span>
                }
              >
                <span class="flex w-15 flex-col whitespace-nowrap">
                  <Show when={!charging() && runtime()}>
                    {(micros) => <span>{runtimeText(micros())}</span>}
                  </Show>
                  <Show when={showPercent()}>
                    <span>{fmt(formats.percent, percent())}</span>
                  </Show>
                  <Show when={showVoltage()}>
                    <span>{fmt(formats.volts, voltage())}</span>
                  </Show>
                </span>
              </Show>
            </div>
            <Tooltip id={tipId} anchor={() => el} placement="left">
              {percentTip() ? fmt(formats.percent, percent()) : undefined}
            </Tooltip>
          </>
        );
      }}
    </Show>
  );
}
