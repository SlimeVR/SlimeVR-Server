import { useState } from 'react';
import { IconButton } from '@/components/commons/IconButton';
import { Typography } from '@/components/commons/Typography';
import { SegmentedControl } from '@/components/commons/SegmentedControl';
import { PauseIcon } from '@/components/commons/icon/PauseIcon';
import { PlayIcon } from '@/components/commons/icon/PlayIcon';
import {
  ChartAxis,
  ChartSeries,
  TimeSeriesChart,
} from '@/components/commons/charts/TimeSeriesChart';
import { useTimeAxisTicks, WINDOW_OPTIONS } from '@/hooks/time-axis';
import {
  ChartSource,
  downsampleMinMax,
  lowerBoundTime,
  nearestIndex,
  rowsSource,
  sourceNow,
  useLiveFrame,
} from '@/hooks/time-series';
import { defaultTrackerGraphsConfig, useConfig } from '@/hooks/config';
import {
  IMU_HISTORY_SEC,
  IMU_KEYS,
  useTrackerImuHistory,
} from '@/hooks/tracker-imu-telemetry';

const MAX_DISPLAY_SLOTS = 400;
const LEFT_AXIS_WIDTH = 64;

const IMU_WINDOW_OPTIONS = WINDOW_OPTIONS.filter(
  (w) => w.sec <= IMU_HISTORY_SEC
);

const AXIS_COLORS = { x: '#e66767', y: '#199e70', z: '#3987e5' };
const AXES = ['x', 'y', 'z'] as const;

const EMPTY_SOURCE = rowsSource([]);

const ACCEL_AXIS: ChartAxis = {
  auto: true,
  symmetric: true,
  minSpan: 2,
  format: (v) => `${v} m/s²`,
};

const ROTATION_AXIS: ChartAxis = {
  min: -180,
  max: 180,
  ticks: [-180, -90, 0, 90, 180],
  format: (v) => `${v} deg`,
};

interface ImuChartDef {
  titleId: string;
  keyPrefix: string;
  axis: ChartAxis;
  series: ChartSeries[];
  unit: string;
  decimals: number;
}

function xyzSeries(keyPrefix: string): ChartSeries[] {
  return AXES.map((a) => ({ key: `${keyPrefix}_${a}`, color: AXIS_COLORS[a] }));
}

const CHARTS: ImuChartDef[] = [
  {
    titleId: 'tracker-settings-imu_graphs-raw_accel',
    keyPrefix: 'raw_acc',
    series: xyzSeries('raw_acc'),
    axis: ACCEL_AXIS,
    unit: ' m/s²',
    decimals: 2,
  },
  {
    titleId: 'tracker-settings-imu_graphs-linear_accel',
    keyPrefix: 'lin_acc',
    series: xyzSeries('lin_acc'),
    axis: ACCEL_AXIS,
    unit: ' m/s²',
    decimals: 2,
  },
  {
    titleId: 'tracker-settings-imu_graphs-raw_rotation',
    keyPrefix: 'raw_rot',
    series: xyzSeries('raw_rot'),
    axis: ROTATION_AXIS,
    unit: 'deg',
    decimals: 1,
  },
  {
    titleId: 'tracker-settings-imu_graphs-rotation',
    keyPrefix: 'rot',
    series: xyzSeries('rot'),
    axis: ROTATION_AXIS,
    unit: 'deg',
    decimals: 1,
  },
];

