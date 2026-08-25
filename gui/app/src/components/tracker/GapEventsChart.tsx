import classNames from 'classnames';
import {
  Dispatch,
  memo,
  SetStateAction,
  useEffect,
  useRef,
  useState,
} from 'react';
import { Typography } from '@/components/commons/Typography';
import { ChartRow, GapEvent } from '@/hooks/telemetry-history';
import {
  flipTooltipLeft,
  getGapColor,
  HoveredChart,
  isGapEventActive,
  relativeTimeTick,
  screenXToTimeSec,
  TelemetryTracker,
  timeSecToScreenX,
} from './DongleTelemetry';
import {
  TelemetryPopoverTooltip,
  TOOLTIP_OFFSET,
  TOOLTIP_WIDTH,
} from './TelemetryTooltip';

const Y_AXIS_WIDTH = 64;
const RIGHT_AXIS_WIDTH = 48;
const ROW_HEIGHT = 24;
const BAR_HEIGHT = 14;
const XAXIS_HEIGHT = 20;
const TOP_PADDING = 8;

function RowLabels({
  trackers,
  visibleIds,
}: {
  trackers: TelemetryTracker[];
  visibleIds: number[];
}) {
  return (
    <div className="shrink-0" style={{ width: Y_AXIS_WIDTH }}>
      {trackers.map((t) => (
        <div
          key={t.deviceId}
          className={classNames(
            'flex items-center gap-2 pl-1 text-[9.5px] font-bold text-white uppercase transition-opacity',
            { 'opacity-[0.28]': !visibleIds.includes(t.deviceId) }
          )}
          style={{ height: ROW_HEIGHT }}
          title={t.name}
        >
          <span
            className="w-1.5 h-1.5 rounded-full inline-block shrink-0"
            style={{ background: t.color }}
          />
          <span className="truncate">{t.name}</span>
        </div>
      ))}
    </div>
  );
}

