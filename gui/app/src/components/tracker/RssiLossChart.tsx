import {
  Dispatch,
  memo,
  SetStateAction,
  useEffect,
  useRef,
  useState,
} from 'react';
import { ChartRow, GapEvent } from '@/hooks/telemetry-history';
import {
  flipTooltipLeft,
  HoveredChart,
  pxToTimeSec,
  relativeTimeTick,
  TelemetryTracker,
  timeSecToPx,
} from './DongleTelemetry';
import {
  estimatedTooltipHeight,
  TelemetryTooltipTable,
  TOOLTIP_OFFSET,
  TOOLTIP_WIDTH,
} from './TelemetryTooltip';

const Y_AXIS_WIDTH = 48;
const RIGHT_AXIS_WIDTH = 40;
const XAXIS_HEIGHT = 20;
const TOP_PADDING = 12;

function RssiLossChartComponent({
  chartData,
  trackers,
  visibleIds,
  eventsByTracker,
  startSec,
  endSec,
  xAxisTicks,
  hoveredTime,
  onHoveredTimeChange,
  isActive,
  onActiveChange,
}: {
  chartData: ChartRow[];
  trackers: TelemetryTracker[];
  visibleIds: number[];
  eventsByTracker: Record<number, GapEvent[]>;
  startSec: number;
  endSec: number;
  xAxisTicks: number[];
  hoveredTime: number | null;
  onHoveredTimeChange: (t: number | null) => void;
  isActive: boolean;
  onActiveChange: Dispatch<SetStateAction<HoveredChart>>;
}) {
  const visible = trackers.filter((t) => visibleIds.includes(t.deviceId));
  const wrapperRef = useRef<HTMLDivElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [hoveredY, setHoveredY] = useState<number | null>(null);

  // High-performance Canvas 2D rendering loop (ultra-low CPU)
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const rect = canvas.getBoundingClientRect();
    const dpr = window.devicePixelRatio || 1;
    const width = rect.width;
    const height = rect.height;

    if (canvas.width !== width * dpr || canvas.height !== height * dpr) {
      canvas.width = width * dpr;
      canvas.height = height * dpr;
    }

    ctx.save();
    ctx.scale(dpr, dpr);
    ctx.clearRect(0, 0, width, height);

    const plotLeft = Y_AXIS_WIDTH;
    const plotRight = width - RIGHT_AXIS_WIDTH;
    const plotWidth = plotRight - plotLeft;
    const plotTop = TOP_PADDING;
    const plotBottom = height - XAXIS_HEIGHT;
    const plotHeight = plotBottom - plotTop;

    const timeToPx = (t: number) => {
      const ratio = (t - startSec) / (endSec - startSec);
      return plotLeft + ratio * plotWidth;
    };

    const rssiToPy = (rssi: number) => {
      const clamped = Math.max(-80, Math.min(-20, rssi));
      const ratio = (clamped - -80) / (-20 - -80);
      return plotBottom - ratio * plotHeight;
    };

    const lossToPy = (loss: number) => {
      const clamped = Math.max(0, Math.min(100, loss));
      const ratio = clamped / 100;
      return plotBottom - ratio * plotHeight;
    };

    // 1. Draw horizontal grid lines
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.08)';
    ctx.lineWidth = 1;
    const rssiTicks = [-80, -60, -40, -20];
    rssiTicks.forEach((val) => {
      const y = rssiToPy(val);
      ctx.beginPath();
      ctx.moveTo(plotLeft, y);
      ctx.lineTo(plotRight, y);
      ctx.stroke();
    });

    // 2. Draw Y-axis tick labels
    ctx.font = '11px Poppins, sans-serif';
    ctx.textBaseline = 'middle';

    // Left Y-axis (RSSI)
    ctx.fillStyle = '#ffffff';
    ctx.textAlign = 'right';
    rssiTicks.forEach((val) => {
      const y = rssiToPy(val);
      ctx.fillText(`${val}dBm`, plotLeft - 6, y);
    });

    // Right Y-axis (Packet Loss)
    ctx.textAlign = 'left';
    const lossTicks = [0, 25, 50, 75, 100];
    lossTicks.forEach((val) => {
      const y = lossToPy(val);
      ctx.fillText(`${val}%`, plotRight + 6, y);
    });

    // 3. Draw X-axis tick labels
    ctx.textAlign = 'center';
    ctx.textBaseline = 'top';
    ctx.fillStyle = '#ffffff';
    xAxisTicks.forEach((tSec) => {
      const x = timeToPx(tSec);
      if (x >= plotLeft && x <= plotRight) {
        ctx.fillText(relativeTimeTick(tSec, endSec), x, plotBottom + 4);
      }
    });

    // 4. Clip plot area to prevent line spillover on left and right margins
    ctx.save();
    ctx.beginPath();
    ctx.rect(plotLeft, plotTop, plotWidth, plotHeight);
    ctx.clip();

    // Draw RSSI lines (solid 2px)
    visible.forEach((t) => {
      const key = `${t.deviceId}_avg`;
      ctx.strokeStyle = t.color;
      ctx.lineWidth = 2;
      ctx.setLineDash([]);
      ctx.beginPath();

      let drawing = false;
      for (let i = 0; i < chartData.length; i++) {
        const row = chartData[i];
        const val = row[key];
        if (val != null) {
          const x = timeToPx(row.t);
          const y = rssiToPy(Number(val));
          if (!drawing) {
            ctx.moveTo(x, y);
            drawing = true;
          } else {
            ctx.lineTo(x, y);
          }
        } else if (drawing) {
          ctx.stroke();
          ctx.beginPath();
          drawing = false;
        }
      }
      if (drawing) {
        ctx.stroke();
      }
    });

    // Draw Packet Loss lines (dashed 1.5px)
    visible.forEach((t) => {
      const key = `${t.deviceId}_loss`;
      ctx.strokeStyle = t.color;
      ctx.lineWidth = 1.5;
      ctx.setLineDash([4, 4]);
      ctx.beginPath();

      let drawing = false;
      for (let i = 0; i < chartData.length; i++) {
        const row = chartData[i];
        const val = row[key];
        if (val != null) {
          const x = timeToPx(row.t);
          const y = lossToPy(Number(val));
          if (!drawing) {
            ctx.moveTo(x, y);
            drawing = true;
          } else {
            ctx.lineTo(x, y);
          }
        } else if (drawing) {
          ctx.stroke();
          ctx.beginPath();
          drawing = false;
        }
      }
      if (drawing) {
        ctx.stroke();
      }
    });
    ctx.setLineDash([]);
    ctx.restore();

    // 6. Draw vertical hover line
    if (hoveredTime != null) {
      const hoverX = timeToPx(hoveredTime);
      if (hoverX >= plotLeft && hoverX <= plotRight) {
        ctx.strokeStyle = 'rgba(255, 255, 255, 0.4)';
        ctx.lineWidth = 1.5;
        ctx.beginPath();
        ctx.moveTo(hoverX, plotTop);
        ctx.lineTo(hoverX, plotBottom);
        ctx.stroke();
      }
    }

    ctx.restore();
  }, [chartData, visible, startSec, endSec, xAxisTicks, hoveredTime]);

  let tooltipLeftPx: number | null = null;
  let tooltipTopPx = 8;
  if (hoveredTime != null && wrapperRef.current) {
    const rect = wrapperRef.current.getBoundingClientRect();
    const hoveredPx = timeSecToPx(hoveredTime, rect.width, startSec, endSec);
    tooltipLeftPx = flipTooltipLeft(
      hoveredPx,
      rect.width,
      TOOLTIP_WIDTH,
      TOOLTIP_OFFSET
    );

    if (hoveredY != null) {
      tooltipTopPx = Math.max(
        0,
        Math.min(
          hoveredY + TOOLTIP_OFFSET,
          rect.height - estimatedTooltipHeight(trackers.length)
        )
      );
    }
  }

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    if (!wrapperRef.current) return;
    const rect = wrapperRef.current.getBoundingClientRect();
    onHoveredTimeChange(pxToTimeSec(e.clientX, rect, startSec, endSec));
    setHoveredY(e.clientY - rect.top);
    onActiveChange('rssi');
  };

  return (
    <div
      ref={wrapperRef}
      className="relative w-full h-full"
      onMouseMove={handleMouseMove}
      onMouseLeave={() => {
        onHoveredTimeChange(null);
        setHoveredY(null);
        onActiveChange((prev) => (prev === 'rssi' ? null : prev));
      }}
    >
      <canvas ref={canvasRef} className="w-full h-full block" />

      {isActive && hoveredTime != null && tooltipLeftPx != null && (
        <div
          className="absolute top-0 left-0 pointer-events-none z-30"
          style={{
            transform: `translate(${tooltipLeftPx}px, ${tooltipTopPx}px)`,
          }}
        >
          <div
            className="bg-background-90/95 border border-background-10/10 rounded-lg px-3 py-2 max-h-[360px] overflow-y-auto"
            style={{ width: TOOLTIP_WIDTH }}
          >
            <TelemetryTooltipTable
              trackers={trackers}
              chartData={chartData}
              hoveredTime={hoveredTime}
              eventsByTracker={eventsByTracker}
            />
          </div>
        </div>
      )}
    </div>
  );
}

export const RssiLossChart = memo(RssiLossChartComponent);
