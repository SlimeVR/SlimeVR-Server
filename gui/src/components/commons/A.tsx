import { openUrl } from '@tauri-apps/plugin-opener';
import { ReactNode } from 'react';

export function A({ href, children }: { href?: string; children?: ReactNode }) {
  return (
    <a
      href="javascript:void(0)"
      onClick={() =>
        href && openUrl(href).catch(() => window.open(href, '_blank'))
      }
      className="underline"
    >
      {children}
    </a>
  );
}
