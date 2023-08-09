import { open } from '@tauri-apps/plugin-shell';
import { ReactNode } from 'react';

export function A({ href, children }: { href: string; children?: ReactNode }) {
  return (
    <a
      href="javascript:void(0)"
      onClick={() => open(href).catch(() => window.open(href, '_blank'))}
      className="underline"
    >
      {children}
    </a>
  );
}
