import { createPortal } from 'react-dom';
import { trackerDrag } from '@/hooks/tracker-drag';
import { Typography } from '@/components/commons/Typography';

export function DragGhostLayer() {
  const drag = trackerDrag.useDragGhost();
  if (!drag) return null;

  return createPortal(
    <div
      className="fixed z-50 pointer-events-none -translate-x-1/2 -translate-y-1/2 rounded-lg bg-background-50 outline outline-2 outline-accent-background-40 shadow-lg px-3 py-2 max-w-[220px]"
      style={{ left: drag.x, top: drag.y }}
    >
      <Typography bold truncate>
        {drag.payload.label}
      </Typography>
    </div>,
    document.body
  );
}
