import { useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { Line } from 'react-chartjs-2';
import { TrackerDataT } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { useConfig } from '@/hooks/config';

export function TrackerGraph({ tracker }: { tracker: TrackerDataT }) {
  const { l10n } = useLocalization();
  const { config } = useConfig();

  type AxisData = {
    x: number;
    y: number;
    time: number;
  };

  type ChartData = {
    x: AxisData[];
    y: AxisData[];
    z: AxisData[];
  };

  const [chartData, setChartData] = useState<ChartData>({
    x: [],
    y: [],
    z: [],
  });

  const [showTrackerGraph, setShowTrackerGraph] = useState(false);

  const secondDuration = 60;

  useEffect(() => {
    if (!showTrackerGraph) {
      return;
    }

    const newValue = tracker.info?.isImu
      ? tracker.linearAcceleration
      : tracker.position;
    if (!newValue) {
      return;
    }

    const currentTime = new Date().getTime() / 1000;
    const startTime = currentTime - secondDuration;

    const updateData = (data: AxisData[], newSample: number) => {
      const remapped = data
        .filter((value) => value.time >= startTime)
        .map((value) => ({ ...value, x: value.time - startTime }));
      remapped.push({
        time: currentTime,
        x: secondDuration,
        y: newSample,
      });
      return remapped;
    };

    const newData = {
      x: updateData(chartData.x, newValue.x),
      y: updateData(chartData.y, newValue.y),
      z: updateData(chartData.z, newValue.z),
    };
    setChartData(newData);
  }, [tracker]);

  useEffect(() => {
    if (!showTrackerGraph) {
      setChartData({ x: [], y: [], z: [] });
    }
  }, [showTrackerGraph]);

  return (
    <>
      <Button
        variant="tertiary"
        className="self-start"
        onClick={() => setShowTrackerGraph(!showTrackerGraph)}
      >
        {l10n.getString(
          showTrackerGraph
            ? 'tracker-settings-graph-hide-title'
            : 'tracker-settings-graph-show-title'
        )}
      </Button>
      {showTrackerGraph && (
        <div className="h-96">
          <Line
            options={{
              responsive: true,
              animation: false,
              font: {
                family: config?.fonts.map((font) => `"${font}"`).join(','),
                size: config?.textSize,
              },
              plugins: {
                title: {
                  display: true,
                  text: l10n.getString(
                    tracker?.info?.isImu
                      ? 'tracker-settings-graph-acceleration-title'
                      : 'tracker-settings-graph-position-title'
                  ),
                  color: 'white',
                },
                tooltip: {
                  mode: 'index',
                  intersect: false,
                  animation: false,
                  callbacks: {
                    title: () => '',
                  },
                },
                legend: {
                  labels: {
                    color: 'white',
                  },
                },
              },
              scales: {
                x: {
                  type: 'linear',
                  min: 0,
                  max: secondDuration,
                  ticks: {
                    color: 'white',
                  },
                },
                y: {
                  min: -4,
                  max: 4,
                  ticks: {
                    color: 'white',
                  },
                },
              },
              elements: {
                point: {
                  radius: 0,
                },
              },
              parsing: false,
              normalized: true,
              maintainAspectRatio: false,
            }}
            data={{
              labels: ['X', 'Y', 'Z'],
              datasets: [
                {
                  label: 'X',
                  data: chartData.x,
                  borderColor: 'rgb(200, 50, 50)',
                  backgroundColor: 'rgb(200, 100, 100)',
                },
                {
                  label: 'Y',
                  data: chartData.y,
                  borderColor: 'rgb(50, 200, 50)',
                  backgroundColor: 'rgb(100, 200, 100)',
                },
                {
                  label: 'Z',
                  data: chartData.z,
                  borderColor: 'rgb(50, 50, 200)',
                  backgroundColor: 'rgb(100, 100, 200)',
                },
              ],
            }}
            id="tracker-graph"
          />
        </div>
      )}
    </>
  );
}
