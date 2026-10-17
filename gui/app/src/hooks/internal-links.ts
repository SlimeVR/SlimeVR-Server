import { useEffect } from 'react';

/**
 * Ctrl / Shift / middle click is the browser's "open in a new window" gesture.
 * On an internal link that only spawns a useless second app window. Swallow the
 * modified event and re-fire a plain click on the same anchor, so React Router
 * (or a bare hash link) navigates in-app and keeps any `state` the Link carries.
 * External links and explicit `target="_blank"` are left alone.
 */
export function useInternalLinkGuard() {
  useEffect(() => {
    const onClick = (e: MouseEvent) => {
      if (e.defaultPrevented) return;
      if (!(e.ctrlKey || e.metaKey || e.shiftKey || e.button === 1)) return;

      const anchor = (e.target as Element | null)?.closest('a');
      if (!anchor?.getAttribute('href')) return;
      if (anchor.target === '_blank') return;

      const url = new URL(anchor.href);
      const samePage =
        url.origin === window.location.origin &&
        url.pathname === window.location.pathname;
      if (!samePage || !url.hash) return;

      e.preventDefault();
      e.stopPropagation();
      anchor.dispatchEvent(
        new MouseEvent('click', { bubbles: true, cancelable: true, view: window })
      );
    };

    window.addEventListener('click', onClick, true);
    window.addEventListener('auxclick', onClick, true);
    return () => {
      window.removeEventListener('click', onClick, true);
      window.removeEventListener('auxclick', onClick, true);
    };
  }, []);
}
