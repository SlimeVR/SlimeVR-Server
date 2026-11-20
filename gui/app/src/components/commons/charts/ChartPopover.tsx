import { ReactNode, useEffect, useRef } from 'react';

export const TOOLTIP_OFFSET = 10;
const MAX_TOOLTIP_HEIGHT = 360;

export function ChartPopover({
  clientPos,
  width,
  estimatedHeight,
  children,
}: {
  clientPos: { x: number; y: number } | null;
  width: number;
  estimatedHeight: number;
  children: ReactNode;
}) {
  const popoverRef = useRef<HTMLDivElement | null>(null);
  const active = clientPos != null;

  useEffect(() => {
    const el = popoverRef.current as
      | (HTMLDivElement & {
          showPopover?: () => void;
          hidePopover?: () => void;
        })
      | null;
    if (!el?.showPopover) return;

    try {
      const isOpenNow = el.matches(':popover-open');

      if (active && !isOpenNow) {
        el.showPopover();
      } else if (!active && isOpenNow) {
        el.hidePopover?.();
      }
    } catch {
      // Safe fallback if popover API fails or is unsupported
    }
  }, [active]);

  const height = Math.min(MAX_TOOLTIP_HEIGHT, estimatedHeight);

  const clampedX = clientPos
    ? Math.max(10, Math.min(clientPos.x, window.innerWidth - width - 10))
    : -9999;
  const clampedY = clientPos
    ? Math.max(10, Math.min(clientPos.y, window.innerHeight - height - 10))
    : -9999;

  return (
    <div
      ref={popoverRef}
      {...({ popover: 'manual' } as any)}
      style={{
        position: 'fixed',
        left: `${clampedX}px`,
        top: `${clampedY}px`,
        margin: 0,
        inset: 'unset',
      }}
      className="border-0 bg-transparent p-0 outline-none backdrop:bg-transparent pointer-events-none"
    >
      {active && (
        <div
          className="bg-background-90/95 border border-background-10/10 rounded-lg px-3 py-2 shadow-2xl max-h-[360px] overflow-y-auto text-background-10"
          style={{ width }}
        >
          {children}
        </div>
      )}
    </div>
  );
}
