import { useLocation, useNavigate } from '@solidjs/router';
import { onCleanup } from 'solid-js';
import { setBackFallback } from '@/lib/back-stack';

function depth(): number {
  const state: unknown = window.history.state;
  if (typeof state !== 'object' || state === null || !('_depth' in state)) return 0;
  return typeof state._depth === 'number' ? state._depth : 0;
}

// Router history under the overlays. Stops at the entry the app started on,
// so back never leaves the app.
export function useHistoryBack() {
  const navigate = useNavigate();
  const location = useLocation();
  const start = depth();

  onCleanup(
    setBackFallback({
      can: () => {
        void location.key;
        return depth() > start;
      },
      back: () => navigate(-1),
    })
  );
}