function GapEventsChartComponent({
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
  variant = 'default',
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
  variant?: 'default' | 'modal';
}) {
  const wrapperRef = useRef<HTMLDivElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);

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

    const plotLeft = 0;
    const plotRight = width - RIGHT_AXIS_WIDTH;
    const plotWidth = plotRight - plotLeft;
    const plotTop = TOP_PADDING;
    const plotBottom = height - XAXIS_HEIGHT;

    const timeToPx = (t: number) => {
      const ratio = (t - startSec) / (endSec - startSec);
      return plotLeft + ratio * plotWidth;
    };

    ctx.strokeStyle = 'rgba(255, 255, 255, 0.04)';
    ctx.lineWidth = 1;
    trackers.forEach((_, i) => {
      const y = plotTop + (i + 1) * ROW_HEIGHT;
      ctx.beginPath();
      ctx.moveTo(plotLeft, y);
      ctx.lineTo(plotRight, y);
      ctx.stroke();
    });

    ctx.save();
    ctx.beginPath();
    ctx.rect(plotLeft, plotTop, plotWidth, plotBottom - plotTop);
    ctx.clip();

    trackers.forEach((t, rowIdx) => {
      if (!visibleIds.includes(t.deviceId)) return;
      const yCenter = plotTop + rowIdx * ROW_HEIGHT + ROW_HEIGHT / 2;
      const yTop = yCenter - BAR_HEIGHT / 2;
      const events = eventsByTracker[t.deviceId] ?? [];

      events.forEach((ev) => {
        const tStartSec = (ev.t - ev.durationMs) / 1000;
        const tEndSec = ev.t / 1000;
        const x1 = timeToPx(tStartSec);
        const x2 = timeToPx(tEndSec);
        const w = Math.max(4, x2 - x1);

        if (x2 < plotLeft || x1 > plotRight) return;

        const isHovered =
          hoveredTime != null && isGapEventActive(ev, hoveredTime);

        ctx.fillStyle = getGapColor(ev.durationMs);
        ctx.globalAlpha = isHovered ? 1.0 : 0.85;

        ctx.beginPath();
        const r = 2;
        ctx.roundRect(x1, yTop, w, BAR_HEIGHT, r);
        ctx.fill();

        if (isHovered) {
          ctx.strokeStyle = '#ffffff';
          ctx.lineWidth = 2;
          ctx.stroke();
        }
      });
    });
    ctx.globalAlpha = 1.0;
    ctx.restore();

    // 3. Draw X-axis tick labels
    ctx.font = '11px Poppins, sans-serif';
    ctx.fillStyle = '#ffffff';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'top';
    xAxisTicks.forEach((tSec) => {
      const x = timeToPx(tSec);
      if (x >= plotLeft && x <= plotRight) {
        ctx.fillText(relativeTimeTick(tSec, endSec), x, plotBottom + 4);
      }
    });

    if (hoveredTime != null) {
      const hoverX = timeToPx(hoveredTime);
      if (hoverX >= plotLeft && hoverX <= plotRight) {
        ctx.strokeStyle = 'rgba(255, 255, 255, 0.6)';
        ctx.lineWidth = 1.5;
        ctx.beginPath();
        ctx.moveTo(hoverX, plotTop);
        ctx.lineTo(hoverX, plotBottom);
        ctx.stroke();
      }
    }

    ctx.restore();
  }, [
    trackers,
    visibleIds,
    eventsByTracker,
    startSec,
    endSec,
    xAxisTicks,
    hoveredTime,
  ]);

  const [hoveredY, setHoveredY] = useState<number | null>(null);

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const canvasRect = canvas.getBoundingClientRect();
    const plotLeftScreen = canvasRect.left;
    const plotWidth = canvasRect.width - RIGHT_AXIS_WIDTH;
    onHoveredTimeChange(
      screenXToTimeSec(e.clientX, plotLeftScreen, plotWidth, startSec, endSec)
    );
    setHoveredY(e.clientY);
    onActiveChange('gap');
  };

  let clientPos: { x: number; y: number } | null = null;
  if (hoveredTime != null && canvasRef.current) {
    const canvasRect = canvasRef.current.getBoundingClientRect();
    const plotLeftScreen = canvasRect.left;
    const plotWidth = canvasRect.width - RIGHT_AXIS_WIDTH;
    const screenX = timeSecToScreenX(
      hoveredTime,
      plotLeftScreen,
      plotWidth,
      startSec,
      endSec
    );
    const tooltipX = flipTooltipLeft(
      screenX,
      window.innerWidth,
      TOOLTIP_WIDTH,
      TOOLTIP_OFFSET
    );

    clientPos = {
      x: tooltipX,
      y: (hoveredY ?? canvasRect.top) + TOOLTIP_OFFSET,
    };
  }

  return (
    <div
      className={classNames(
        'bg-background-80 rounded-xl',
        variant === 'modal' ? 'p-3' : 'p-4'
      )}
    >
      <div className="pl-[64px] pb-2">
        <Typography
          variant="section-title"
          id={'dongle-settings-telemetry-chart_gaps'}
        />
      </div>
      <div className="relative">
        <div
          ref={wrapperRef}
          className="flex"
          style={{
            height: trackers.length * ROW_HEIGHT + XAXIS_HEIGHT + TOP_PADDING,
          }}
          onMouseMove={handleMouseMove}
          onMouseLeave={() => {
            onHoveredTimeChange(null);
            setHoveredY(null);
            onActiveChange((prev) => (prev === 'gap' ? null : prev));
          }}
        >
          <RowLabels trackers={trackers} visibleIds={visibleIds} />
          <div className="flex-1 relative">
            <canvas ref={canvasRef} className="w-full h-full block" />
          </div>
        </div>

        <TelemetryPopoverTooltip
          isOpen={isActive && hoveredTime != null && clientPos != null}
          clientPos={clientPos}
          trackers={trackers}
          chartData={chartData}
          hoveredTime={hoveredTime}
          eventsByTracker={eventsByTracker}
        />
      </div>
    </div>
  );
}

export const GapEventsChart = memo(GapEventsChartComponent);