function ImuTooltip({
  source,
  chart,
  hoveredTime,
}: {
  source: ChartSource;
  chart: ImuChartDef;
  hoveredTime: number;
}) {
  const i = nearestIndex(source, hoveredTime);
  return (
    <table className="w-full text-xs">
      <tbody>
        {AXES.map((a) => {
          const v = i < 0 ? null : source.value(`${chart.keyPrefix}_${a}`, i);
          return (
            <tr key={a}>
              <td className="py-0.5 w-4">
                <span
                  className="w-2 h-2 rounded-full inline-block"
                  style={{ background: AXIS_COLORS[a] }}
                />
              </td>
              <td className="text-background-20 py-0.5 uppercase">{a}</td>
              <td className="text-right font-bold font-mono text-background-10 tabular-nums py-0.5 whitespace-nowrap">
                {v != null ? `${v.toFixed(chart.decimals)}${chart.unit}` : '--'}
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}

function ImuChart({
  chart,
  source,
  startSec,
  endSec,
  xAxisTicks,
  hoveredTime,
  onHoveredTimeChange,
}: {
  chart: ImuChartDef;
  source: ChartSource;
  startSec: number;
  endSec: number;
  xAxisTicks: number[];
  hoveredTime: number | null;
  onHoveredTimeChange: (t: number | null) => void;
}) {
  return (
    <div className="bg-background-80 rounded-xl p-4 flex flex-col gap-2">
      <div
        className="flex items-center justify-between pr-[12px]"
        style={{ paddingLeft: LEFT_AXIS_WIDTH }}
      >
        <Typography variant="section-title" id={chart.titleId} />
        <div className="flex items-center gap-3">
          {AXES.map((a) => (
            <div key={a} className="flex items-center gap-1.5">
              <span
                className="w-2 h-2 rounded-full"
                style={{ background: AXIS_COLORS[a] }}
              />
              <Typography>{a.toUpperCase()}</Typography>
            </div>
          ))}
        </div>
      </div>
      <div className="h-[180px]">
        <TimeSeriesChart
          source={source}
          startSec={startSec}
          endSec={endSec}
          xAxisTicks={xAxisTicks}
          leftAxis={chart.axis}
          series={chart.series}
          hoveredTime={hoveredTime}
          onHoveredTimeChange={onHoveredTimeChange}
          tooltipWidth={180}
          tooltipHeight={80}
          leftAxisWidth={LEFT_AXIS_WIDTH}
          renderTooltip={(t) => (
            <ImuTooltip source={source} chart={chart} hoveredTime={t} />
          )}
        />
      </div>
    </div>
  );
}

export function TrackerImuGraphs({ trackerId }: { trackerId: number }) {
  const { source: history, clock } = useTrackerImuHistory(trackerId);

  const { config, setConfig } = useConfig();
  const graphsConfig = config?.trackerGraphs ?? defaultTrackerGraphsConfig;
  const windowSec = graphsConfig.windowSec;

  const handleSetWindowSec = (sec: number) => {
    setConfig({ trackerGraphs: { ...graphsConfig, windowSec: sec } });
  };

  const [live, setLive] = useState(true);
  const [hoveredTime, setHoveredTime] = useState<number | null>(null);
  const [view, setView] = useState<{ source: ChartSource; nowSec: number }>(
    () => ({ source: EMPTY_SOURCE, nowSec: Date.now() / 1000 })
  );

  const stepSec =
    IMU_WINDOW_OPTIONS.find((w) => w.sec === windowSec)?.step ?? 5;

  useLiveFrame(live, () => {
    const now = sourceNow(clock);
    const from = lowerBoundTime(history, now - windowSec - 1);
    setView({
      source: downsampleMinMax(
        history,
        from,
        windowSec,
        MAX_DISPLAY_SLOTS,
        IMU_KEYS
      ),
      nowSec: now,
    });
  });

  const endSec = view.nowSec;
  const startSec = endSec - windowSec;
  const xAxisTicks = useTimeAxisTicks(endSec, windowSec, stepSec);

  return (
    <div className="flex flex-col bg-background-70 rounded-lg p-5 gap-3">
      <div className="flex items-center justify-between gap-4 flex-wrap">
        <Typography
          variant="section-title"
          id="tracker-settings-imu_graphs-title"
        />
        <div className="flex items-center gap-3 flex-wrap">
          <SegmentedControl
            value={windowSec}
            onChange={handleSetWindowSec}
            options={IMU_WINDOW_OPTIONS.map((w) => ({
              value: w.sec,
              label: w.label,
            }))}
          />
          <IconButton
            labelId={
              live
                ? 'tracker-settings-imu_graphs-live-pause'
                : 'tracker-settings-imu_graphs-live-resume'
            }
            pressed={live}
            className="bg-background-50 hover:bg-background-40 rounded-full cursor-pointer w-10 h-10 flex items-center justify-center fill-background-10"
            onClick={() => setLive((v) => !v)}
          >
            {live ? <PauseIcon width={12} /> : <PlayIcon width={12} />}
          </IconButton>
        </div>
      </div>

      <div className="grid grid-cols-2 mobile:grid-cols-1 xs:grid-cols-1 gap-3">
        {CHARTS.map((chart) => (
          <ImuChart
            key={chart.keyPrefix}
            chart={chart}
            source={view.source}
            startSec={startSec}
            endSec={endSec}
            xAxisTicks={xAxisTicks}
            hoveredTime={hoveredTime}
            onHoveredTimeChange={setHoveredTime}
          />
        ))}
      </div>
    </div>
  );
}
