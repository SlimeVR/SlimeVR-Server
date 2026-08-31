import classNames from 'classnames';
import { useMemo, useRef, useState } from 'react';
import { useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { PauseIcon } from '@/components/commons/icon/PauseIcon';
import { PlayIcon } from '@/components/commons/icon/PlayIcon';
import { DropdownInside } from '@/components/commons/Dropdown';
import { CheckboxInternal } from '@/components/commons/Checkbox';
import { getLocalizedTrackerName } from '@/hooks/tracker';
import { useDongleTelemetryFeed } from '@/hooks/dongle-telemetry-feed';
import { FlatDeviceTracker } from '@/store/app-store';
import { RssiLossChart } from './RssiLossChart';
import { GapEventsChart } from './GapEventsChart';
import { defaultDongleTelemetryConfig, useConfig } from '@/hooks/config';

import { GapEvent } from '@/hooks/telemetry-history';

export interface TelemetryTracker {
  deviceId: number;
  name: string;
  color: string;
}

export type HoveredChart = 'rssi' | 'gap' | null;

export const SERIES_COLORS = [
  '#3987e5',
  '#d95926',
  '#199e70',
  '#c98500',
  '#d55181',
  '#008300',
  '#9085e9',
  '#e66767',
  '#00a3e0',
  '#e59400',
];

export const WINDOW_OPTIONS = [
  { sec: 10, label: '10s', step: 5 },
  { sec: 30, label: '30s', step: 10 },
  { sec: 60, label: '1m', step: 15 },
  { sec: 120, label: '2m', step: 30 },
  { sec: 300, label: '5m', step: 60 },
];

export const CHART_MARGIN = { top: 8, right: 12, bottom: 4, left: 0 };
export const Y_AXIS_WIDTH = 64;
export const RIGHT_AXIS_WIDTH = 48;

export function relativeTimeTick(v: number, endSec: number) {
  const diff = Math.round(v - endSec);
  if (diff === 0) return '0s';
  if (Math.abs(diff) >= 60 && Math.abs(diff) % 60 === 0) {
    return `${diff / 60}m`;
  }
  return `${diff}s`;
}

export function screenXToTimeSec(
  clientX: number,
  plotLeftScreen: number,
  plotWidth: number,
  startSec: number,
  endSec: number
): number {
  if (plotWidth <= 0) return startSec;
  const pct = Math.max(0, Math.min(1, (clientX - plotLeftScreen) / plotWidth));
  return startSec + pct * (endSec - startSec);
}

export function timeSecToScreenX(
  t: number,
  plotLeftScreen: number,
  plotWidth: number,
  startSec: number,
  endSec: number
): number {
  if (endSec <= startSec) return plotLeftScreen;
  const pct = Math.max(0, Math.min(1, (t - startSec) / (endSec - startSec)));
  return plotLeftScreen + pct * plotWidth;
}

export function flipTooltipLeft(
  clientX: number,
  screenWidth: number,
  tooltipWidth: number,
  offset: number
): number {
  const wouldOverflow = clientX + offset + tooltipWidth > screenWidth;
  return wouldOverflow ? clientX - offset - tooltipWidth : clientX + offset;
}

export function isGapEventActive(
  ev: GapEvent,
  hoveredTime: number,
  paddingSec = 0.25
): boolean {
  const tStartSec = (ev.t - ev.durationMs) / 1000;
  const tEndSec = ev.t / 1000;
  return (
    hoveredTime >= tStartSec - paddingSec && hoveredTime <= tEndSec + paddingSec
  );
}

export function getGapColor(durationMs: number): string {
  if (durationMs <= 30) {
    return 'hsl(48, 95%, 52%)'; // Amber Yellow
  }
  if (durationMs <= 200) {
    const ratio = (durationMs - 30) / 170;
    const hue = 48 - ratio * 48; // Amber -> Orange -> Red
    return `hsl(${Math.round(hue)}, 92%, 50%)`;
  }
  const ratio = Math.min(1, (durationMs - 200) / 800);
  const hue = (360 - ratio * 50) % 360; // Red -> Crimson -> Magenta
  return `hsl(${Math.round(hue)}, 85%, 48%)`;
}

export function getGapTextColor(durationMs: number): string {
  if (durationMs <= 30) {
    return 'hsl(48, 95%, 65%)'; // High-contrast Amber Yellow
  }
  if (durationMs <= 200) {
    const ratio = (durationMs - 30) / 170;
    const hue = 48 - ratio * 48; // Amber -> Orange -> Light Red
    return `hsl(${Math.round(hue)}, 95%, 65%)`;
  }
  const ratio = Math.min(1, (durationMs - 200) / 800);
  const hue = (360 - ratio * 50) % 360; // Light Red -> Vibrant Coral -> Bright Magenta
  return `hsl(${Math.round(hue)}, 90%, 68%)`;
}

export function DongleTelemetry({
  trackers,
  variant = 'default',
}: {
  trackers: FlatDeviceTracker[];
  variant?: 'default' | 'modal';
}) {
  const { l10n } = useLocalization();

  const colorsRef = useRef<Map<number, string>>(new Map());
  const colorFor = (deviceId: number): string => {
    const colors = colorsRef.current;
    let color = colors.get(deviceId);
    if (!color) {
      color = SERIES_COLORS[colors.size % SERIES_COLORS.length];
      colors.set(deviceId, color);
    }
    return color;
  };

  const list = useMemo<TelemetryTracker[]>(
    () =>
      trackers
        .filter((t) => t.device)
        .map((t) => ({
          deviceId: t.device!.id,
          name: String(getLocalizedTrackerName(l10n, t.tracker.info)),
          color: colorFor(t.device!.id),
        })),
    [trackers, l10n]
  );

  const trackerKey = list
    .map((t) => t.deviceId)
    .sort((a, b) => a - b)
    .join(',');

  const { config, setConfig } = useConfig();
  const telemetryConfig =
    config?.dongleTelemetry ?? defaultDongleTelemetryConfig;

  const windowSec = telemetryConfig.windowSec;
  const disabledTrackerIds = telemetryConfig.disabledTrackerIds ?? [];

  const visibleIds = useMemo(
    () =>
      list
        .map((t) => t.deviceId)
        .filter((id) => !disabledTrackerIds.includes(id)),
    [list, disabledTrackerIds]
  );

  const handleSetVisibleIds = (values: number[]) => {
    const allIds = list.map((t) => t.deviceId);
    const disabled = allIds.filter((id) => !values.includes(id));
    setConfig({
      dongleTelemetry: {
        ...telemetryConfig,
        disabledTrackerIds: disabled,
      },
    });
  };

  const handleSetWindowSec = (sec: number) => {
    setConfig({
      dongleTelemetry: {
        ...telemetryConfig,
        windowSec: sec,
      },
    });
  };

  const showMinMax = telemetryConfig.showMinMax ?? true;

  const handleToggleMinMax = () => {
    setConfig({
      dongleTelemetry: {
        ...telemetryConfig,
        showMinMax: !showMinMax,
      },
    });
  };

  const [live, setLive] = useState(true);
  const [hoveredTime, setHoveredTime] = useState<number | null>(null);
  const [hoveredChart, setHoveredChart] = useState<HoveredChart>(null);

  const trackerIds = useMemo(() => list.map((t) => t.deviceId), [trackerKey]);
  const { chartData, gapEvents, displayStats, nowSec } = useDongleTelemetryFeed(
    trackerIds,
    visibleIds,
    windowSec,
    live
  );

  const stepSec = WINDOW_OPTIONS.find((w) => w.sec === windowSec)?.step ?? 60;
  const endSec = nowSec;
  const startSec = endSec - windowSec;

  const fixedEndTick = Math.floor(endSec / stepSec) * stepSec;
  const xAxisTicks = useMemo(() => {
    const ticks: number[] = [];
    for (
      let t = fixedEndTick - windowSec;
      t <= fixedEndTick + stepSec;
      t += stepSec
    ) {
      ticks.push(t);
    }
    return ticks;
  }, [fixedEndTick, windowSec, stepSec]);

  if (list.length === 0) return null;

  return (
    <div
      className={classNames(
        'flex flex-col bg-background-70 rounded-lg  gap-3',
        { 'p-5': variant == 'default' }
      )}
    >
      <div className="flex items-center justify-between gap-4 flex-wrap">
        <Typography
          variant="section-title"
          id="dongle-settings-telemetry-title"
        />
        <div className="flex items-center gap-3 flex-wrap">
          <DropdownInside
            name="dongle-telemetry-trackers"
            variant="quaternary"
            display="fit"
            multiple
            value={visibleIds.map(String)}
            onChange={(values) => handleSetVisibleIds(values.map(Number))}
            items={list.map((t) => ({
              value: String(t.deviceId),
              label: (
                <div className="flex items-center gap-2 min-w-[160px]">
                  <span
                    className="w-2 h-2 rounded-full shrink-0"
                    style={{ background: t.color }}
                  />
                  <span className="flex-1 truncate">{t.name}</span>
                  <span className="text-background-30 font-mono tabular-nums">
                    {displayStats[t.deviceId]?.rssi != null
                      ? `${displayStats[t.deviceId]!.rssi} dBm`
                      : '--'}
                  </span>
                </div>
              ),
            }))}
            placeholder={l10n.getString(
              'dongle-settings-telemetry-select_trackers'
            )}
            renderValue={() => (
              <div className="flex items-center gap-2">
                <div className="flex gap-[3px]">
                  {list.map((t) => (
                    <span
                      key={t.deviceId}
                      className={classNames(
                        'w-1.5 h-1.5 rounded-full transition-opacity',
                        {
                          'opacity-100': visibleIds.includes(t.deviceId),
                          'opacity-25': !visibleIds.includes(t.deviceId),
                        }
                      )}
                      style={{ background: t.color }}
                    />
                  ))}
                </div>
                <Typography
                  id="dongle-settings-telemetry-select_trackers-summary"
                  vars={{ count: visibleIds.length, total: list.length }}
                />
              </div>
            )}
          />

          <div className="flex items-center px-2">
            <CheckboxInternal
              name="showMinMax"
              variant="toggle"
              label={l10n.getString('dongle-settings-telemetry-show_min_max')}
              checked={showMinMax}
              onChange={handleToggleMinMax}
            />
          </div>

          <div className="flex bg-background-80 rounded-md p-2 gap-0.5">
            {WINDOW_OPTIONS.map((w) => (
              <button
                key={w.sec}
                type="button"
                onClick={() => handleSetWindowSec(w.sec)}
                className={classNames(
                  'text-standard-bold px-2.5 py-1.5 rounded',
                  windowSec === w.sec
                    ? 'bg-accent-background-30 text-background-10'
                    : 'text-background-30 hover:text-background-10'
                )}
              >
                {w.label}
              </button>
            ))}
          </div>

          <div
            className="bg-background-50 hover:bg-background-40 rounded-full cursor-pointer w-10 h-10 flex items-center justify-center fill-background-10"
            onClick={() => setLive((v) => !v)}
          >
            {live ? <PauseIcon width={12} /> : <PlayIcon width={12} />}
          </div>
        </div>
      </div>

      <div
        className={classNames(
          'flex flex-col',
          variant === 'modal' ? 'gap-4' : 'gap-4'
        )}
      >
        <div
          className={classNames(
            'bg-background-80 rounded-xl',
            variant === 'modal' ? 'p-3' : 'p-4'
          )}
        >
          <div className="flex items-center justify-between pl-[64px] pr-[48px] pb-2">
            <div className="flex items-center gap-2">
              <svg width={20} height={10} className="shrink-0">
                <line
                  x1={0}
                  y1={5}
                  x2={20}
                  y2={5}
                  stroke="rgb(var(--background-10))"
                  strokeWidth={2}
                />
              </svg>
              <Typography
                variant="section-title"
                id="dongle-settings-telemetry-chart_rssi"
              />
            </div>
            <div className="flex items-center gap-2">
              <svg width={20} height={10} className="shrink-0">
                <line
                  x1={0}
                  y1={5}
                  x2={20}
                  y2={5}
                  stroke="rgb(var(--background-10))"
                  strokeWidth={1.5}
                  strokeDasharray="4 3"
                />
              </svg>
              <Typography
                variant="section-title"
                id="dongle-settings-telemetry-chart_loss"
              />
            </div>
          </div>
          <div
            className={
              variant === 'modal'
                ? 'h-[min(440px,45vh)] min-h-[220px]'
                : 'h-[440px]'
            }
          >
            <RssiLossChart
              chartData={chartData}
              trackers={list}
              visibleIds={visibleIds}
              eventsByTracker={gapEvents}
              startSec={startSec}
              endSec={endSec}
              xAxisTicks={xAxisTicks}
              hoveredTime={hoveredTime}
              onHoveredTimeChange={setHoveredTime}
              isActive={hoveredChart === 'rssi'}
              onActiveChange={setHoveredChart}
              showMinMax={showMinMax}
            />
          </div>
        </div>

        <GapEventsChart
          chartData={chartData}
          trackers={list}
          visibleIds={visibleIds}
          eventsByTracker={gapEvents}
          startSec={startSec}
          endSec={endSec}
          xAxisTicks={xAxisTicks}
          hoveredTime={hoveredTime}
          onHoveredTimeChange={setHoveredTime}
          isActive={hoveredChart === 'gap'}
          onActiveChange={setHoveredChart}
          variant={variant}
        />
      </div>

      <Typography color="secondary" id="dongle-settings-telemetry-footnote" />
    </div>
  );
}
